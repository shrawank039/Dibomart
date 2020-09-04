package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dibomart.dibomart.adapter.AddressListAdapter;
import com.dibomart.dibomart.adapter.CartListAdapter;
import com.dibomart.dibomart.fragment.PaymentAddressFragment;
import com.dibomart.dibomart.fragment.ShippingAddressFragment;
import com.dibomart.dibomart.model.AddressList;
import com.dibomart.dibomart.model.CartList;
import com.dibomart.dibomart.net.MySingleton;
import com.dibomart.dibomart.net.ServiceNames;
import com.dibomart.dibomart.ui.PageViewModel;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddressActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Fragment fragment;
        fragment = new ShippingAddressFragment();
        loadFragment(fragment);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("SHIPPING"));
        tabLayout.addTab(tabLayout.newTab().setText("PAYMENT"));
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        assert tab != null;
        tab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment;
                switch(tab.getPosition()) {
                    case 0:
                        fragment = new ShippingAddressFragment();
                        loadFragment(fragment);
                        break;
                    case 1:
                        fragment = new PaymentAddressFragment();
                        loadFragment(fragment);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
       // transaction.addToBackStack(null);
        transaction.commit();
    }

    public void backPress(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}