package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.ProductListAdapter;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.ProductOption;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    EditText inputSearch;
    private static PrefManager prf;
    private List<ProductList> productLists = new ArrayList<>();
    private List<ProductOption> productOptionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        inputSearch = (EditText) findViewById(R.id.inputSearch);
        prf = new PrefManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        productLists = new ArrayList();
        mAdapter = new ProductListAdapter(getApplicationContext(), productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                getProductList(String.valueOf(cs));
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void getProductList(String getData) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.SEARCH_PRODUCT_LIST+getData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productLists.clear();
                        mAdapter.notifyDataSetChanged();
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                JSONArray jsonarray = null;
                                try {
                                    jsonarray = jsonObject.getJSONArray("data");

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            productOptionList = new ArrayList<>();
                                            ProductList productList = new ProductList();
                                            productList.setName(c.optString("name"));
                                            productList.setId(c.optString("id"));
                                            productList.setImage_url(c.optString("image"));
                                            productList.setPrice(c.optInt("price"));
                                            productList.setSpecial_price(c.optInt("special"));
                                            productList.setWeight(c.optString("weight"));
                                            productList.setItem_count(0);
                                            productList.setWeight_class(c.optString("weight_class"));
                                            productList.setProduct_id(c.optString("product_id"));
                                            productList.setDescription(c.optString("description"));

                                            JSONArray jsonArray = c.optJSONArray("options");
                                            assert jsonArray != null;
                                            JSONObject jsonObj = jsonArray.optJSONObject(0);
                                            productList.setProduct_option_id(jsonObj.optString("product_option_id"));

                                            JSONArray jsonArr = jsonObj.optJSONArray("option_value");
                                            assert jsonArr != null;
                                            for (int a = 0; a < jsonArr.length(); a++) {
                                                JSONObject jsonOb = jsonArr.optJSONObject(a);
                                                ProductOption productOption = new ProductOption();
                                                productOption.setName(jsonOb.optString("name"));
                                                productOption.setPrice(jsonOb.optInt("price"));
                                                productOption.setProduct_option_value_id(jsonOb.optString("product_option_value_id"));
                                                productOptionList.add(productOption);
                                            }
                                            productList.setProductOptions(productOptionList);
                                            productLists.add(productList);
                                            mAdapter.notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "03 error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}