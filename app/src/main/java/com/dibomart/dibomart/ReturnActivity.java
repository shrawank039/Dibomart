package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.model.OrderedProductList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ReturnActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static PrefManager prf;
    private String returnReasonId="1";
    private String packetOpened="1";
    EditText edtMessage;
    private boolean pactStatus = false;
    private boolean returnReason = false;
    private static final String TAG_USERID = "customer_id";
    private static final String TAG_FIRSTNAME = "firstname";
    private static final String TAG_LASTNAME = "lastname";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_MOBILE = "telephone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);

        prf = new PrefManager(this);
        edtMessage = findViewById(R.id.edt_message);

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked)
            returnReason = true;
        switch(view.getId()) {
            case R.id.one:
                if (checked)
                    returnReasonId = "1";
                    break;
            case R.id.two:
                if (checked)
                    returnReasonId = "2";
                    break;
            case R.id.three:
                if (checked)
                    returnReasonId = "3";
                break;
            case R.id.four:
                if (checked)
                    returnReasonId = "4";
                break;
            case R.id.five:
                if (checked)
                    returnReasonId = "5";
                break;
        }
    }

    public void onRadioStatusClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        if (checked)
            pactStatus = true;
        switch(view.getId()) {
            case R.id.open:
                if (checked)
                    packetOpened = "1";
                break;
            case R.id.packed:
                if (checked)
                    packetOpened = "0";
                break;

        }
    }

    public void makeReturnReq(View view) {
        if (pactStatus){
            if (returnReason){
                submitRequest();
            }else {
                Toast.makeText(this, "Please select any reason to return", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(this, "Please select packet status", Toast.LENGTH_LONG).show();
        }
    }

    private void submitRequest() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        OrderedProductList orderedProductList = Global.orderedProductList;

        JSONObject data= new JSONObject();
        try {
            data.put("firstname", orderedProductList.getFirstname());
            data.put( "lastname", orderedProductList.getLastname());
            data.put("email", orderedProductList.getEmail());
            data.put("telephone", orderedProductList.getTelephone());
            data.put("date_ordered", orderedProductList.getDate());
            data.put("order_id", orderedProductList.getOrder_id());
            data.put("product", orderedProductList.getPrduct());
            data.put("model", orderedProductList.getModel());
            data.put("return_reason_id", returnReasonId);
            data.put("opened", packetOpened);
            data.put("quantity", orderedProductList.getQuantity());
            data.put("comment", edtMessage.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.RETURN, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        String success = jsonObject.optString("success");
                        if (success.equals("1")) {
                            Toast.makeText(ReturnActivity.this, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ReturnHistoryActivity.class));
                            finish();
                        }else {
                            Toast.makeText(ReturnActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
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

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}