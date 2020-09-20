package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.adapter.ReturnHistoryAdapter;
import com.dibomart.dibomart.model.ReturnHistoryList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnDetailsActivity extends AppCompatActivity {

    String return_id;
    private static PrefManager prf;
    private ProgressDialog pDialog;
    private TextView order_id;
    private TextView product;
    private TextView quantity;
    private TextView date_ordered;
    private TextView reason;
    private TextView opened;
    private TextView date_return;
    private TextView name;
    private TextView email;
    private TextView phone;
    private TextView comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_details);

        prf = new PrefManager(this);
        return_id = getIntent().getStringExtra("return_id");

        order_id = findViewById(R.id.id);
        product = findViewById(R.id.product);
        quantity = findViewById(R.id.quantity);
        date_ordered = findViewById(R.id.date_ordered);
        reason = findViewById(R.id.reason);
        opened = findViewById(R.id.opened);
        date_return = findViewById(R.id.date_return);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        comment = findViewById(R.id.comment);

        getReturnDetails();

    }

    private void getReturnDetails() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, ServiceNames.RETURN+return_id,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject data = jsonObject.getJSONObject("data");

                                order_id.setText("#"+data.optString("order_id"));
                                product.setText(data.optString("product"));
                                quantity.setText(data.optString("quantity"));
                                date_ordered.setText(data.optString("date_ordered"));
                                reason.setText(data.optString("reason"));
                                String namee= data.optString("firstname")+" "+data.optString("lastname");
                                opened.setText(data.optString("opened"));
                                date_return.setText(data.optString("date_added"));
                                name.setText(namee);
                                email.setText(data.optString("email"));
                                phone.setText(data.optString("telephone"));
                                if (data.optString("comment").equals(""))
                                    comment.setText("N/A");
                                else
                                comment.setText(data.optString("comment"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "03 error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}