package com.longpt.moneymanager.data.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private double amount;
    private String note;
    private String date;
    private String categoryId;
    private String type;

    public Transaction() {
    }

    public Transaction(String id, double amount, String note, String date, String categoryId, String type) {
        this.id = id;
        this.amount = amount;
        this.note = note;
        this.date = date;
        this.categoryId = categoryId;
        this.type= type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
