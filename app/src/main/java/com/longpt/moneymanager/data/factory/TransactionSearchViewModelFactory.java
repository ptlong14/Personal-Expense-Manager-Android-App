package com.longpt.moneymanager.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.longpt.moneymanager.data.viewmodel.TransactionSearchViewModel;

public class TransactionSearchViewModelFactory implements ViewModelProvider.Factory {
    private final String uid;

    public TransactionSearchViewModelFactory(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TransactionSearchViewModel.class)) {
            return (T) new TransactionSearchViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}