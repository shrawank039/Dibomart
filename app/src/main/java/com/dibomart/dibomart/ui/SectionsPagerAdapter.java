package com.dibomart.dibomart.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.ProductList;
import com.dibomart.dibomart.model.SubCategory;

import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    String category_id;
    private final List<SubCategory> subCategoryLists;

    public SectionsPagerAdapter(Context context, FragmentManager fm, List<SubCategory> subCategoryLists) {
        super(fm);
        mContext = context;
        this.subCategoryLists = subCategoryLists;
    }

    @Override
    public Fragment getItem(int position) {
        final SubCategory subCategory = subCategoryLists.get(position);
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        final SubCategory subCategory = subCategoryLists.get(position);
        return subCategory.getName();
    }

    @Override
    public int getCount() {
        return subCategoryLists.size();
    }
}