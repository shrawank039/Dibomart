package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.Merchant;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<Merchant> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;


        MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.imageMerchant);
        }
    }


    public MerchantAdapter(Context context, List<Merchant> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_merchant, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Merchant ongoing = moviesList.get(position);

        Glide.with(ctx)
                .load(ongoing.getImage_url())
                .placeholder(R.drawable.ic_categories)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
