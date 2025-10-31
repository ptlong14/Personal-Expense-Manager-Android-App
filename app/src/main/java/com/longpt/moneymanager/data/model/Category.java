package com.longpt.moneymanager.data.model;

import java.io.Serializable;

public class Category implements Serializable {
    private String id, name, icon, color;

    public Category() {
    }

    public Category(String id, String name, String icon, String color) {
        this.id = id ;
        this.name = name;
        this.icon = icon;
        this.color= color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
