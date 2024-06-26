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
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.glide.slider.library.slidertypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SplashScreen extends Activity {

    private static final int SPLASH_SHOW_TIME = 500;
    private List<SubCategory> subCategoryLists = new ArrayList<>();

    //Prefrance
    private static PrefManager prf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        prf = new PrefManager(SplashScreen.this);

        if (prf.getString("base_url").equals(""))
            Global.base_url = "https://dibomart.in/";
        else
            Global.base_url = prf.getString("base_url");

        Global.ongoingList = new ArrayList();

        if (!prf.getString("s_key").equals("")) {
            getCategory();
        }else {
            new BackgroundSplashTask().execute();
        }

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
                Global.cartTotalPrice =prf.getInt("cart_price");
                Global.cartTotalItem = prf.getInt("cart_item");

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
                Request.Method.GET, Global.base_url+ServiceNames.BANNER_IMG+"/7", null,
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
                                        Global.bannerUrl.add(c.optString("link"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        new BackgroundSplashTask().execute();
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
    private void getCategory() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, Global.base_url+ServiceNames.ALL_CATEGORIES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                      //  Global.ongoingList.clear();

                        if (response.optString("success").equals("1")) {
                            JSONArray jsonarray = null;
                            try {
                                jsonarray = response.getJSONArray("data");

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    JSONObject c = null;
                                    try {
                                        c = jsonarray.getJSONObject(i);
                                        Category ongoing = new Category();
                                        subCategoryLists = new ArrayList<>();
                                        ongoing.setName(c.optString("name"));
                                        ongoing.setId(c.optString("category_id"));
                                        ongoing.setImage_url(c.optString("image"));

                                        JSONArray subArray = c.getJSONArray("categories");

                                        for (int a = -1; a < subArray.length(); a++) {
                                            SubCategory subCategory = new SubCategory();
                                            if (a==-1){
                                                subCategory.setName("All Products");
                                                subCategory.setCategory_id(c.optString("category_id"));
                                            }else {
                                                JSONObject subJson = subArray.getJSONObject(a);
                                                subCategory.setName(subJson.optString("name"));
                                                subCategory.setCategory_id(subJson.optString("category_id"));
                                            }
                                            subCategoryLists.add(subCategory);
                                            //  Log.d("subCat",ongoing.getName()+" "+subCategory.getName());
                                        }
                                        ongoing.setSubCategoryList(subCategoryLists);
                                        Global.ongoingList.add(ongoing);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getBannerImage();
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
