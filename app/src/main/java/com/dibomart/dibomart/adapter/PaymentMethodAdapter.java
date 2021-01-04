package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.PaymentMethodList;
import com.dibomart.dibomart.model.ShippingMethodList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<PaymentMethodList> moviesList;
    private int selectedPosition = 0;
    private static PrefManager prf;
   // private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView charges;
        final CheckBox checkBox;

        MyViewHolder(View view) {
            super(view);

            prf = new PrefManager(ctx);
            name = view.findViewById(R.id.title);
            charges = view.findViewById(R.id.txt_charges);
            checkBox = view.findViewById(R.id.radio_btn);

        }
    }

    public PaymentMethodAdapter(Context context, List<PaymentMethodList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;

       // pageViewModel = ViewModelProviders.of((FragmentActivity) context).get(PageViewModel.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_payment_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PaymentMethodList ongoing = moviesList.get(position);

        holder.name.setText(ongoing.getTitle());

        if(selectedPosition == position){
            holder.checkBox.setChecked(true);
            setMethod(ongoing.getCode());
        }
        else{
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
              //  Global.shippingCharges = ongoing.getCharges();
                notifyDataSetChanged();
            }
        });
    }

    private void setMethod(String key) {
        JSONObject data= new JSONObject();
        try {
            data.put("payment_method", key);
            data.put("comment", "");
            data.put("agree","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.PAYMENT_METHOD, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //     pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(ctx, err, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
