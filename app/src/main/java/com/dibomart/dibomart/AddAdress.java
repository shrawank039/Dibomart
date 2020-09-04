package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddAdress extends AppCompatActivity {

    EditText edtFirstName, edtLastName, edtAdd1, edtAdd2, edtCity, edtZip, edtCountry;
    private static PrefManager prf;
    private ProgressDialog pDialog;
    String url = "";

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

        String type = getIntent().getStringExtra("type");
        assert type != null;
        if (type.equals("0"))
            url = ServiceNames.ADDRESS;
        else
            url =ServiceNames.PAY_ADDRESS;


    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void add(View view) {
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

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, data,
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
                Toast.makeText(getApplicationContext(), "error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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