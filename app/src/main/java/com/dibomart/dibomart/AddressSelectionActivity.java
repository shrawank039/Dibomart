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
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.adapter.AddressListAdapter;
import com.dibomart.dibomart.adapter.PayAddressListAdapter;
import com.dibomart.dibomart.adapter.cartAddressListAdapter;
import com.dibomart.dibomart.adapter.cartPayAddressListAdapter;
import com.dibomart.dibomart.fragment.PaymentAddressFragment;
import com.dibomart.dibomart.model.AddressList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressSelectionActivity extends AppCompatActivity {

    private static PrefManager prf;
    private ProgressDialog pDialog;
    private List<AddressList> addressLists;
    private List<AddressList> payAddressLists;
    private RecyclerView rv_shipping,rv_payment;
    private cartAddressListAdapter mAdapter;
    private cartPayAddressListAdapter payAdapter;
    private LinearLayout llemptyadd, llhasadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_selection);

        prf = new PrefManager(this);
        payAddressLists = new ArrayList<>();
        rv_shipping =  findViewById(R.id.rv_shipping);
        rv_payment =  findViewById(R.id.rv_payment);
        llemptyadd = findViewById(R.id.ll_emptyadd);
        llhasadd = findViewById(R.id.ll_hasadd);


        rv_shipping.setHasFixedSize(true);
        rv_payment.setHasFixedSize(true);

        addressLists = new ArrayList<>();
        mAdapter = new cartAddressListAdapter(AddressSelectionActivity.this, addressLists);
        payAdapter = new cartPayAddressListAdapter(AddressSelectionActivity.this, addressLists);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager payLayoutManager = new LinearLayoutManager(this);
        rv_shipping.setLayoutManager(mLayoutManager);
        rv_shipping.setItemAnimator(new DefaultItemAnimator());
        rv_shipping.setAdapter(mAdapter);
        rv_payment.setLayoutManager(payLayoutManager);
        rv_payment.setItemAnimator(new DefaultItemAnimator());
        rv_payment.setAdapter(payAdapter);

    }

    private void loadPayAddress() {
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Global.base_url+ServiceNames.PAY_ADDRESS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    String defaultAddress = jsonObject1.optString("address_id");
                                    JSONArray jsonarray = jsonObject1.getJSONArray("addresses");

                                    Global.addressCount = jsonarray.length();

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            AddressList addressList = new AddressList();
                                            addressList.setFirstName(c.optString("firstname"));
                                            addressList.setLastName(c.optString("lastname"));
                                            addressList.setAdd1(c.optString("address_1"));
                                            addressList.setAdd2(c.optString("address_2"));
                                            addressList.setAddressId(c.optString("address_id"));
                                            addressList.setPincode(c.optString("postcode"));
                                            addressList.setCity(c.optString("city"));
                                            addressList.setCountry(c.optString("country"));
                                            addressList.setZoneId(c.optString("zone_id"));

                                            payAddressLists.add(addressList);
                                            payAdapter.notifyDataSetChanged();

                                            if (Global.addressCount == 1){
                                                makeDefault(c.optString("address_id"));
                                            }
                                            if (Global.addressCount ==0){
                                                startActivity(new Intent(getApplicationContext(), AddAdress.class));
                                                finish();
                                            }

                                            if (defaultAddress.equals(c.optString("address_id"))){
                                                prf.setString("payment_id",defaultAddress);
                                            }

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
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
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


    private void loadAddress() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        addressLists.clear();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Global.base_url+ServiceNames.ADDRESS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    String defaultAddress = jsonObject1.optString("address_id");
                                    JSONArray jsonarray = jsonObject1.getJSONArray("addresses");

                                    Global.addressCount = jsonarray.length();
                                    if (Global.addressCount ==0){
                                        llemptyadd.setVisibility(View.VISIBLE);
                                        llhasadd.setVisibility(View.GONE);
                                    }

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            AddressList addressList = new AddressList();
                                            addressList.setFirstName(c.optString("firstname"));
                                            addressList.setLastName(c.optString("lastname"));
                                            addressList.setAdd1(c.optString("address_1"));
                                            addressList.setAdd2(c.optString("address_2"));
                                            addressList.setAddressId(c.optString("address_id"));
                                            addressList.setPincode(c.optString("postcode"));
                                            addressList.setCity(c.optString("city"));
                                            addressList.setCountry(c.optString("country"));
                                            addressList.setZoneId(c.optString("zone_id"));

                                            addressLists.add(addressList);
                                            mAdapter.notifyDataSetChanged();

                                            if (Global.addressCount == 1 && !defaultAddress.equals(c.optString("address_id"))){
                                                makeDefaultShip(c.optString("address_id"));
                                            }

                                            if (defaultAddress.equals(c.optString("address_id"))){
                                                String name = c.optString("firstname")+" "+c.optString("lastname");
                                                prf.setString("shipping_id",defaultAddress);
                                                prf.setString("shipping_name",name);
                                                prf.setString("shipping_address",c.optString("address_1"));
                                                prf.setString("shipping_postcode",c.optString("postcode"));
                                            }

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
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
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

    private void makeDefault(final String key) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Setting Payment Address...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.EXISTING_PAY_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        loadAddress();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   pDialog.dismiss();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
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

    private void makeDefaultShip(final String key) {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Setting Shipping Address...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.EXISTING_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        makeDefault(key);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                   pDialog.dismiss();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
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

    @Override
    protected void onResume() {
        super.onResume();
        loadAddress();
    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void nextClick(View view) {
        if (!prf.getString("shipping_id").equals(""))
        startActivity(new Intent(getApplicationContext(), ShippingMethod.class));
        else
            Toast.makeText(this, "Please select any address first", Toast.LENGTH_SHORT).show();
    }

    public void addAddress(View view) {
        startActivity(new Intent(getApplicationContext(), AddressActivity.class));
    }
}