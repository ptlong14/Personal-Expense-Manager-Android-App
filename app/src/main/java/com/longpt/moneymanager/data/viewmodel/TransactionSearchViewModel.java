package com.longpt.moneymanager.data.viewmodel;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.repository.CategoryRepository;
import com.longpt.moneymanager.data.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TransactionSearchViewModel extends ViewModel {

    private final MutableLiveData<List<Transaction>> allTransactions = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Category>> categoryMap = new MutableLiveData<>();
    private final MutableLiveData<List<DateOfTransaction>> searchResults = new MutableLiveData<>();

    private final MutableLiveData<Double> totalIncome = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalExpense = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalBalance = new MutableLiveData<>(0.0);

    private final MediatorLiveData<Pair<List<DateOfTransaction>, Map<String, Category>>> mergedData = new MediatorLiveData<>();

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionSearchViewModel(String uid) {
        transactionRepository = new TransactionRepository(uid);
        categoryRepository = new CategoryRepository(uid);

        loadAllTransactions();
        loadCategories();

        mergedData.addSource(searchResults, data -> mergeData());
        mergedData.addSource(categoryMap, map -> mergeData());
    }

    private void mergeData() {
        List<DateOfTransaction> list = searchResults.getValue();
        Map<String, Category> map = categoryMap.getValue();
        if (list != null && map != null) {
            mergedData.setValue(new Pair<>(list, map));
        }
    }

    private void loadAllTransactions() {
        transactionRepository.getTransactions(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> all = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction t = child.getValue(Transaction.class);
                    if (t != null) all.add(t);
                }
                allTransactions.setValue(all);
                loadCategories();
                searchTransactions("", true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadCategories() {
        categoryRepository.getAndMapCategories("income", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                Map<String, Category> map = new HashMap<>();
                for (DataSnapshot child : snapshot1.getChildren()) {
                    Category c = child.getValue(Category.class);
                    if (c != null && c.getId() != null) map.put(c.getId(), c);
                }

                categoryRepository.getAndMapCategories("expense", new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        for (DataSnapshot child : snapshot2.getChildren()) {
                            Category c = child.getValue(Category.class);
                            if (c != null && c.getId() != null) map.put(c.getId(), c);
                        }
                        categoryMap.setValue(map);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void searchTransactions(String keyword, boolean isAllTime) {
        if (keyword == null || keyword.trim().isEmpty()) {
            mergedData.setValue(new Pair<>(new ArrayList<>(), new HashMap<>()));
            totalBalance.setValue(0.0);
            totalExpense.setValue(0.0);
            totalIncome.setValue(0.0);
            return;
        }
        List<Transaction> all = allTransactions.getValue();
        Map<String, Category> map = categoryMap.getValue();
        if (all == null || map == null) return;

        keyword = keyword.toLowerCase(Locale.ROOT).trim();
        List<Transaction> resultTransactions = new ArrayList<>();

        double incomeSum = 0;
        double expenseSum = 0;

        for (Transaction t : all) {
            boolean isMatchQuery = false;
            if (!isAllTime) {
                String yearString = t.getDate().substring(6);
                int year = Integer.parseInt(yearString);
                if (year != LocalDate.now().getYear()) continue;
            }

            // Theo amount
            if (String.valueOf((int) Math.abs(t.getAmount())).contains(keyword)) {
                isMatchQuery = true;
            }

            // Theo tên category
            Category c = map.get(t.getCategoryId());
            if (c != null && c.getName().toLowerCase().contains(keyword)) {
                isMatchQuery = true;
            }

            if (isMatchQuery) {
                resultTransactions.add(t);
                if (t.getType().equals("income")) incomeSum += t.getAmount();
                else if (t.getType().equals("expense")) expenseSum += t.getAmount();
            }
        }

        totalIncome.setValue(incomeSum);
        totalExpense.setValue(expenseSum);
        totalBalance.setValue(incomeSum - expenseSum);

        Map<String, List<Transaction>> grouped = new TreeMap<>(Collections.reverseOrder());
        for (Transaction t : resultTransactions) {
            grouped.computeIfAbsent(t.getDate(), k -> new ArrayList<>()).add(t);
        }

        List<DateOfTransaction> resultGroupByDate = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : grouped.entrySet()) {
            resultGroupByDate.add(new DateOfTransaction(entry.getKey(), entry.getValue()));
        }
        searchResults.setValue(resultGroupByDate);
    }

    public LiveData<Pair<List<DateOfTransaction>, Map<String, Category>>> getMergedData() {
        return mergedData;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<Double> getTotalBalance() {
        return totalBalance;
    }
}