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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

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
}