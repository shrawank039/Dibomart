package com.dibomart.dibomart;

import android.content.Intent;
import android.os.Bundle;

import com.dibomart.dibomart.model.Category;
import com.dibomart.dibomart.model.SubCategory;
import com.dibomart.dibomart.ui.SectionsPagerAdapter2;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{

    private SectionsPagerAdapter2 sectionsPagerAdapter;
    private List<SubCategory> subCategoryList = new ArrayList<>();
    private static PrefManager prf;
    int position;
    ViewPager viewPager;
    TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        prf = new PrefManager(this);
        subCategoryList = new ArrayList<>();
        viewPager = findViewById(R.id.view_pager);
        tabs = findViewById(R.id.tabs);

        position = getIntent().getIntExtra("index", 0);

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        List<String> spinnerArray =  new ArrayList<String>();

        spinnerArray.clear();
        for (int i = 0; i < Global.ongoingList.size(); i++) {
            Category category = Global.ongoingList.get(i);
            spinnerArray.add(category.getName());
        }

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
}