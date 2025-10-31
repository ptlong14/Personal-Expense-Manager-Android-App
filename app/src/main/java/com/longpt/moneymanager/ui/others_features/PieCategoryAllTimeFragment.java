package com.longpt.moneymanager.ui.others_features;

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
import com.longpt.moneymanager.enums.TypeAnalysis;
import com.longpt.moneymanager.ui.analysis_trans.CustomMarkerViewPie;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PieCategoryAllTimeFragment extends Fragment {
    private static final String ARG_TYPE_PIE = "type";
    private final List<CategorySummary> categorySummaryList = new ArrayList<>();
    private TypeAnalysis pieType;
    private PieChart pieChart;
    private TransactionAnalyticsViewModel transViewModel;
    private RecyclerView rvCategorySummary;
    private CategoryAnalysisOVVAdapter adapter;

    public static PieCategoryAllTimeFragment newInstance(TypeAnalysis type) {
        PieCategoryAllTimeFragment fm = new PieCategoryAllTimeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE_PIE, type);
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
        adapter.setShowNextIcon(false);
        rvCategorySummary.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategorySummary.setAdapter(adapter);

        setupViewModel();

        pieType = (TypeAnalysis) getArguments().getSerializable(ARG_TYPE_PIE);

        if (pieType == TypeAnalysis.INCOME_ANALYSIS) {
            transViewModel.getPieEntriesAllTimeIncome().observe(getViewLifecycleOwner(), data -> {
                setupPieChart(pieChart, data);
            });
            transViewModel.getAllTimeCategoryIncomeSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
        } else if (pieType == TypeAnalysis.EXPENSE_ANALYSIS) {
            transViewModel.getPieEntriesAllTimeExpense().observe(getViewLifecycleOwner(), data -> {
                setupPieChart(pieChart, data);
            });
            transViewModel.getAllTimeCategoryExpenseSummary().observe(getViewLifecycleOwner(), this::updateAdapter);
        }

        return v;
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

    private void updateAdapter(List<CategorySummary> newData) {
        categorySummaryList.clear();
        if (newData != null) {
            categorySummaryList.addAll(newData);
        }
        adapter.notifyDataSetChanged();
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
