package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.ProductList;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<ProductList> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView quantity;
        final TextView price;
        final TextView special_price;
        final ImageView productImg;
        final ImageView favImg;

        MyViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            quantity = view.findViewById(R.id.txt_quantity);
            price = view.findViewById(R.id.txt_mrp);
            special_price = view.findViewById(R.id.txt_price);
            productImg = view.findViewById(R.id.product_image);
            favImg = view.findViewById(R.id.img_fav);

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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ProductList ongoing = moviesList.get(position);

        holder.title.setText(ongoing.getName());
        String a = "Quantity - "+ongoing.getWeight()+" "+ongoing.getWeight_class();
        holder.quantity.setText(a);
        if (!ongoing.getPrice().equals(""))
        holder.price.setText("\u20B9"+ongoing.getPrice());
        else
            holder.price.setVisibility(View.GONE);
        holder.special_price.setText("\u20B9"+ongoing.getSpecial_price());
        Glide.with(ctx)
                .load(ongoing.getImage_url())
                .placeholder(R.drawable.placeholder)
                .into(holder.productImg);

        holder.favImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // holder.favImg.setImageDrawable(getResources().getDrawable(R.drawable.heart_selected));
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
