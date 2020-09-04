package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.AddressList;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<AddressList> moviesList;
    int priceA;
    int priceB;
    private static PrefManager prf;
   // private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView address;
        final TextView pincode;
        final TextView defaultTxt, txtMenu;

        MyViewHolder(View view) {
            super(view);

            prf = new PrefManager(ctx);
            name = view.findViewById(R.id.txt_name);
            address = view.findViewById(R.id.txt_address);
            pincode = view.findViewById(R.id.txt_pincode);
            defaultTxt = view.findViewById(R.id.txt_default);
            txtMenu = view.findViewById(R.id.textViewOptions);

        }
    }

    public AddressListAdapter(Context context, List<AddressList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;

       // pageViewModel = ViewModelProviders.of((FragmentActivity) context).get(PageViewModel.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_address_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AddressList ongoing = moviesList.get(position);

        holder.name.setText(ongoing.getFirstName()+" "+ ongoing.getLastName());
        holder.address.setText(ongoing.getAdd1()+", "+ongoing.getCity());
        holder.pincode.setText(ongoing.getPincode());
        if (prf.getString("shipping_id").equals(ongoing.getAddressId()))

            holder.defaultTxt.setVisibility(View.VISIBLE);
        else
            holder.defaultTxt.setVisibility(View.GONE);


        holder.txtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(ctx, holder.txtMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_address);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit:
                                //handle menu1 click
                                return true;
                            case R.id.delete:
                                moviesList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, moviesList.size());
                                deleteItem(ongoing.getAddressId());
                                return true;
                            case R.id.make_default:
                                prf.setString("shipping_id",ongoing.getAddressId());
                                prf.setString("shipping_name",ongoing.getFirstName()+" "+ongoing.getLastName());
                                prf.setString("shipping_address",ongoing.getAdd1());
                                prf.setString("shipping_postcode",ongoing.getPincode());
                                holder.defaultTxt.setVisibility(View.VISIBLE);
                                makeDefault(ongoing.getAddressId());
                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    private void deleteItem(final String key) {

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, ServiceNames.DELETE_ADDRESS+key, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                    if (prf.getString("shipping_id").equals(key)){
                        prf.setString("shipping_id","");
                        prf.setString("shipping_name","");
                        prf.setString("shipping_address","");
                        prf.setString("shipping_postcode","");
                    }
                        if (prf.getString("payment_id").equals(key)){
                            prf.setString("payment_id","");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             // pDialog.dismiss();
                Toast.makeText(ctx, "error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void makeDefault(String key) {

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.EXISTING_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                   //     pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //   pDialog.dismiss();
                Toast.makeText(ctx, "error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
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
