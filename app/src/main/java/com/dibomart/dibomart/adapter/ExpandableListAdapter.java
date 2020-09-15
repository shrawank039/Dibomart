package com.dibomart.dibomart.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dibomart.dibomart.R;
import com.dibomart.dibomart.model.MenuModel;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<MenuModel> listDataHeader;
    private HashMap<MenuModel, List<MenuModel>> listDataChild;

    public ExpandableListAdapter(Context context, List<MenuModel> listDataHeader,
                                 HashMap<MenuModel, List<MenuModel>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public MenuModel getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).menuName;

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_child, null);
        }

        TextView txtListChild = convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);

        ImageView subicon = convertView.findViewById(R.id.icon_subheader);
        if (groupPosition == 3) {
            switch (childPosition) {
                case 0:
                    subicon.setBackgroundResource(R.drawable.order);
                    break;
                case 1:
                    subicon.setBackgroundResource(R.drawable.refund);
                    break;
                case 2:
                    subicon.setBackgroundResource(R.drawable.replace);
                    break;
                case 3:
                    subicon.setBackgroundResource(R.drawable.edit_profile);
                    break;
                case 4:
                    subicon.setBackgroundResource(R.drawable.rate);
                    break;
                default:
                    break;
            }
        } else if (groupPosition == 2){
            subicon.setBackgroundResource(R.drawable.plus);
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)
            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public MenuModel getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).menuName;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group_header, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);
        ImageView icon = convertView.findViewById(R.id.icon_header);

        switch(groupPosition) {
            case 0:
                icon.setBackgroundResource(R.drawable.home);
                break;
            case 1:
                icon.setBackgroundResource(R.drawable.address);
                break;
            case 2:
                icon.setBackgroundResource(R.drawable.category);
                break;
            case 3:
                icon.setBackgroundResource(R.drawable.settings);
                break;
            case 4:
                icon.setBackgroundResource(R.drawable.contact);
                break;
            case 5:
                icon.setBackgroundResource(R.drawable.share);
                break;
            case 6:
                icon.setBackgroundResource(R.drawable.support);
                break;
            case 7:
                icon.setBackgroundResource(R.drawable.terms);
                break;
            case 8:
                icon.setBackgroundResource(R.drawable.info);
                break;
            case 9:
                icon.setBackgroundResource(R.drawable.return_img);
                break;
            case 10:
                icon.setBackgroundResource(R.drawable.logout);
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
