package com.longpt.moneymanager.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.ui.manage_transaction.TransactionResultCallback;

public class TransactionRepository {
    private final DatabaseReference db;

    public TransactionRepository(String uid) {
        db = FirebaseDatabase.getInstance().getReference("users").child(uid).child("transactions");
    }

    public void addTransaction(Transaction transaction, TransactionResultCallback callback) {
        String key = db.push().getKey();
        if (key != null) {
            transaction.setId(key);
            db.child(key).setValue(transaction).addOnSuccessListener(unused -> callback.onSuccess()).addOnFailureListener(callback::onFailure);
        } else {
            callback.onFailure(new Exception("Khóa chính Transaction null!"));
        }
    }

    public void updateTransaction(Transaction transaction, TransactionResultCallback callback){
        db.child(transaction.getId()).setValue(transaction)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
    public void updateTransactionsCategoryToDefault(String oldCategoryId, String defaultCategoryId) {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction t = child.getValue(Transaction.class);
                    if (t != null && oldCategoryId.equals(t.getCategoryId())) {
                        t.setCategoryId(defaultCategoryId);
                        db.child(child.getKey()).setValue(t);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void deleteTransaction(String id, TransactionResultCallback callback) {
        db.child(id).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
    public void getTransactions(ValueEventListener listener) {
        db.addValueEventListener(listener);
    }
}
