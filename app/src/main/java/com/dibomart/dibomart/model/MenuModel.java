package com.dibomart.dibomart.model;

public class MenuModel {

    public String menuName, id;
    public boolean hasChildren, isGroup;

    public MenuModel(String menuName, boolean isGroup, boolean hasChildren, String id) {

        this.menuName = menuName;
        this.id = id;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
