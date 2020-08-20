package com.dibomart.dibomart.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.request.RequestOptions;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.ProductListActivity;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.WebViewActivity;
import com.dibomart.dibomart.adapter.CategoryAdapter;
import com.dibomart.dibomart.adapter.MerchantAdapter;
import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.Merchant;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SliderLayout homeSlider;
    SliderLayout promoSlider;
    private static PrefManager prf;
    private List<Category> ongoingList = new ArrayList<>();
    private List<Merchant> merchantList = new ArrayList<>();
    private RecyclerView recyclerView, merchantRecylerView;
    private CategoryAdapter mAdapter;
    private MerchantAdapter merchantAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

         homeSlider = view.findViewById(R.id.slider);
         promoSlider = view.findViewById(R.id.promo_slider);

        prf = new PrefManager(getContext());
        getCategory();
        getBannerImage();
        getPromoBanner();
        getMerchant();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        merchantRecylerView = view.findViewById(R.id.recyclerViewMerchant);

        ongoingList = new ArrayList();
        merchantList = new ArrayList();

        mAdapter = new CategoryAdapter(getActivity(), ongoingList);
        merchantAdapter = new MerchantAdapter(getActivity(), merchantList);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // row click listenerMyDividerItemDecoration
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Category ongoing = ongoingList.get(position);

                //  Toast.makeText(context, ongoing.getId(), Toast.LENGTH_SHORT).show();

                    Intent in = new Intent(getActivity(), ProductListActivity.class);
                    in.putExtra("category_id", ongoing.getId());
                    startActivity(in);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        merchantRecylerView.setHasFixedSize(true);

        merchantRecylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        merchantRecylerView.setItemAnimator(new DefaultItemAnimator());
        merchantRecylerView.setAdapter(merchantAdapter);

        // row click listenerMyDividerItemDecoration
        merchantRecylerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Merchant merchant = merchantList.get(position);

                if(!merchant.getUrl().equals("")) {
                    Intent in = new Intent(getActivity(), WebViewActivity.class);
                    in.putExtra("link", merchant.getUrl());
                    startActivity(in);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        LinearLayout llbanner = view.findViewById(R.id.ll_banner);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,prf.getInt("banner_height"));
        llbanner.setLayoutParams(parms);

        LinearLayout llpromo_banner = view.findViewById(R.id.ll_banner_promo);
        LinearLayout.LayoutParams pparms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,prf.getInt("pbanner_height"));
        llpromo_banner.setLayoutParams(pparms);

        return view;
    }

    private void getBannerImage() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.BANNER_IMG+"/7", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        //  Log.d("TAG", "image : "+c.optString("image_url"));
                                        TextSliderView sliderView = new TextSliderView(getContext());
                                        sliderView
                                                .image(c.optString("image"))
                                                //    .description(listName.get(i))
                                                .setRequestOption(requestOptions)
                                                .setProgressBarVisible(true);

                                        //add your extra information
                                        sliderView.bundle(new Bundle());
                                        //  sliderView.getBundle().putString("extra", listName.get(i));
                                        homeSlider.addSlider(sliderView);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }

    private void getPromoBanner() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.BANNER_IMG+"/6", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                RequestOptions requestOptions = new RequestOptions();
                                requestOptions.centerCrop();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = null;
                                    try {
                                        c = jsonArray.getJSONObject(i);
                                        //  Log.d("TAG", "image : "+c.optString("image_url"));
                                        TextSliderView sliderView = new TextSliderView(getContext());
                                        sliderView
                                                .image(c.optString("image"))
                                                //    .description(listName.get(i))
                                                .setRequestOption(requestOptions)
                                                .setProgressBarVisible(true);

                                        //add your extra information
                                        sliderView.bundle(new Bundle());
                                        //  sliderView.getBundle().putString("extra", listName.get(i));
                                        promoSlider.addSlider(sliderView);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }

    private void getCategory() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.CATEGORY, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            JSONArray jsonarray = null;
                            try {
                                jsonarray = response.getJSONArray("data");

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    JSONObject c = null;
                                    try {
                                        c = jsonarray.getJSONObject(i);
                                        Category ongoing = new Category();
                                        ongoing.setName(c.optString("name"));
                                        ongoing.setId(c.optString("category_id"));
                                        ongoing.setImage_url(c.optString("image"));

                                        ongoingList.add(ongoing);
                                        mAdapter.notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }

    private void getMerchant() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, ServiceNames.BANNER_IMG+"/8", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response.optString("success").equals("1")) {
                            JSONArray jsonarray = null;
                            try {
                                jsonarray = response.getJSONArray("data");

                                for (int i = 0; i < jsonarray.length(); i++) {

                                    JSONObject c = null;
                                    try {
                                        c = jsonarray.getJSONObject(i);
                                        Merchant merchant = new Merchant();
                                        merchant.setName(c.optString("name"));
                                        merchant.setUrl(c.optString("link"));
                                        merchant.setImage_url(c.optString("image"));

                                        merchantList.add(merchant);
                                        merchantAdapter.notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "03 " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-Oc-Merchant-Id", prf.getString("s_key"));
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjReq.setShouldCache(false);
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(), slider.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show();
    }
}