package com.dibomart.dibomart.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.adapter.ProductListAdapter;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.ProductOption;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ProgressDialog pDialog;
    private static PrefManager prf;
    private TextView id;
    private TextView shipping;
    private TextView payment;
    private TextView date;
    private TextView status;
    private TextView subTotal;
    private TextView shippingCharges;
    private TextView discount;
    private TextView total;
    private String order_id;

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailsFragment newInstance(String param1, String param2) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order_details, container, false);

        prf = new PrefManager(getContext());
        assert getArguments() != null;
        order_id = getArguments().getString("order_id");

        id = root.findViewById(R.id.id);
        shipping = root.findViewById(R.id.shipping_method);
        payment = root.findViewById(R.id.pay_method);
        date = root.findViewById(R.id.date);
        status = root.findViewById(R.id.order_status);
        subTotal = root.findViewById(R.id.txt_subtotal);
        shippingCharges = root.findViewById(R.id.txt_delivery);
        discount = root.findViewById(R.id.txt_discount);
        total = root.findViewById(R.id.txt_total);

        id.setText("#"+order_id);

        getProductDetails();
        return root;
    }

    private void getProductDetails() {

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.ORDER_HISTORY+order_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {

                                    payment.setText(jsonObject1.optString("payment_method"));
                                    shipping.setText(jsonObject1.optString("shipping_method"));

                                    JSONArray history = jsonObject1.getJSONArray("histories");
                                    JSONObject hisObj = history.getJSONObject(0);
                                    date.setText(hisObj.optString("date_added"));
                                    status.setText(hisObj.optString("status"));

                                    JSONArray jsonarray = jsonObject1.getJSONArray("totals");

                                    if (jsonarray.length() > 3) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(0);
                                            String a = "Rs. " + c.optString("value");
                                            subTotal.setText(a);
                                            c = jsonarray.getJSONObject(2);
                                            String b = "Rs. " + c.optString("value");
                                            discount.setText(b);
                                            c = jsonarray.getJSONObject(3);
                                            String d = "Rs. " + c.optString("value");
                                            total.setText(d);
                                            c = jsonarray.getJSONObject(1);
                                            String de = "Rs. " + c.optString("value");
                                            shipping.setText(de);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(0);
                                            String a = "Rs. " + c.optString("value");
                                            subTotal.setText(a);
                                            c = jsonarray.getJSONObject(2);
                                            String d = "Rs. " + c.optString("value");
                                            total.setText(d);
                                            c = jsonarray.getJSONObject(1);
                                            String de = "Rs. " + c.optString("value");
                                            shippingCharges.setText(de);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                Toast.makeText(getContext(), "03 error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
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
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

}