package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.OrderHistoryAdapter;
import com.dibomart.dibomart.adapter.ReturnHistoryAdapter;
import com.dibomart.dibomart.model.OrderList;
import com.dibomart.dibomart.model.ReturnHistoryList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnHistoryActivity extends AppCompatActivity {

    private static PrefManager prf;
    private List<ReturnHistoryList> productLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private ReturnHistoryAdapter mAdapter;
    private ProgressDialog pDialog;
    private LinearLayout llemptycart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_history);

        prf = new PrefManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llemptycart = findViewById(R.id.ll_emptycart);
        productLists = new ArrayList();
        mAdapter = new ReturnHistoryAdapter(getApplicationContext(), productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getOrderList();
    }

    private void getOrderList() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.RETURN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        ReturnHistoryList productList = new ReturnHistoryList();
                                        productList.setOrder_id(c.optString("order_id"));
                                        productList.setReturn_id(c.optString("return_id"));
                                        productList.setName(c.optString("name"));
                                        productList.setDate(c.optString("date_added"));
                                        productList.setStatus(c.optString("status"));

                                        productLists.add(productList);
                                        mAdapter.notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (productLists.size() == 0) {
                            llemptycart.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "03 error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                headers.put("X-Oc-Session", prf.getString("session"));
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        stringRequest.setShouldCache(false);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    public void homeGo(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}