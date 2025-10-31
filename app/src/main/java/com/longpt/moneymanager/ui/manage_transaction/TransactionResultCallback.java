package com.longpt.moneymanager.ui.manage_transaction;

public interface TransactionResultCallback {
    void onSuccess();
    void onFailure(Exception e);
}
