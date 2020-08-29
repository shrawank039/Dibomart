package com.dibomart.dibomart.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductDetailsActivity;
import com.dibomart.dibomart.ProductListActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.adapter.ProductListAdapter;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.ProductOption;
import com.dibomart.dibomart.model.SubCategory;
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
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static PrefManager prf;
    private PageViewModel pageViewModel;
    private List<ProductList> productLists = new ArrayList<>();
    private List<ProductOption> productOptionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductListAdapter mAdapter;
    private Context context;
    private List<SubCategory> subCat;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        context = getActivity();
        prf = new PrefManager(context);
        pageViewModel.setIndex(index);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);
        prf = new PrefManager(getContext());

        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                ProductListActivity activity = (ProductListActivity) getActivity();
                subCat = Global.subCatList;
                //  Toast.makeText(activity, String.valueOf(s), Toast.LENGTH_SHORT).show();
                if (subCat.size() > 0) {
                    SubCategory subCategory = subCat.get(s);
                    getProductList(subCategory.getCategory_id());
                }
            }
        });

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        productLists = new ArrayList();
        mAdapter = new ProductListAdapter(context, productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    private void getProductList(String getData) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.PRODUCT_LIST + getData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                JSONArray jsonarray = null;
                                try {
                                    jsonarray = jsonObject.getJSONArray("data");

                                    for (int i = 0; i < jsonarray.length(); i++) {

                                        productOptionList = new ArrayList<>();
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            ProductList productList = new ProductList();
                                            productList.setName(c.optString("name"));
                                            productList.setId(c.optString("id"));
                                            productList.setImage_url(c.optString("image"));
                                            productList.setPrice(c.optString("price"));
                                            productList.setSpecial_price(c.optString("special"));
                                            productList.setWeight(c.optString("weight"));
                                            productList.setItem_count(0);
                                            productList.setWeight_class(c.optString("weight_class"));
                                            productList.setProduct_id(c.optString("product_id"));
                                            productList.setDescription(c.optString("description"));

                                            JSONArray jsonArray = c.optJSONArray("options");
                                            assert jsonArray != null;
                                            JSONObject jsonObj = jsonArray.optJSONObject(0);
                                            productList.setProduct_option_id(jsonObj.optString("product_option_id"));

                                            JSONArray jsonArr = jsonObj.optJSONArray("option_value");
                                            assert jsonArr != null;
                                            for (int a = 0; a < jsonArr.length(); a++) {
                                                JSONObject jsonOb = jsonArr.optJSONObject(a);
                                                ProductOption productOption = new ProductOption();
                                                productOption.setName(jsonOb.optString("name"));
                                                productOption.setPrice(jsonOb.optString("price"));
                                                productOption.setProduct_option_value_id(jsonOb.optString("product_option_value_id"));
                                                productOptionList.add(productOption);
                                            }
                                            productList.setProductOptions(productOptionList);
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
                Toast.makeText(getContext(), "03 error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
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