package com.dibomart.dibomart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.dibomart.dibomart.fragment.OrderDetailsFragment;
import com.dibomart.dibomart.fragment.OrderListFragment;
import com.dibomart.dibomart.fragment.PaymentAddressFragment;
import com.dibomart.dibomart.fragment.ShippingAddressFragment;
import com.google.android.material.tabs.TabLayout;

public class OrderDetailActivity extends AppCompatActivity {

    private String id;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        id = getIntent().getStringExtra("order_id");

        bundle = new Bundle();
        bundle.putString("order_id", id);

        Fragment fragment;
        fragment = new OrderDetailsFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Details"));
        tabLayout.addTab(tabLayout.newTab().setText("Products"));
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        assert tab != null;
        tab.select();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment;
                switch(tab.getPosition()) {
                    case 0:
                        fragment = new OrderDetailsFragment();
                        fragment.setArguments(bundle);
                        loadFragment(fragment);
                        break;
                    case 1:
                        fragment = new OrderListFragment();
                        fragment.setArguments(bundle);
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