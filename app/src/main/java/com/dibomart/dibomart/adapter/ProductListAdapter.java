package com.dibomart.dibomart.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.LoginActivity;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.ProductOption;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    private final Context ctx;
    private final List<ProductList> moviesList;
    int priceA = 0;
    int priceB=  0;

    int posi = 0;
    List<String> spinnerArray;
    private static PrefManager prf;
    private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final Spinner quantity;
        final TextView price;
        final TextView special_price;
        final TextView item_count;
        final ImageView productImg;
        final ImageView favImg,imgAdd,imgLess;

        MyViewHolder(View view) {
            super(view);

            prf = new PrefManager(ctx);
            title = view.findViewById(R.id.title);
            quantity = view.findViewById(R.id.spinner);
            price = view.findViewById(R.id.txt_mrp);
            special_price = view.findViewById(R.id.txt_price);
            productImg = view.findViewById(R.id.product_image);
            favImg = view.findViewById(R.id.img_fav);
            imgAdd = view.findViewById(R.id.img_add);
            imgLess = view.findViewById(R.id.img_less);
            item_count = view.findViewById(R.id.txt_itemcount);
            spinnerArray =  new ArrayList<>();

        }
    }

    public ProductListAdapter(Context context, List<ProductList> moviesList) {
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductList ongoing = moviesList.get(position);

        holder.title.setText(ongoing.getName());
        final int[] price = {0};
        final int[] specialPrice = {0};

        for (int i = 0; i < ongoing.getProductOptions().size(); i++) {
            spinnerArray.add(" Quantity - "+ongoing.getProductOptions().get(i).getName());
        }

        ArrayAdapter aa = new ArrayAdapter(ctx,R.layout.spinner_quantity,spinnerArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        holder.quantity.setAdapter(aa);
      //  holder.quantity.setSelection(position);

        holder.quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //    Toast.makeText(ctx, String.valueOf(ongoing.getProductOptions().get(i).getPrice()), Toast.LENGTH_SHORT).show();
                holder.item_count.setText("0");
                ongoing.setItem_count(0);
                price[0] = ongoing.getPrice()+ongoing.getProductOptions().get(i).getPrice();
                specialPrice[0] = ongoing.getSpecial_price()+ongoing.getProductOptions().get(i).getPrice();

                posi = i;
                if (ongoing.getSpecial_price()==0){
                    holder.special_price.setText("\u20B9"+ price[0]);
                    holder.price.setVisibility(View.GONE);
                }
                else{
                    holder.price.setText("\u20B9"+ price[0]);
                    holder.special_price.setText("\u20B9"+ specialPrice[0]);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.item_count.setText(String.valueOf(ongoing.getItem_count()));

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
               // Toast.makeText(ctx, ongoing.getName(), Toast.LENGTH_SHORT).show();
                if (prf.getString("session").equals("")){
                    ctx.startActivity(new Intent(ctx.getApplicationContext(), LoginActivity.class));
                    }else {
                    priceA = price[0];
                    priceB = specialPrice[0];
                    ongoing.setItem_count(ongoing.getItem_count() + 1);
                    if (ongoing.getItem_count()==1){
                    Global.cartTotalItem = prf.getInt("cart_item")+1;
                    prf.setInt("cart_item",Global.cartTotalItem);
                    PageViewModel.setitemIndex(Global.cartTotalItem);
                    }
                    int b = priceA * ongoing.getItem_count();
                    int c = priceB * ongoing.getItem_count();
                    if (ongoing.getSpecial_price()==0) {
                        holder.price.setVisibility(View.GONE);
                        holder.special_price.setText("\u20B9" + b);
                        Global.cartTotalPrice = Global.cartTotalPrice+priceA;
                        PageViewModel.setIndex(Global.cartTotalPrice);
                        prf.setInt("cart_price",Global.cartTotalPrice);
                    }else {
                        Global.cartTotalPrice = Global.cartTotalPrice+priceB;
                        PageViewModel.setIndex(Global.cartTotalPrice);
                        prf.setInt("cart_price",Global.cartTotalPrice);
                        holder.price.setText("\u20B9" + b);
                        holder.special_price.setText("\u20B9" + c);
                    }
                    holder.item_count.setText(String.valueOf(ongoing.getItem_count()));
                    ProductOption productOption = ongoing.getProductOptions().get(posi);
               //     Toast.makeText(ctx, " option - "+ongoing.getProduct_option_id()+" value - "+productOption.getProduct_option_value_id(), Toast.LENGTH_LONG).show();
                    addToCart(ongoing.getProduct_option_id(), productOption.getProduct_option_value_id(),
                            ongoing.getProduct_id(), "1");
                }
            }
        });
        holder.imgLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ongoing.getItem_count()>1) {
                    ongoing.setItem_count(ongoing.getItem_count() - 1);
                    priceA = price[0];//Global.ongoingPrice;
                    priceB = specialPrice[0];//Global.ongoingSpecialPrice;
                    int b = priceA*ongoing.getItem_count();
                    int c = priceB*ongoing.getItem_count();
                    if (ongoing.getSpecial_price()==0) {
                        holder.price.setVisibility(View.GONE);
                        holder.special_price.setText("\u20B9" + b);
                        Global.cartTotalPrice = Global.cartTotalPrice-priceA;
                        PageViewModel.setIndex(Global.cartTotalPrice);
                        prf.setInt("cart_price",Global.cartTotalPrice);
                    }else {
                        Global.cartTotalPrice = Global.cartTotalPrice-priceB;
                        PageViewModel.setIndex(Global.cartTotalPrice);
                        prf.setInt("cart_price",Global.cartTotalPrice);
                        holder.price.setText("\u20B9" + b);
                        holder.special_price.setText("\u20B9" + c);
                    }

                    holder.item_count.setText(String.valueOf(ongoing.getItem_count()));
                    ProductOption productOption = ongoing.getProductOptions().get(posi);
               //     Toast.makeText(ctx, " option - "+ongoing.getProduct_option_id()+" value - "+productOption.getProduct_option_value_id(), Toast.LENGTH_LONG).show();
                        addToCart(ongoing.getProduct_option_id(),productOption.getProduct_option_value_id(),
                                ongoing.getProduct_id(), "-1");
                }

            }
        });
    }

    private void addToCart(String product_option_id, String product_option_value_id, String productId, String item_count) {
        JSONObject data= new JSONObject();
        try {
            data.put("product_id", productId);
            data.put("quantity", item_count);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(product_option_id,product_option_value_id);
            data.put("option", jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, ServiceNames.CART, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                     //   pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  pDialog.dismiss();
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
