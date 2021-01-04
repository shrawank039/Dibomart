package com.dibomart.dibomart.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.AddAdress;
import com.dibomart.dibomart.Global;
import com.dibomart.dibomart.PrefManager;
import com.dibomart.dibomart.R;
import com.dibomart.dibomart.adapter.AddressListAdapter;
import com.dibomart.dibomart.model.AddressList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShippingAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShippingAddressFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static PrefManager prf;
    private ProgressDialog pDialog;
    private List<AddressList> addressLists;
    private RecyclerView recyclerView;
    private AddressListAdapter mAdapter;
    private Button addBtn;

    public ShippingAddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShippingAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShippingAddressFragment newInstance(String param1, String param2) {
        ShippingAddressFragment fragment = new ShippingAddressFragment();
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
        View root = inflater.inflate(R.layout.fragment_shipping_address, container, false);
        // Inflate the layout for this fragment
        prf = new PrefManager(getContext());
        addressLists = new ArrayList<>();
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        addBtn = root.findViewById(R.id.btn_add_address);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddAdress.class)
                        .putExtra("address_id","new")
                .putExtra("type","0"));
                requireActivity().finish();
            }
        });

        mAdapter = new AddressListAdapter(getContext(), addressLists);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        loadAddress();
        return root;
    }

        private void loadAddress() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();


        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, Global.base_url+ServiceNames.ADDRESS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        pDialog.dismiss();
                        Log.d("TAG", "response : " + jsonObject);
                        try {
                            if (jsonObject.optInt("success") == 1) {
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                try {
                                    String defaultAddress = jsonObject1.optString("address_id");
                                    JSONArray jsonarray = jsonObject1.getJSONArray("addresses");

                                    Global.addressCount = jsonarray.length();

                                    for (int i = 0; i < jsonarray.length(); i++) {
                                        JSONObject c = null;
                                        try {
                                            c = jsonarray.getJSONObject(i);
                                            AddressList addressList = new AddressList();
                                            addressList.setFirstName(c.optString("firstname"));
                                            addressList.setLastName(c.optString("lastname"));
                                            addressList.setAdd1(c.optString("address_1"));
                                            addressList.setAdd2(c.optString("address_2"));
                                            addressList.setAddressId(c.optString("address_id"));
                                            addressList.setPincode(c.optString("postcode"));
                                            addressList.setCity(c.optString("city"));
                                            addressList.setCountry(c.optString("country"));
                                            addressList.setZoneId(c.optString("zone_id"));

                                            addressLists.add(addressList);
                                            mAdapter.notifyDataSetChanged();

                                            if (Global.addressCount == 1){
                                                makeDefault(c.optString("address_id"));
                                            }

                                            if (defaultAddress.equals(c.optString("address_id"))){
                                                String name = c.optString("firstname")+" "+c.optString("lastname");
                                                prf.setString("shipping_id",defaultAddress);
                                                prf.setString("shipping_name",name);
                                                prf.setString("shipping_address",c.optString("address_1"));
                                                prf.setString("shipping_postcode",c.optString("postcode"));
                                            }

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
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
            }
        }){
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

    private void makeDefault(final String key) {

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.EXISTING_PAY_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        makeDefaultShip(key);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
            }
        }){
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

    private void makeDefaultShip(String key) {

        JSONObject data= new JSONObject();
        try {
            data.put("address_id", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, Global.base_url+ServiceNames.EXISTING_ADDRESS, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        //     pDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   pDialog.dismiss();
                try {
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONArray jsonArray = jsonObject.optJSONArray("error");
                    String err = jsonArray.optString(0);
                    Toast.makeText(getContext(), err, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    //Handle a malformed json response
                }
            }
        }){
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