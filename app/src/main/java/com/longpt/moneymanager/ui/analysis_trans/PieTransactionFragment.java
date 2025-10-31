package com.longpt.moneymanager.ui.analysis_trans;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.CategoryAnalysisOVVAdapter;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.CategorySummary;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.enums.ScopeAnalysis;
import com.longpt.moneymanager.enums.TypeAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PieTransactionFragment extends Fragment implements CategoryAnalysisOVVAdapter.CategorySummaryItemListener {
    private static final String ARG_TYPE_PIE = "type";
    private static final String ARG_SCOPE = "scope";
    private final List<CategorySummary> categorySummaryList = new ArrayList<>();
    private TypeAnalysis pieType;
    private ScopeAnalysis pieScope;
    private PieChart pieChart;
    private TransactionAnalyticsViewModel transViewModel;
    private RecyclerView rvCategorySummary;
    private CategoryAnalysisOVVAdapter adapter;

    public static PieTransactionFragment newInstance(TypeAnalysis type, ScopeAnalysis scope) {
        PieTransactionFragment fm = new PieTransactionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE_PIE, type);
        args.putSerializable(ARG_SCOPE, scope);
        fm.setArguments(args);
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_pie_transaction, container, false);
        pieChart = v.findViewById(R.id.pieOVV);

        rvCategorySummary = v.findViewById(R.id.rvTransOVV);
        adapter = new CategoryAnalysisOVVAdapter(categorySummaryList, getContext());
        adapter.setShowNextIcon(true);
        adapter.setListener(this);

        rvCategorySummary.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategorySummary.setAdapter(adapter);

        setupViewModel();

        pieType = (TypeAnalysis) getArguments().getSerializable(ARG_TYPE_PIE);
        pieScope = (ScopeAnalysis) getArguments().getSerializable(ARG_SCOPE);


        if (pieScope == ScopeAnalysis.MONTH_ANALYSIS) {
            if (pieType == TypeAnalysis.EXPENSE_ANALYSIS) {
                transViewModel.getPieEntriesMonthExpense().observe(getViewLifecycleOwner(), data -> {
                    setupPieChart(pieChart, data);
                });
                transViewModel.getMonthExpenseSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
            } else if (pieType == TypeAnalysis.INCOME_ANALYSIS) {
                transViewModel.getPieEntriesMonthIncome().observe(getViewLifecycleOwner(), data -> {
                    setupPieChart(pieChart, data);
                });
                transViewModel.getMonthIncomeSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
            }
        } else if (pieScope == ScopeAnalysis.YEAR_ANALYSIS) {
            if (pieType == TypeAnalysis.EXPENSE_ANALYSIS) {
                transViewModel.getPieEntriesYearExpense().observe(getViewLifecycleOwner(), data -> {
                    setupPieChart(pieChart, data);
                });
                transViewModel.getYearExpenseSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
            } else if (pieType == TypeAnalysis.INCOME_ANALYSIS) {
                transViewModel.getPieEntriesYearIncome().observe(getViewLifecycleOwner(), data -> {
                    setupPieChart(pieChart, data);
                });
                transViewModel.getYearIncomeSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
            }
        }

        return v;
    }


    private void updateAdapter(List<CategorySummary> newData) {
        categorySummaryList.clear();
        if (newData != null) {
            categorySummaryList.addAll(newData);
        }
        adapter.notifyDataSetChanged();
    }


    private void setupViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory factory = new TransactionCalendarViewModelFactory(uid);
        transViewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionAnalyticsViewModel.class);
    }

    private void setupPieChart(PieChart pieChart, List<PieEntry> pieEntries) {
        if (pieEntries == null || pieEntries.isEmpty()) {
            pieChart.clear();
            pieChart.setNoDataText("Không có dữ liệu để hiển thị");
            pieChart.setNoDataTextColor(Color.RED);
            pieChart.invalidate();
            return;
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry entry) {
                return value < 10f ? "" : String.format(Locale.getDefault(), "%.1f%%", value);
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setDrawEntryLabels(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);

        CustomMarkerViewPie markerView = new CustomMarkerViewPie(requireContext(), R.layout.custom_maker_view_pie, pieEntries);
        markerView.setChartView(pieChart);
        pieChart.setMarker(markerView);

        pieChart.invalidate();
    }

    @Override
    public void onCategorySummaryClick(View v, int position) {
        if (position >= 0 && position < categorySummaryList.size()) {
            CategorySummary category = categorySummaryList.get(position);

            Intent intent = new Intent(getContext(), CategoryAmountDetailActivity.class);
            intent.putExtra("categoryId", category.getCategory().getId());
            intent.putExtra("scope", pieScope);
            intent.putExtra("categoryName", category.getCategory().getName());
            if (pieScope == ScopeAnalysis.MONTH_ANALYSIS) {
                intent.putExtra("month", transViewModel.getCurrentMonth());
            } else {
                intent.putExtra("year", transViewModel.getCurrentYear());
            }
            startActivity(intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (pieChart != null) {
            pieChart.highlightValues(null);
            pieChart.setMarker(null);
        }
    }
}
