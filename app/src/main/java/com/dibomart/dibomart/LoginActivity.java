package com.dibomart.dibomart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object

    private ArrayList<HashMap<String, String>> offersList;


    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    //user
    private static final String TAG_USERID = "customer_id";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "telephone";
    private static final String TAG_PASSWORD = "password";

    //balance
    private static final String TAG_USERBALANCE = "balance";

    //matchdetail
    private static final String TAG_WONAMOUNT = "wonamount";

    // products JSONArray
    private JSONArray jsonarray = null;

    //Prefrance
    private static PrefManager prf;

    //Textbox
    private EditText phone;
    private EditText password;
    private Button signup;
    private Button signin;
    private TextView resetnow;
    String session;

    private String success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        prf = new PrefManager(LoginActivity.this);

        // Hashmap for ListView
        offersList = new ArrayList<>();

        if (prf.getString("session").equals(""))
            createSession();

        phone = (EditText) findViewById(R.id.phone);
        password = (EditText) findViewById(R.id.password);
        resetnow = (TextView) findViewById(R.id.resetnow);

        signup = (Button) findViewById(R.id.registerFromLogin);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        signin = (Button) findViewById(R.id.signinbtn);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkdetails()) {
                    // Loading jsonarray in Background Thread
                    loginSubmit();
                }
            }
        });

        resetnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this,"Email us from your registed email id",Toast.LENGTH_LONG).show();
                // Loading offers in Background Thread
                Intent intent = new Intent(LoginActivity.this, MobileVerifyActivity.class);
                intent.putExtra("password", "password");
                startActivity(intent);
            }
        });
    }

    private boolean checkdetails() {

        if (phone.getText().toString().trim().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(phone.getText().toString().trim()).matches()) {
            Toast.makeText(LoginActivity.this, "Enter Value for E-mail", Toast.LENGTH_SHORT).show();
            phone.requestFocus();
        }  else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Enter Value for Password", Toast.LENGTH_SHORT).show();
            password.requestFocus();
            return false;
        }


        return true;
    }

    private void loginSubmit() {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data= new JSONObject();
        try {
            data.put("email", phone.getText().toString());
            data.put(TAG_PASSWORD, password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.USER_LOGIN, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();

                        success = jsonObject.optString("success");
                        if (success.equals("1")) {
                            prf.setString("session", session);
                            JSONObject data = jsonObject.optJSONObject("data");
                                // preference and set username for session
                                prf.setString(TAG_USERID, data.optString(TAG_USERID));
                                prf.setString(TAG_FIRSTNAME, data.optString(TAG_FIRSTNAME));
                                prf.setString(TAG_LASTNAME, data.optString(TAG_LASTNAME));
                                prf.setString(TAG_EMAIL, data.optString(TAG_EMAIL));
                                prf.setString(TAG_MOBILE, data.optString("telephone"));

                            startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();

                            Toast.makeText(LoginActivity.this,"Sign in done Succsessfully",Toast.LENGTH_LONG).show();

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
                headers.put("X-Oc-Session", session);
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

    private void createSession() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, Global.base_url+ServiceNames.SESSION, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONObject jsonObject = response.getJSONObject("data");
                                session = jsonObject.optString("session");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
