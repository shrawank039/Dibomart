package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.adapter.CategoryAdapter;
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCategories extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter mAdapter;
    private static PrefManager prf;
    private List<Category> subCategoryLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new CategoryAdapter(getApplicationContext(), subCategoryLists);

        prf = new PrefManager(this);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
       // recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // row click listenerMyDividerItemDecoration
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Category ongoing = Global.ongoingList.get(position);

                //  Toast.makeText(context, ongoing.getId(), Toast.LENGTH_SHORT).show();

                Intent in = new Intent(getApplicationContext(), ProductListActivity.class);
                in.putExtra("category_id", ongoing.getId());
                in.putExtra("index", position);
                startActivity(in);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getCategory();

    }

    private void getCategory() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.ALL_CATEGORIES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            JSONArray jsonarray = null;
                            try {
                                jsonarray = response.getJSONArray("data");

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    JSONObject c = null;
                                    try {
                                        c = jsonarray.getJSONObject(i);
                                        Category ongoing = new Category();
                                        ongoing.setName(c.optString("name"));
                                        ongoing.setId(c.optString("category_id"));
                                        ongoing.setImage_url(c.optString("image"));

                                        subCategoryLists.add(ongoing);
                                        mAdapter.notifyDataSetChanged();


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void searchClick(View view) {
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    public void cartClick(View view) {
        startActivity(new Intent(getApplicationContext(),CartActivity.class));
    }

}