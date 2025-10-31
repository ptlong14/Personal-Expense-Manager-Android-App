package com.longpt.moneymanager.data.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;

public class TransactionCalendarViewModelFactory implements ViewModelProvider.Factory {
        private final String uid;

        public TransactionCalendarViewModelFactory(String uid) {
            this.uid = uid;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TransactionAnalyticsViewModel.class)) {
                return (T) new TransactionAnalyticsViewModel(uid);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }

}
