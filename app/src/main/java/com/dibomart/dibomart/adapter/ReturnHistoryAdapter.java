package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dibomart.dibomart.OrderDetailActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.ReturnDetailsActivity;
import com.dibomart.dibomart.model.OrderList;
import com.dibomart.dibomart.model.ReturnHistoryList;

import java.util.List;

public class ReturnHistoryAdapter extends RecyclerView.Adapter<ReturnHistoryAdapter.MyViewHolder> {

    private final Context ctx;
    private final List<ReturnHistoryList> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView id;
        final TextView name;
        final TextView date;
        final TextView status;
        final ImageView next;
        final RelativeLayout rlorder;

        MyViewHolder(View view) {
            super(view);

            id = view.findViewById(R.id.id);
            name = view.findViewById(R.id.name);
            next = view.findViewById(R.id.next);
            date = view.findViewById(R.id.date);
            status = view.findViewById(R.id.return_status);
            rlorder = view.findViewById(R.id.rl_order);

        }
    }

    public ReturnHistoryAdapter(Context context, List<ReturnHistoryList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_return_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ReturnHistoryList ongoing = moviesList.get(position);

        holder.id.setText("#"+ongoing.getOrder_id());
        holder.name.setText(ongoing.getName());
        holder.status.setText(ongoing.getStatus());
        holder.date.setText(ongoing.getDate());

        holder.rlorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(), ReturnDetailsActivity.class)
                        .putExtra("return_id", ongoing.getReturn_id()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
