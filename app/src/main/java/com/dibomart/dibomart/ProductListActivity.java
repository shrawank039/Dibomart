package com.dibomart.dibomart;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;

import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.ui.PageViewModel;
import com.dibomart.dibomart.ui.SectionsPagerAdapter2;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    private SectionsPagerAdapter2 sectionsPagerAdapter;
    private List<SubCategory> subCategoryList = new ArrayList<>();
    private static PrefManager prf;
    int position;
    private PageViewModel pageViewModel;
    ViewPager viewPager;
    TabLayout tabs;
    LinearLayout ll_cart;
    TextView txtCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        prf = new PrefManager(this);
        subCategoryList = new ArrayList<>();
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);
        ll_cart = findViewById(R.id.ll_cart);
        txtCartItem = findViewById(R.id.cart_item);

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        position = getIntent().getIntExtra("index", 0);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.clear();
        for (int i = 0; i < Global.ongoingList.size(); i++) {
            Category category = Global.ongoingList.get(i);
            spinnerArray.add(category.getName());
        }

        PageViewModel.setIndex(prf.getInt("cart_price"));
        PageViewModel.setitemIndex(prf.getInt("cart_item"));
        pageViewModel.getText().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                TextView price = findViewById(R.id.txt_total);
                String a = "Rs. " + s;
                price.setText(a);
            }
        });
        pageViewModel.getItemText().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer s) {
                if (s == 0) {
                    ll_cart.setVisibility(View.GONE);
                    txtCartItem.setVisibility(View.GONE);
                }else {
                    ll_cart.setVisibility(View.VISIBLE);
                    txtCartItem.setVisibility(View.VISIBLE);
                }
                TextView item = findViewById(R.id.txt_item);
                txtCartItem.setText(String.valueOf(s));
                String a = "Item " + s;
                item.setText(a);
            }
        });

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,R.layout.spinner_item,spinnerArray);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);
        spin.setSelection(position);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
     //   Toast.makeText(getApplicationContext(), Global.ongoingList.get(position).getName() , Toast.LENGTH_LONG).show();
        Category category = Global.ongoingList.get(position);
        subCategoryList = category.getSubCategoryList();
        Global.subCatList = subCategoryList;
        sectionsPagerAdapter = new SectionsPagerAdapter2(this, getSupportFragmentManager(),category.getSubCategoryList());
      //  sectionsPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void backPress(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void searchClick(View view) {
        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
    }
    public void cartClick(View view) {
        startActivity(new Intent(getApplicationContext(),CartActivity.class));
    }

    public void nextClick(View view) {
        startActivity(new Intent(getApplicationContext(),ShippingMethod.class));
    }
}