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
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PincodeActivity extends AppCompatActivity {

    EditText pinEdt;
    private ProgressDialog pDialog;
    private static PrefManager prf;
    LinearLayout llnoService;
    private String TAG ="PincodeAct";
    private List<SubCategory> subCategoryLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pincode);
        llnoService = findViewById(R.id.ll_noservice);
        pinEdt = findViewById(R.id.edt_pincode);
        Global.ongoingList = new ArrayList();
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
                                    getCategory();
                                }else {
                                    Intent intent = new Intent(PincodeActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }

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
    private void getCategory() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.ALL_CATEGORIES, null,
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
                        Intent intent = new Intent(PincodeActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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