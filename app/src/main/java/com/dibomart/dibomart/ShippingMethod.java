package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.adapter.AddressListAdapter;
import com.dibomart.dibomart.adapter.ShippingMethodAdapter;
import com.dibomart.dibomart.model.AddressList;
import com.dibomart.dibomart.model.ShippingMethodList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShippingMethod extends AppCompatActivity {

    private static PrefManager prf;
    private ProgressDialog pDialog;
    private List<ShippingMethodList> shippingLists;
    private RecyclerView recyclerView;
    private ShippingMethodAdapter mAdapter;
    TextView name, address, pincode, txtChange;
    //  Button btnAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_method);

        prf = new PrefManager(this);
        shippingLists = new ArrayList<>();
        name = findViewById(R.id.txt_name);
        pincode = findViewById(R.id.txt_pincode);
        address = findViewById(R.id.txt_address);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        txtChange = findViewById(R.id.txt_change);

        mAdapter = new ShippingMethodAdapter(getApplicationContext(), shippingLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddressActivity.class));
                finish();
            }
        });
        if (!prf.getString("shipping_id").equals("")) {
            name.setText(prf.getString("shipping_name"));
            address.setText(prf.getString("shipping_address"));
            pincode.setText(prf.getString("shipping_postcode"));
            setShippingAdd(prf.getString("shipping_id"));
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShippingMethod.this);
                            builder.setTitle("No Default Address Found!");
                            builder.setMessage("You don't have any default shipping address selected. Please make any address to default to continue.")
                                    .setCancelable(false)
                                    .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent( getApplicationContext(), AddressActivity.class));
                                            finish();
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.setOnShowListener( new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                                }
                            });
                            alert.show();
        }
    }

    private void loadMethod() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, ServiceNames.SHIPPING_METHOD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    JSONArray jsonarray = jsonObject1.getJSONArray("shipping_methods");

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            JSONObject a = jsonarray.getJSONObject(i);
                                            JSONArray jsonArray = a.optJSONArray("quote");
                                            assert jsonArray != null;
                                            c = jsonArray.getJSONObject(0);
                                            ShippingMethodList shippingMethodList = new ShippingMethodList();
                                            shippingMethodList.setTitle(c.optString("title"));
                                            shippingMethodList.setCharges(c.optInt("cost"));
                                            shippingMethodList.setCode(c.optString("code"));

                                            shippingLists.add(shippingMethodList);
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
                pDialog.dismiss();
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

    private void setShippingAdd(String key) {

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.EXISTING_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (prf.getString("payment_id").equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ShippingMethod.this);
                            builder.setTitle("No Default Payment Address Found!");
                            builder.setMessage("You don't have any default payment address selected. Please make any address to default to continue.")
                                    .setCancelable(false)
                                    .setPositiveButton("SELECT", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            startActivity(new Intent( getApplicationContext(), AddressActivity.class));
                                            finish();
                                        }
                                    });
                            final AlertDialog alert = builder.create();
                            alert.setOnShowListener( new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface arg0) {
                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                                }
                            });
                            alert.show();
                        }else {
                            setPaymentAdd(prf.getString("payment_id"));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "1error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
    private void setPaymentAdd(String key) {

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.EXISTING_PAY_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        loadMethod();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "2error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void nextClick(View view) {
            startActivity(new Intent(getApplicationContext(), OrderConfirmActivity.class));
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}