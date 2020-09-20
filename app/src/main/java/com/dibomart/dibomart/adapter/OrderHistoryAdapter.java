package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.dibomart.dibomart.OrderDetailActivity;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.OrderList;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    private final Context ctx;
    private final List<OrderList> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView id;
        final TextView item;
        final TextView date;
        final TextView price;
        final TextView status;
        final ImageView next;
        final RelativeLayout rlorder;

        MyViewHolder(View view) {
            super(view);

            id = view.findViewById(R.id.id);
            item = view.findViewById(R.id.item);
            price = view.findViewById(R.id.price);
            next = view.findViewById(R.id.next);
            date = view.findViewById(R.id.date);
            status = view.findViewById(R.id.order_status);
            rlorder = view.findViewById(R.id.rl_order);

        }
    }

    public OrderHistoryAdapter(Context context, List<OrderList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderList ongoing = moviesList.get(position);

        holder.id.setText("#"+ongoing.getId());
        holder.price.setText(ongoing.getPrice());
        holder.status.setText(ongoing.getStatus());
        holder.item.setText(ongoing.getItem());
        holder.date.setText(ongoing.getDate());

        holder.rlorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), OrderDetailActivity.class)
                        .putExtra("order_id", ongoing.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
