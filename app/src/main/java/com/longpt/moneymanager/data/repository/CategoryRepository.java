package com.longpt.moneymanager.data.repository;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.ui.manage_category.CategoryResultCallback;

public class CategoryRepository {
    private final DatabaseReference db;

    private final String uid;
    public CategoryRepository(String uid) {
        this.uid= uid;
        db= FirebaseDatabase.getInstance().getReference("users").child(uid).child("categories");
    }
    public String getUid() {
        return uid;
    }

    public void addCategory(String type, Category category, CategoryResultCallback callback){
        //Tạo id
        String key= db.child(type).push().getKey();
        if(key!=null){
            category.setId(key);
            //Lưu lên firebase
            db.child(type).child(key).setValue(category)
                    .addOnSuccessListener(unused -> callback.onSuccess())
                    .addOnFailureListener(callback::onFailure);
        }else {
            callback.onFailure(new Exception("Khóa chính của Category null!"));
        }
    }

    public void updateCategory(String type, Category category, CategoryResultCallback callback){
        db.child(type).child(category.getId()).setValue(category)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
    public void deleteCategory(String type, String id, CategoryResultCallback callback) {
        db.child(type).child(id).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
    public void getAndMapCategories(String type, ValueEventListener listener) {
        db.child(type).addValueEventListener(listener);
    }
}
