package com.dibomart.dibomart;


import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.request.RequestOptions;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.glide.slider.library.slidertypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SplashScreen extends Activity {

    private static final int SPLASH_SHOW_TIME = 2000;

    //Prefrance
    private static PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prf = new PrefManager(SplashScreen.this);

        if (!prf.getString("s_key").equals("")) {
            getBannerImage();
            getPromoBanner();
        }

        new BackgroundSplashTask().execute();

    }

    /**
     * Async Task: can be used to load DB, images during which the splash screen
     * is shown to user
     */
    private class BackgroundSplashTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


                // Create a new boolean and preference and set it to true
                String isSignedin = prf.getString("pincode");

                if(!isSignedin.equalsIgnoreCase("")) {
                    //user signedin
                  //  ServiceNames.PRODUCTION_API = prf.getString("url");
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                } else {
                    //user not signedin
                    Intent i = new Intent(SplashScreen.this, PincodeActivity.class);
                    startActivity(i);
                }

            finish();
        }

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
