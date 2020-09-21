package com.dibomart.dibomart.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.adapter.CartListAdapter;
import com.dibomart.dibomart.adapter.OrderListAdapter;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.model.OrderedProductList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String order_id;
    private ProgressDialog pDialog;
    private static PrefManager prf;
    String status;
    private RecyclerView recyclerView;
    private OrderListAdapter mAdapter;
    private List<OrderedProductList> productLists = new ArrayList<>();

    public OrderListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderListFragment newInstance(String param1, String param2) {
        OrderListFragment fragment = new OrderListFragment();
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
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        prf = new PrefManager(getContext());
        assert getArguments() != null;
        order_id = getArguments().getString("order_id");

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        productLists = new ArrayList();
        mAdapter = new OrderListAdapter(getContext(), productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        getProductList();

        return view;
    }

    private void getProductList() {

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, ServiceNames.ORDER_HISTORY+order_id,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    JSONArray jsonarray = jsonObject1.getJSONArray("products");
                                    JSONArray history = jsonObject1.getJSONArray("histories");
                                    for (int x =0; x<history.length(); x++) {
                                        JSONObject hisObj = history.getJSONObject(x);
                                        status = hisObj.optString("status");
                                    }

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            OrderedProductList productList = new OrderedProductList();
                                            productList.setName(c.optString("name"));
                                            productList.setModel(c.optString("model"));
                                            productList.setPrice(c.optString("total"));
                                            productList.setQuantity(c.optString("quantity"));
                                            productList.setProduct_id(c.optString("product_id"));
                                            productList.setFirstname(jsonObject1.optString("firstname"));
                                            productList.setLastname(jsonObject1.optString("lastname"));
                                            productList.setEmail(jsonObject1.optString("email"));
                                            productList.setTelephone(jsonObject1.optString("telephone"));
                                            productList.setPrduct(c.optString("name"));
                                            productList.setDate(jsonObject1.optString("date_added"));
                                            productList.setStatus(status);
                                            productList.setOrder_id(jsonObject1.optString("order_id"));

                                            JSONArray optionArray = c.getJSONArray("option");
                                            JSONObject optionObj = optionArray.getJSONObject(0);
                                            productList.setOption_name(optionObj.optString("name"));
                                            productList.setOption_value(optionObj.optString("value"));

                                            productLists.add(productList);
                                            mAdapter.notifyDataSetChanged();

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