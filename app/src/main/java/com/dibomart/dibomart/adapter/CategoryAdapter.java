package com.dibomart.dibomart.adapter;

import android.content.Context;
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
import com.dibomart.dibomart.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<Category> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final CardView cardView;
        final TextView name;
        ImageView imageView;


        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cardView);
            name = (TextView) view.findViewById(R.id.title);
            imageView = view.findViewById(R.id.image);
        }
    }


    public CategoryAdapter(Context context, List<Category> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Category ongoing = moviesList.get(position);

        holder.name.setText(ongoing.getName());
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
