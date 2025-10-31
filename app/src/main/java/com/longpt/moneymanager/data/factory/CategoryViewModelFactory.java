package com.longpt.moneymanager.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.longpt.moneymanager.data.viewmodel.CategoryViewModel;

public class CategoryViewModelFactory implements ViewModelProvider.Factory {
    private final String uid;

    public CategoryViewModelFactory(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoryViewModel.class)) {
            return (T) new CategoryViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

