package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.adapter.PaymentMethodAdapter;
import com.dibomart.dibomart.model.PaymentMethodList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSuccessActivity extends AppCompatActivity {

    private static PrefManager prf;
    private ProgressDialog pDialog;
    private List<PaymentMethodList> paymentMethodLists;
    private RecyclerView recyclerView;
    private PaymentMethodAdapter mAdapter;
    TextView txtStatus, txtID;
    ImageView imgTick;
    String status ="0";
    LinearLayout llHistory, llContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        prf = new PrefManager(this);
        status = getIntent().getStringExtra("status");
        imgTick = findViewById(R.id.img);
        txtID = findViewById(R.id.txt_id);
        txtStatus = findViewById(R.id.txt_status);
        llHistory = findViewById(R.id.ll_history);
        llContinue = findViewById(R.id.ll_continue);

        llContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        llHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });
        if (status.equals("1")) {
            confirm();
        } else {
            imgTick.setBackgroundResource(R.drawable.error);
            txtStatus.setText("Order Failed!");
            llHistory.setVisibility(View.GONE);
        }

    }

    private void confirm() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.PUT, ServiceNames.CONFIRM, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);

                        if (jsonObject.optInt("success") == 1) {
                            try {
                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String a= jsonObject1.optString("order_id");
                            txtID.setText("ID - #"+a);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}