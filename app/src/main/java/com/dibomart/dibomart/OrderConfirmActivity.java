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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.CartListAdapter;
import com.dibomart.dibomart.adapter.PaymentMethodAdapter;
import com.dibomart.dibomart.adapter.ShippingMethodAdapter;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.model.PaymentMethodList;
import com.dibomart.dibomart.model.ShippingMethodList;
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

public class OrderConfirmActivity extends AppCompatActivity {

    private static PrefManager prf;
    private ProgressDialog pDialog;
    private List<PaymentMethodList> paymentMethodLists;
    private RecyclerView recyclerView;
    private PaymentMethodAdapter mAdapter;
    TextView txtCoupon;
    EditText edtCoupon;
    LinearLayout llApply, llRemove, llDiscount;
    Button btnApply, btnRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        prf = new PrefManager(this);

        prf = new PrefManager(this);
        paymentMethodLists = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtCoupon = findViewById(R.id.txt_coupon);
        edtCoupon = findViewById(R.id.edt_coupon);
        llApply =  findViewById(R.id.ll_apply);
        llRemove = findViewById(R.id.ll_delete);
        llDiscount = findViewById(R.id.ll_discount);
        btnApply = findViewById(R.id.btn_apply);
        btnRemove = findViewById(R.id.btn_delete);

//        if (llDiscount.getVisibility() == View.VISIBLE){
//            llApply.setVisibility(View.GONE);
//            llRemove.setVisibility(View.VISIBLE);
//        }

        mAdapter = new PaymentMethodAdapter(getApplicationContext(), paymentMethodLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCoupon(edtCoupon.getText().toString().trim());
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCoupon();
            }
        });

        loadMethod();

        getProductList();

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
                                Global.cartTotalPrice = jsonObject1.optInt("total_raw");
                                try {
                                    JSONArray jsonarray = jsonObject1.getJSONArray("totals");

                                    if (jsonarray.length()>2) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(0);
                                            TextView subTotal = findViewById(R.id.txt_subtotal);
                                            String a = "Rs. " + c.optInt("value");
                                            subTotal.setText(a);
                                            c = jsonarray.getJSONObject(1);
                                            TextView discount = findViewById(R.id.txt_discount);
                                            String b = "Rs. " + c.optInt("value");
                                            discount.setText(b);
                                            llDiscount.setVisibility(View.VISIBLE);
                                            llApply.setVisibility(View.GONE);
                                            llRemove.setVisibility(View.VISIBLE);
                                            c = jsonarray.getJSONObject(2);
                                            TextView total = findViewById(R.id.txt_total);
                                            String d = "Rs. " + c.optInt("value");
                                            total.setText(d);
                                            TextView deliver = findViewById(R.id.txt_delivery);
                                            String de = "Rs. " + Global.shippingCharges;
                                            deliver.setText(de);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    else {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(0);
                                            TextView subTotal = findViewById(R.id.txt_subtotal);
                                            String a = "Rs. " + c.optInt("value");
                                            subTotal.setText(a);

                                            llDiscount.setVisibility(View.GONE);
                                            llApply.setVisibility(View.VISIBLE);
                                            llRemove.setVisibility(View.GONE);
                                            c = jsonarray.getJSONObject(1);
                                            TextView total = findViewById(R.id.txt_total);
                                            String d = "Rs. " + c.optInt("value");
                                            total.setText(d);
                                            TextView deliver = findViewById(R.id.txt_delivery);
                                            String de = "Rs. " + Global.shippingCharges;
                                            deliver.setText(de);

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

    private void loadMethod() {

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, ServiceNames.PAYMENT_METHOD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("TAG", "response : " + jsonObject);
//                        try {
//                            if (jsonObject.optInt("success") == 1) {
//                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
//                                try {
//                                    JSONArray jsonarray = jsonObject1.getJSONArray("payment_methods");
//
//                                    for (int i = 0; i < jsonarray.length(); i++) {
//                                        try {
//                                            JSONObject c = jsonarray.getJSONObject(i);
//                                            PaymentMethodList shippingMethodList = new PaymentMethodList();
//                                            shippingMethodList.setTitle(c.optString("title"));
//                                            shippingMethodList.setSort_order(c.optString("sort_order"));
//                                            shippingMethodList.setCode(c.optString("code"));
//
//                                            paymentMethodLists.add(shippingMethodList);
//                                            mAdapter.notifyDataSetChanged();
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "02error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void setCoupon(String key) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        JSONObject data= new JSONObject();
        try {
            data.put("coupon", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.COUPON, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        if (jsonObject.optInt("success")==1){
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("message");
                                String a = jsonArray.optString(0);
                                getProductList();
                                txtCoupon.setText(a);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("error");
                                Toast.makeText(OrderConfirmActivity.this, jsonArray.optString(0), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Warning: Coupon is either invalid, expired or reached its usage limit!", Toast.LENGTH_LONG).show();
            }
        }){
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

    private void deleteCoupon() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, ServiceNames.COUPON, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        if (jsonObject.optInt("success")==1){
                            Toast.makeText(OrderConfirmActivity.this, "Coupon removed successfully.", Toast.LENGTH_SHORT).show();
                                getProductList();

                        } else {
                            try {
                                JSONArray jsonArray = jsonObject.getJSONArray("error");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
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

    private void pay() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.PAY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),PaymentWebViewActivity.class)
                                .putExtra("data", response));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void confirm() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.CONFIRM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);

                        if (jsonObject.optInt("success") == 1) {
                            pay();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void confirmClick(View view) {
        confirm();
    }
}