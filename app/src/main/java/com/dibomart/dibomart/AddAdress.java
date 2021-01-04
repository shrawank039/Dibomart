package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAdress extends AppCompatActivity {

    EditText edtFirstName, edtLastName, edtAdd1, edtAdd2, edtCity, edtZip, edtCountry;
    private static PrefManager prf;
    private ProgressDialog pDialog;
    String url = Global.base_url+ServiceNames.DELETE_ADDRESS;
    String address_id, zone_id;
    Button addAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_adress);
        prf = new PrefManager(this);
        edtFirstName = findViewById(R.id.edt_firstName);
        edtLastName = findViewById(R.id.edt_lastname);
        edtAdd1 = findViewById(R.id.edt_address_one);
        edtAdd2 = findViewById(R.id.edt_add_two);
        edtCity = findViewById(R.id.edt_city);
        edtZip = findViewById(R.id.edt_postal);
        edtCountry = findViewById(R.id.edt_country);

        edtZip.setText(prf.getString("pincode"));

        addAddress = findViewById(R.id.btn_add_address);

        if (!Objects.equals(getIntent().getStringExtra("address_id"), "new")){

            address_id = getIntent().getStringExtra("address_id");
            String firstname = getIntent().getStringExtra("firstname");
            String lastname = getIntent().getStringExtra("lastname");
            String address_1 = getIntent().getStringExtra("address_1");
            String address_2 = getIntent().getStringExtra("address_2");
            String postcode = getIntent().getStringExtra("postcode");
            String city = getIntent().getStringExtra("city");
            zone_id = getIntent().getStringExtra("zone_id");
            String country_id = getIntent().getStringExtra("country_id");

            edtFirstName.setText(firstname);
            edtLastName.setText(lastname);
            edtAdd1.setText(address_1);
            edtAdd2.setText(address_2);
            edtCity.setText(city);
            edtZip.setText(postcode);
            edtCountry.setText(country_id);
            addAddress.setText("UPDATE ADDRESS");

        }

        final String type = getIntent().getStringExtra("type");
        assert type != null;
//        if (type.equals("0"))
//            url = ServiceNames.ADDRESS;
//        else
//            url =ServiceNames.PAY_ADDRESS;

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("2")){
                    update();
                    Toast.makeText(AddAdress.this, "updating address", Toast.LENGTH_SHORT).show();
                }else {
                    add();
                }
            }
        });

    }

    public void backPress(View view) {
        startActivity(new Intent(getApplicationContext(),AddressActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),AddressActivity.class));
        finish();
    }

    public void add() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put("firstname",edtFirstName.getText().toString().trim());
            data.put("lastname",edtLastName.getText().toString().trim());
            data.put("city",edtCity.getText().toString().trim());
            data.put("address_1",edtAdd1.getText().toString().trim());
            data.put("address_2",edtAdd2.getText().toString().trim());
            data.put("country", edtCountry.getText().toString().trim());
            data.put("country_id","81");
            data.put("postcode",edtZip.getText().toString().trim());
            data.put("zone_id","1506");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.UPDATE_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                             pDialog.dismiss();
                             startActivity(new Intent(getApplicationContext(),AddressActivity.class));
                             finish();
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

    public void update() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data = new JSONObject();
        try {
            data.put("firstname",edtFirstName.getText().toString().trim());
            data.put("lastname",edtLastName.getText().toString().trim());
            data.put("city",edtCity.getText().toString().trim());
            data.put("address_1",edtAdd1.getText().toString().trim());
            data.put("address_2",edtAdd2.getText().toString().trim());
            data.put("country_id",edtCountry.getText().toString().trim());
            data.put("postcode",edtZip.getText().toString().trim());
            data.put("zone_id","1506");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, Global.base_url+ServiceNames.UPDATE_ADDRESS+address_id, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(),AddressActivity.class));
                        finish();
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
}