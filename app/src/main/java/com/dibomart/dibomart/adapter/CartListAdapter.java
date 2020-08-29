package com.dibomart.dibomart.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.ProductOption;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<CartList> moviesList;
    int priceA;
    int priceB;
    private static PrefManager prf;
    private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView quantity;
        final TextView price;
        final TextView special_price;
        final TextView item_count;
        final ImageView productImg;
        final ImageView favImg,imgAdd,imgLess;

        MyViewHolder(View view) {
            super(view);

            prf = new PrefManager(ctx);
            title = view.findViewById(R.id.title);
            quantity = view.findViewById(R.id.txt_quantity);
            price = view.findViewById(R.id.txt_mrp);
            special_price = view.findViewById(R.id.txt_price);
            productImg = view.findViewById(R.id.product_image);
            favImg = view.findViewById(R.id.img_fav);
            imgAdd = view.findViewById(R.id.img_add);
            imgLess = view.findViewById(R.id.img_less);
            item_count = view.findViewById(R.id.txt_itemcount);

        }
    }

    public CartListAdapter(Context context, List<CartList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CartList ongoing = moviesList.get(position);

        priceA = ongoing.getPrice();
        priceB = ongoing.getTotal_price();
        holder.title.setText(ongoing.getName());
      //  String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
      //  holder.quantity.setText(a);
        holder.item_count.setText(ongoing.getQuantity());
//        if (!ongoing.getPrice().equals(""))
//        holder.price.setText("\u20B9"+ongoing.getPrice());
//        else
            holder.price.setVisibility(View.GONE);
        holder.special_price.setText("\u20B9"+ongoing.getTotal_price());
        Glide.with(ctx)
                .load(ongoing.getImage_url())
                .placeholder(R.drawable.placeholder)
                .into(holder.productImg);

        holder.productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), ProductDetailsActivity.class)
                .putExtra("product_id", ongoing));
            }
        });

        holder.favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // holder.favImg.setImageDrawable(getResources().getDrawable(R.drawable.heart_selected));
            }
        });
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ctx, "add", Toast.LENGTH_SHORT).show();
                ongoing.setItem_count(ongoing.getItem_count()+1);
                ongoing.setQuantity(String.valueOf(ongoing.getItem_count()));
                int b = priceA*ongoing.getItem_count();
                ongoing.setTotal_price(b);
              //  ongoing.setSpecial_price(String.valueOf(c));
                //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
//                if (!ongoing.getPrice().equals(""))
//                    holder.price.setText("\u20B9"+ongoing.getPrice());
//                else
                    holder.price.setVisibility(View.GONE);
                 holder.special_price.setText("\u20B9" + ongoing.getTotal_price());
                holder.item_count.setText(ongoing.getQuantity());

                 //   addToCart(ongoing.getKey(), String.valueOf(ongoing.getItem_count()), 2);
            }
        });
        holder.imgLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ongoing.getItem_count()>0) {
                    ongoing.setItem_count(ongoing.getItem_count() - 1);
                    ongoing.setQuantity(String.valueOf(ongoing.getItem_count()));
                    //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
                    ongoing.setItem_count(ongoing.getItem_count()-1);
                    int b = priceA*ongoing.getItem_count();
                    ongoing.setTotal_price(b);
                  //  ongoing.setSpecial_price(String.valueOf(c));
                    //String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
//                    if (!ongoing.getPrice().equals(""))
//                        holder.price.setText("\u20B9"+ongoing.getPrice());
//                    else
                        holder.price.setVisibility(View.GONE);
                     holder.special_price.setText("\u20B9"+ongoing.getTotal_price());
                    holder.item_count.setText(ongoing.getQuantity());
                  //  ProductOption productOption = ongoing.getProductOptions().get(0);
                 //       addToCart(ongoing.getKey(), String.valueOf(ongoing.getItem_count()), 2);
                }
                else
                    Toast.makeText(ctx, "Minimum Quantity Selected", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void addToCart(String key, String item_count, int reqType) {
        pDialog = new ProgressDialog(ctx);
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JSONObject data= new JSONObject();
        try {
            data.put("key", key);
            data.put("quantity", item_count);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(reqType, ServiceNames.CART, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();

                      //  Toast.makeText(ctx,"Item Added to Cart",Toast.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
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
