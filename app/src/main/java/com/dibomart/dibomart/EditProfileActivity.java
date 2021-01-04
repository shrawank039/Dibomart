package com.dibomart.dibomart;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "telephone";
    private static final String TAG_PASSWORD = "password";

    private static PrefManager prf;

    private TextInputEditText eMail;
    private String email;
    private String firstname;
    private TextInputEditText fname;
    private String lastname;
    private TextInputEditText lname;
    private TextInputEditText mNumber;
    private String mnumber;
    private TextInputEditText newPass;
    private Button resetPassButton;
    private TextInputEditText retypeNewPass;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        prf = new PrefManager(EditProfileActivity.this);

        fname = (TextInputEditText) findViewById(R.id.firstname);
        lname = (TextInputEditText) findViewById(R.id.lastname);
        eMail = (TextInputEditText) findViewById(R.id.email);
        mNumber = (TextInputEditText) findViewById(R.id.mobileNumber);
        saveButton = (Button) findViewById(R.id.saveBtn);
        newPass = (TextInputEditText) findViewById(R.id.newpass);
        retypeNewPass = (TextInputEditText) findViewById(R.id.retypeNewPass);
        resetPassButton = (Button) findViewById(R.id.changePassBtn);

        firstname = prf.getString(TAG_FIRSTNAME);
        lastname = prf.getString(TAG_LASTNAME);
        email = prf.getString(TAG_EMAIL);
        mnumber = prf.getString(TAG_MOBILE);

        fname.setText(firstname);
        lname.setText(lastname);
        eMail.setText(email);
        mNumber.setText(mnumber);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Objects.requireNonNull(fname.getText()).length() >0 && Objects.requireNonNull(lname.getText()).length()>0 &&
                        Objects.requireNonNull(mNumber.getText()).length() >9 && Objects.requireNonNull(eMail.getText()).length()>1) {
                   updateProfile();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }
            }
        });
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newPass.getText().length()>1 && retypeNewPass.getText().length()>1) {
                    if (newPass.getText().toString().equals(retypeNewPass.getText().toString())) {
                        updatePass();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "NewPassword And RetypePass is not Same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Please enter value for all field", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updatePass() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put(TAG_PASSWORD, newPass.getText().toString());
            data.put("confirm", newPass.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, Global.base_url+ServiceNames.CHANGE_PASS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Password Changed Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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

    private void updateProfile() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put(TAG_EMAIL, eMail.getText().toString());
            data.put(TAG_FIRSTNAME, fname.getText().toString());
            data.put(TAG_LASTNAME, lname.getText().toString());
            data.put(TAG_MOBILE, mNumber.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, Global.base_url+ServiceNames.EDIT_PROFILE, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        prf.setString(TAG_FIRSTNAME, fname.getText().toString());
                        prf.setString(TAG_LASTNAME, lname.getText().toString());
                        prf.setString(TAG_EMAIL, eMail.getText().toString());
                        prf.setString(TAG_MOBILE, mNumber.getText().toString());
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
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

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
