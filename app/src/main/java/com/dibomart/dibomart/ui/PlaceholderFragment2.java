package com.dibomart.dibomart.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class PlaceholderFragment2 extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel2 pageViewModel;
    private ProgressDialog pDialog;
    private static PrefManager prf;
    private List<ProductList> productLists = new ArrayList<>();
    private List<ProductOption> productOptionList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductListAdapter mAdapter;
    private List<SubCategory> subCat = new ArrayList<>();

    public static PlaceholderFragment2 newInstance(int index) {
        PlaceholderFragment2 fragment = new PlaceholderFragment2();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel2.class);
        int index = 0;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product_list, container, false);
      //  final TextView textView = root.findViewById(R.id.section_label);
        prf = new PrefManager(getContext());
        pageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                assert s != null;
                Log.d("subcatindex",String.valueOf(s));
              //  textView.setText(s);
                subCat = Global.subCatList;
                if (subCat.size()>s) {
                    SubCategory subCategory = subCat.get(s);
                    getProductList(subCategory.getCategory_id());
                }
            }
        });

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        mAdapter = new ProductListAdapter(getContext(), productLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    private void getProductList(String getData) {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ServiceNames.PRODUCT_LIST + getData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("success") == 1) {
                                productLists.clear();
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
                                            productList.setPrice(c.optInt("price"));
                                            productList.setSpecial_price(c.optInt("special"));
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
                                                productOption.setPrice(jsonOb.optInt("price"));
                                                productOption.setQuantity(jsonOb.optString("quantity"));
                                                productOption.setProduct_option_value_id(jsonOb.optString("product_option_value_id"));
                                                productOptionList.add(productOption);
                                            }
                                            productList.setProductOptions(productOptionList);
                                            productLists.add(productList);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    mAdapter.notifyDataSetChanged();
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