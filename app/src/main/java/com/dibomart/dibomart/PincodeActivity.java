package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PincodeActivity extends AppCompatActivity {

    EditText pinEdt;
    private ProgressDialog pDialog;
    private static PrefManager prf;
    LinearLayout llnoService;
    private String TAG ="PincodeAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);
        llnoService = findViewById(R.id.ll_noservice);
        pinEdt = findViewById(R.id.edt_pincode);
        pinEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llnoService.setVisibility(View.GONE);
            }
        });
        prf = new PrefManager(PincodeActivity.this);
    }

    public void pinSubmit(View view) {
        final String pin= pinEdt.getText().toString().trim();

            pDialog = new ProgressDialog(PincodeActivity.this);
            pDialog.setMessage("Checking Service...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, ServiceNames.PINCODE+pin, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            pDialog.dismiss();
                            Log.d(TAG, "response : "+jsonObject.toString());
                            if (jsonObject.optString("service").equalsIgnoreCase("Available")) {

                                prf.setString("pincode", jsonObject.optString("pincode"));
                                prf.setString("url", jsonObject.optString("url"));
                                prf.setString("s_key", jsonObject.optString("s_key"));
                              //  ServiceNames.PRODUCTION_API = jsonObject.optString("url");

                                if (Global.banner.size()<1) {
                                    getBannerImage();
                                    getPromoBanner();
                                }

                                Intent intent = new Intent(PincodeActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {
                                llnoService.setVisibility(View.VISIBLE);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    Toast.makeText(PincodeActivity.this, "error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    15000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            stringRequest.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    private void getBannerImage() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.BANNER_IMG+"/7", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        Log.d("TAG", "image : "+c.optString("image"));
                                        Global.banner.add(c.optString("image"));

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

    private void getPromoBanner() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.BANNER_IMG+"/6", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        Log.d("TAG", "image : "+c.optString("image"));
                                        Global.promoBanner.add(c.optString("image"));

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
}