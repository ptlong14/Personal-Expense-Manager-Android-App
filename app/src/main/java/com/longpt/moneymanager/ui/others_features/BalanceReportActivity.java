package com.longpt.moneymanager.ui.others_features;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.BalanceByMonthAdapter;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.helper.YearSelectorHelper;
import com.longpt.moneymanager.util.YearPickerUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BalanceReportActivity extends AppCompatActivity {

    private CombinedChart chart;
    private TextView tvBack;
    private YearSelectorHelper yearHelper;
    private TextView tvTime, btnPrevTime, btnNextTime, tvTitle;
    private TransactionAnalyticsViewModel viewModel;

    private BalanceByMonthAdapter adapter;
    private RecyclerView rvMonthBalance;
    Map<Integer, Double> mapMonthBalance= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_balance_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        chart = findViewById(R.id.combineChartBalanceReport);
        tvBack = findViewById(R.id.tvBackFromBalanceReport);
        tvTime = findViewById(R.id.tvTimeBalanceReport);
        btnNextTime = findViewById(R.id.btnNextTimeBalanceReport);
        btnPrevTime = findViewById(R.id.btnPrevBalanceReport);
        tvTitle = findViewById(R.id.tvTitleBalanceReport);
        rvMonthBalance= findViewById(R.id.rvMonthlyBalanceReport);

        tvTitle.setText("Báo cáo số dư - " + Calendar.getInstance().get(Calendar.YEAR));
        yearHelper = new YearSelectorHelper(tvTime, btnNextTime);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory factory = new TransactionCalendarViewModelFactory(uid);
        viewModel = new ViewModelProvider(this, factory).get(TransactionAnalyticsViewModel.class);

        viewModel.getIncomeYearlyList().observe(this, incomeEntries -> {
            updateChart();
        });

        viewModel.getExpenseYearlyList().observe(this, expenseEntries -> {
            updateChart();
        });

        viewModel.getBalanceYearlyList().observe(this, balanceEntries -> {
            updateChart();
            updateAdapter(balanceEntries);
        });

        tvBack.setOnClickListener(view -> {
            finish();
        });
        YearPickerUtil.showYearPickerDialog(this, tvTime, yearHelper);
        btnPrevTime.setOnClickListener(v -> {
            yearHelper.prevYear();
        });
        btnNextTime.setOnClickListener(v -> {
            yearHelper.nextYear();
        });
        yearHelper.setOnYearChangeListener(l->updateSelectedTime());
    }

    private void updateAdapter(List<Entry> entries) {
        mapMonthBalance.clear();
        for (Entry e : entries) {
            mapMonthBalance.put((int) e.getX(), (double) e.getY());
        }

        adapter= new BalanceByMonthAdapter(this, mapMonthBalance);
        rvMonthBalance.setLayoutManager(new LinearLayoutManager(this));
        rvMonthBalance.setAdapter(adapter);
    }

    private void updateSelectedTime() {
        int year = yearHelper.getSelectedYear();
        tvTitle.setText("Báo cáo số dư - " + year);
        viewModel.setCurrentYear(year);
        viewModel.updateYearlySeries();
    }

    private void updateChart() {
        List<BarEntry> incomeEntries = viewModel.getIncomeYearlyList().getValue();
        List<BarEntry> expenseEntries = viewModel.getExpenseYearlyList().getValue();
        List<Entry> balanceRawEntries = viewModel.getBalanceYearlyList().getValue();

        if (incomeEntries == null || expenseEntries == null || balanceRawEntries == null) return;

        BarDataSet incomeSet = new BarDataSet(incomeEntries, "Income");
        incomeSet.setColor(Color.GREEN);
        incomeSet.setDrawValues(false);

        // Expense dataset
        BarDataSet expenseSet = new BarDataSet(expenseEntries, "Expense");
        expenseSet.setColor(Color.RED);
        expenseSet.setDrawValues(false);

        // BarData
        BarData barData = new BarData(incomeSet, expenseSet);

        // Months labels
        String[] months = new String[]{"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"};
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // bar group config
        float barWidth = 0.3f;  // rộng hơn
        float barSpace = 0.05f; // hẹp lại
        float groupSpace = 0.3f;
        barData.setBarWidth(barWidth);
        float groupWidth = barData.getGroupWidth(groupSpace, barSpace);

        // ---- Balance Line: dịch vào giữa 2 cột ----
        List<Entry> balanceEntries = new ArrayList<>();
        for (int i = 0; i < balanceRawEntries.size(); i++) {
            float x = i * groupWidth + groupWidth / 2f; // giữa group
            balanceEntries.add(new Entry(x, balanceRawEntries.get(i).getY()));
        }

        LineDataSet balanceSet = new LineDataSet(balanceEntries, "Balance");
        balanceSet.setColor(Color.BLUE);
        balanceSet.setLineWidth(2f);
        balanceSet.setCircleColor(Color.BLUE);
        balanceSet.setCircleRadius(4f);
        balanceSet.setValueTextSize(10f);
        balanceSet.setDrawValues(false);

        LineData lineData = new LineData(balanceSet);

        // Combined chart
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);
        chart.setData(combinedData);
        chart.getAxisRight().setEnabled(false);
        CustomMarkerViewCombine marker = new CustomMarkerViewCombine(this, R.layout.custom_maker_view_combine, months);
        marker.setChartView(chart);
        chart.setMarker(marker);

        // X range
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(0 + groupWidth * months.length);

        chart.getBarData().groupBars(0f, groupSpace, barSpace);

        chart.setDragEnabled(true);
        chart.setVisibleXRangeMaximum(6);

        chart.invalidate();
    }
}