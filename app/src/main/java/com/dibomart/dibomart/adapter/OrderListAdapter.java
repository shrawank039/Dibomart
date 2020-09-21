package com.dibomart.dibomart.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.ReturnActivity;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.model.OrderList;
import com.dibomart.dibomart.model.OrderedProductList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<OrderedProductList> moviesList;
    private static PrefManager prf;
    private ProgressDialog pDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final TextView name;
        final TextView quantity;
        final TextView price;
        final TextView optionValue;
        final TextView optionName;
        final Button returnReq;

        MyViewHolder(View view) {
            super(view);

            prf = new PrefManager(ctx);
            name = view.findViewById(R.id.name);
            quantity = view.findViewById(R.id.txt_quantity);
            price = view.findViewById(R.id.txt_price);
            optionValue = view.findViewById(R.id.txt_option_value);
            optionName = view.findViewById(R.id.txt_option_name);
            returnReq = view.findViewById(R.id.btn_return);
        }
    }

    public OrderListAdapter(Context context, List<OrderedProductList> moviesList) {
        ctx = context;
        this.moviesList = moviesList;

       // pageViewModel = ViewModelProviders.of((FragmentActivity) context).get(PageViewModel.class);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_product_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderedProductList ongoing = moviesList.get(position);

        holder.name.setText(ongoing.getName());
        holder.quantity.setText(ongoing.getQuantity());
        holder.optionName.setText(ongoing.getOption_name()+" - ");
        holder.optionValue.setText(ongoing.getOption_value());
        holder.price.setText(ongoing.getPrice());
        pDialog = new ProgressDialog(ctx);

        if (ongoing.getStatus().equalsIgnoreCase("Delivered"))
            holder.returnReq.setVisibility(View.VISIBLE);

        holder.returnReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.orderedProductList = ongoing;
                view.getContext().startActivity(new Intent(view.getContext(), ReturnActivity.class));
            }
        });

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

}
