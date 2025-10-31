package com.longpt.moneymanager.ui.analysis_trans;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.CategoryTotalByMonthAdapter;
import com.longpt.moneymanager.adapter.recyclerView.DateOfTransCalendarAdapter;
import com.longpt.moneymanager.adapter.recyclerView.TransInDateCalendarAdapter;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.enums.ScopeAnalysis;
import com.longpt.moneymanager.ui.manage_transaction.EditTransactionActivity;
import com.longpt.moneymanager.ui.manage_transaction.TransactionResultCallback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoryAmountDetailActivity extends AppCompatActivity {
    private TextView tvBack, tvTitle;
    private BarChart barChart;
    private TransactionAnalyticsViewModel viewModel;
    private String categoryId, categoryName;
    private ScopeAnalysis scope;
    private RecyclerView recyclerViewDateTransactions;
    private DateOfTransCalendarAdapter dateOfTransCalendarAdapter;
    private CategoryTotalByMonthAdapter categoryTotalByMonthAdapter;
    private Map<Integer, Double> mapCategoryTotalMonthly;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_category_amount_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvBack= findViewById(R.id.tvBackFromDetail);
        tvTitle= findViewById(R.id.tvTitleDetail);
        barChart = findViewById(R.id.barCategoryAmount);
        recyclerViewDateTransactions = findViewById(R.id.rvCategoryAmount);
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");
        scope = (ScopeAnalysis) getIntent().getSerializableExtra("scope");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory factory = new TransactionCalendarViewModelFactory(uid);
        viewModel = new ViewModelProvider(this, factory).get(TransactionAnalyticsViewModel.class);

        if (scope == ScopeAnalysis.MONTH_ANALYSIS) {
            LocalDate month = (LocalDate) getIntent().getSerializableExtra("month");
            tvTitle.setText(categoryName+ " - T"+ month.getMonthValue()+"/"+month.getYear());
            viewModel.setCurrentMonth(month);
            observeIncomeExpenseList();
            observeBarEntries(viewModel.getBarEntriesCategoryForMonth());
            viewModel.getMergedTransactionWithCategory().observe(this, pair -> {
                List<DateOfTransaction> filteredList = new ArrayList<>();
                for (DateOfTransaction dateItem : pair.first) {
                    List<Transaction> filteredTransactions = new ArrayList<>();
                    for (Transaction t : dateItem.getTransactions()) {
                        if (categoryId.equals(t.getCategoryId())) {
                            filteredTransactions.add(t);
                        }
                    }
                    if (!filteredTransactions.isEmpty()) {
                        filteredList.add(new DateOfTransaction(dateItem.getDate(), filteredTransactions));
                    }
                }
                Map<String, Category> mapCategory = pair.second;
                updateMonthAdapter(filteredList, mapCategory);
            });
        } else {
            int year = getIntent().getIntExtra("year", LocalDate.now().getYear());
            tvTitle.setText(categoryName+ " - "+ year);

            viewModel.setCurrentYear(year);
            observeIncomeExpenseList();
            observeBarEntries(viewModel.getBarEntriesCategoryForYear());
            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    int month = (int) e.getX();
                    int year = viewModel.getCurrentYear();
                    Intent intent = new Intent(CategoryAmountDetailActivity.this, CategoryAmountDetailActivity.class);
                    intent.putExtra("categoryId", categoryId);
                    intent.putExtra("scope", ScopeAnalysis.MONTH_ANALYSIS);
                    intent.putExtra("month", LocalDate.of(year, month, 1));
                    intent.putExtra("categoryName", categoryName);
                    startActivity(intent);
                }

                @Override
                public void onNothingSelected() {
                }
            });
            viewModel.getCategoryTotalByMonth().observe(this, this::updateYearAdapter);
        }
        tvBack.setOnClickListener(view->{
            finish();
        });
    }

    private void updateYearAdapter(Map<Integer, Double> map) {
        categoryTotalByMonthAdapter = new CategoryTotalByMonthAdapter(map, this);
        categoryTotalByMonthAdapter.setListener(new CategoryTotalByMonthAdapter.CategoryTotalMonthlyItemListener() {
            @Override
            public void onItemMonthlyClick(View v, int position) {
                int month = new ArrayList<>(map.keySet()).get(position);
                int year = viewModel.getCurrentYear();

                // Mở Activity để hiển thị dữ liệu của tháng đó
                Intent intent = new Intent(CategoryAmountDetailActivity.this, CategoryAmountDetailActivity.class);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("scope", ScopeAnalysis.MONTH_ANALYSIS);
                intent.putExtra("month", LocalDate.of(year, month, 1));
                intent.putExtra("categoryName", categoryName);
                startActivity(intent);
            }
        });

        recyclerViewDateTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDateTransactions.setAdapter(categoryTotalByMonthAdapter);
    }

    private void observeIncomeExpenseList() {
        viewModel.getIncomeListTransaction().observe(this, income -> tryUpdateBarChart(categoryId));
        viewModel.getExpenseListTransaction().observe(this, expense -> tryUpdateBarChart(categoryId));
    }

    private void observeBarEntries(LiveData<List<BarEntry>> liveData) {
        liveData.observe(this, entries -> {
            if (entries == null || entries.isEmpty()) {
                barChart.clear();
                barChart.setNoDataText("Không có dữ liệu để hiển thị");
            } else {
                BarDataSet dataSet = new BarDataSet(entries, "Tổng giao dịch theo ngày");
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.BLACK);
                BarData data = new BarData(dataSet);
                data.setBarWidth(0.9f);
                barChart.setData(data);
            }
            barChart.invalidate();
        });
    }

    private void tryUpdateBarChart(String categoryId) {
        if (viewModel.getIncomeListTransaction().getValue() != null && viewModel.getExpenseListTransaction().getValue() != null) {
            if (scope == ScopeAnalysis.MONTH_ANALYSIS) {
                viewModel.updateBarChartDataByCategoryForMonth(categoryId);
            } else {
                viewModel.updateBarChartDataByCategoryForYear(categoryId);
            }
        }
    }

    private void updateMonthAdapter(List<DateOfTransaction> dateList, Map<String, Category> categoryMap) {
        dateOfTransCalendarAdapter = new DateOfTransCalendarAdapter(dateList, categoryMap);
        dateOfTransCalendarAdapter.setTransactionItemListener(new TransInDateCalendarAdapter.TransItemCalendarListener() {
            @Override
            public void onTransactionClick(Transaction t) {
                Intent intent = new Intent(CategoryAmountDetailActivity.this, EditTransactionActivity.class);
                intent.putExtra("edit_transaction", t);
                startActivity(intent);
            }

            @Override
            public void onDeleteTransactionClick(Transaction transaction) {
                new AlertDialog.Builder(CategoryAmountDetailActivity.this).setTitle("Xác nhận xóa").setMessage("Bạn có chắc muốn xóa '" + transaction.getAmount() + "'?").setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.deleteTransaction(transaction.getId(), new TransactionResultCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(CategoryAmountDetailActivity.this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            viewModel.loadData();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CategoryAmountDetailActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }).setNegativeButton("Hủy", ((dialog, which) -> {
                    if (recyclerViewDateTransactions.getAdapter() instanceof DateOfTransCalendarAdapter) {
                        DateOfTransCalendarAdapter parentAdapter = (DateOfTransCalendarAdapter) recyclerViewDateTransactions.getAdapter();
                        parentAdapter.getGlobalBinderHelper().closeLayout(transaction.getId());
                    }
                })).show();
            }
        });
        recyclerViewDateTransactions.setLayoutManager(new LinearLayoutManager(CategoryAmountDetailActivity.this));
        recyclerViewDateTransactions.setAdapter(dateOfTransCalendarAdapter);
    }
}