package com.longpt.moneymanager.ui.search_transaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.DateOfTransSearchAdapter;
import com.longpt.moneymanager.adapter.recyclerView.TransInDateSearchAdapter;
import com.longpt.moneymanager.data.factory.TransactionSearchViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.TransactionSearchViewModel;
import com.longpt.moneymanager.ui.manage_transaction.EditTransactionActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchTransactionActivity extends AppCompatActivity {
    private SearchView svAllTime;
    private TextView tvIncome, tvExpense, tvBalance, tvBack, tvFilter, tvTitleSearch;
    private RecyclerView rvDateTrans;
    private DateOfTransSearchAdapter adapter;
    private TransactionSearchViewModel transactionSearchViewModel;
    private final List<Transaction> allList = new ArrayList<>();
    private String currentQuery = "";
    private boolean isAllTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_search_transaction);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        setupViewModel();
        setupRecyclerView();
        observeData();
        setupSearchView();
        setupListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        transactionSearchViewModel.searchTransactions(currentQuery, isAllTime);
    }

    private void setupListener() {
        tvBack.setOnClickListener(v -> finish());
        tvFilter.setOnClickListener(view->{
            FilterBottomSheet filterBottomSheet= new FilterBottomSheet();
            filterBottomSheet.setOnFilterSelectedListener(isAllTime1 -> {
                isAllTime= isAllTime1;
                if(isAllTime){
                    tvTitleSearch.setText("Tìm kiếm (Toàn thời gian)");
                }else {
                    tvTitleSearch.setText("Tìm kiếm (Năm hiện tại)");
                }
                transactionSearchViewModel.searchTransactions(currentQuery, isAllTime);
            });
            filterBottomSheet.show(getSupportFragmentManager(),filterBottomSheet.getTag());
        });
    }

    private void setupSearchView() {
        svAllTime.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                transactionSearchViewModel.searchTransactions(query, isAllTime);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                transactionSearchViewModel.searchTransactions(newText, isAllTime);
                return true;
            }
        });
    }

    private void observeData() {
        transactionSearchViewModel.getMergedData().observe(this, pair -> {
            List<DateOfTransaction> list = pair.first;
            Map<String, Category> map = pair.second;
            updateAdapter(list, map);
        });

        transactionSearchViewModel.getTotalIncome().observe(this, income -> tvIncome.setText(String.format(Locale.getDefault(), "%,.0fđ", income)));

        transactionSearchViewModel.getTotalExpense().observe(this, expense -> tvExpense.setText(String.format(Locale.getDefault(), "%,.0fđ", expense)));

        transactionSearchViewModel.getTotalBalance().observe(this, balance -> tvBalance.setText(String.format(Locale.getDefault(), "%s%,.0fđ", balance >= 0 ? "+" : "-", Math.abs(balance))));

    }

    private void setupRecyclerView() {
        adapter = new DateOfTransSearchAdapter(null, null);
        rvDateTrans.setAdapter(adapter);
    }

    private void setupViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionSearchViewModelFactory transactionSearchViewModelFactory = new TransactionSearchViewModelFactory(uid);
        transactionSearchViewModel = new ViewModelProvider(this, transactionSearchViewModelFactory).get(TransactionSearchViewModel.class);
    }

    private void initView() {
        svAllTime = findViewById(R.id.svAllTime);
        svAllTime.post(() -> {
            svAllTime.setIconified(false);
            svAllTime.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(svAllTime.findFocus(), InputMethodManager.SHOW_IMPLICIT);
            }
        });

        tvTitleSearch= findViewById(R.id.tvTitleSearch);
        tvIncome = findViewById(R.id.tvAllTimeIncome);
        tvExpense = findViewById(R.id.tvAllTimeExpense);
        tvBalance = findViewById(R.id.tvAllTimeBalance);
        rvDateTrans = findViewById(R.id.rvAllTimeDateTrans);
        tvFilter = findViewById(R.id.tvFilter);
        tvBack = findViewById(R.id.tvBackFromSearchTrans);
    }

    private void updateAdapter(List<DateOfTransaction> dateList, Map<String, Category> categoryMap) {
        adapter = new DateOfTransSearchAdapter(dateList, categoryMap);

        adapter.setTransItemSearchListener(new TransInDateSearchAdapter.TransItemSearchListener() {
            @Override
            public void onTransactionClick(Transaction t) {
                Intent intent = new Intent(SearchTransactionActivity.this, EditTransactionActivity.class);
                intent.putExtra("edit_transaction", t);
                startActivity(intent);
            }
        });
        rvDateTrans.setLayoutManager(new LinearLayoutManager(this));
        rvDateTrans.setAdapter(adapter);
    }
}