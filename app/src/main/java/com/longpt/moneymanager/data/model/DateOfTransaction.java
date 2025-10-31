package com.longpt.moneymanager.data.model;

import java.util.List;

public class DateOfTransaction {
    private String date;
    private List<Transaction> transactions;

    public DateOfTransaction(String date, List<Transaction> transactions) {
        this.date = date;
        this.transactions = transactions;
    }

    public String getDate() {
        return date;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}

