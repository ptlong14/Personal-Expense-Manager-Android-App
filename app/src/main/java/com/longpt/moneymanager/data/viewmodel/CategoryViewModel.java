package com.longpt.moneymanager.data.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.repository.CategoryRepository;
import com.longpt.moneymanager.data.repository.TransactionRepository;
import com.longpt.moneymanager.ui.manage_category.CategoryResultCallback;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Category>> income= new MutableLiveData<>();
    private final MutableLiveData<List<Category>> expense= new MutableLiveData<>();
    private final CategoryRepository repo;
    public CategoryViewModel (String uid){
        repo= new CategoryRepository(uid);
        loadData();
    }
    public void addCategory(String type, Category c, CategoryResultCallback callback) {
        repo.addCategory(type, c, callback);
    }

    public void updateCategory(String type, Category c, CategoryResultCallback callback) {
        repo.updateCategory(type, c, callback);
    }

    public void deleteCategory(String type, String id, CategoryResultCallback callback) {
        repo.deleteCategory(type, id, new CategoryResultCallback() {
            @Override
            public void onSuccess() {
                String defaultId = type.equals("income") ? "unknownIncomeId" : "unknownExpenseId";
                TransactionRepository transactionRepo = new TransactionRepository(repo.getUid());
                transactionRepo.updateTransactionsCategoryToDefault(id, defaultId);
                callback.onSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }
    public LiveData<List<Category>> getIncomeCategory() {
        return income;
    }

    public LiveData<List<Category>> getExpenseCategory() {
        return expense;
    }

    private void loadData() {
        repo.getAndMapCategories("income", getListener(income));
        repo.getAndMapCategories("expense", getListener(expense));
    }

    private ValueEventListener getListener(MutableLiveData<List<Category>> target){
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categories= new ArrayList<>();
                for(DataSnapshot child: snapshot.getChildren() ){
                    Category c=child.getValue(Category.class);
                    if (c!=null) categories.add(c);
                }
                target.setValue(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }
}
