package com.dibomart.dibomart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.CartListAdapter;
import com.dibomart.dibomart.model.CartList;
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

public class CartActivity extends AppCompatActivity {

    private static PrefManager prf;
    private List<CartList> productLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CartListAdapter mAdapter;
    private PageViewModel pageViewModel;
    private ProgressDialog pDialog;
    private LinearLayout llemptycart, llhascart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        prf = new PrefManager(this);

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llemptycart = findViewById(R.id.ll_emptycart);
        llhascart = findViewById(R.id.ll_hascart);

        productLists = new ArrayList();
        mAdapter = new CartListAdapter(getApplicationContext(), productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getProductList();

        pageViewModel.getText().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                TextView item = findViewById(R.id.txt_total_price);
                TextView price = findViewById(R.id.txt_total);
                String a = "Rs. " + s;
                String b = "Total Rs. " + s;
                item.setText(a);
                price.setText(b);
            }
        });
        pageViewModel.getItemText().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                if (s == 0) {
                    llhascart.setVisibility(View.GONE);
                    llemptycart.setVisibility(View.VISIBLE);
                }
                TextView item = findViewById(R.id.txt_item);
                String a = "Item " + s;
                item.setText(a);
            }
        });

    }

    private void getProductList() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.CART,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    JSONArray jsonarray = jsonObject1.getJSONArray("products");
                                    JSONArray jsonarrayy = jsonObject1.getJSONArray("totals");

                                    JSONObject m = jsonarrayy.getJSONObject(0);
                                    Global.cartTotalPrice = m.optInt("value");
                                    PageViewModel.setIndex(Global.cartTotalPrice);
                                    prf.setInt("cart_price",Global.cartTotalPrice);

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            JSONArray optionArray = c.getJSONArray("option");
                                            JSONObject optionObj = optionArray.getJSONObject(0);
                                            CartList productList = new CartList();
                                            productList.setWeight(optionObj.optString("value"));
                                            productList.setName(c.optString("name"));
                                            productList.setKey(c.optString("key"));
                                            productList.setImage_url(c.optString("thumb"));
                                            productList.setPrice(c.optInt("price_raw"));
                                            productList.setTotal_price(c.optInt("total_raw"));
                                            productList.setQuantity(c.optString("quantity"));
                                            productList.setItem_count(Integer.parseInt(c.optString("quantity")));
                                            productList.setProduct_id(c.optString("product_id"));

                                            productLists.add(productList);
                                            mAdapter.notifyDataSetChanged();
                                            if (productLists.size()>0)
                                                llhascart.setVisibility(View.VISIBLE);

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
                        if (productLists.size() == 0) {
                            llhascart.setVisibility(View.GONE);
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

    public void nextClick(View view) {
        startActivity(new Intent(getApplicationContext(), ShippingMethod.class));
    }

    public void homeGo(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}