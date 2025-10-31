package com.longpt.moneymanager.ui.analysis_trans;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.enums.ScopeAnalysis;
import com.longpt.moneymanager.enums.TypeAnalysis;
import com.longpt.moneymanager.helper.MonthSelectorHelper;
import com.longpt.moneymanager.helper.YearSelectorHelper;
import com.longpt.moneymanager.util.MonthPickerUtil;
import com.longpt.moneymanager.util.YearPickerUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ViewAnalysisTabFragment extends Fragment {
    public static final String ARG_SCOPE = "scope";
    private final ArrayList<PieEntry> pieEntries = new ArrayList<>();
    private ScopeAnalysis scopeAnalysis;
    private TextView tvTime, btnPrevTime, btnNextTime, tvIncome, tvExpense, tvBalance;
    private TabLayout tabHeaderOVV;
    private MonthSelectorHelper monthHelper;
    private YearSelectorHelper yearHelper;
    private TransactionAnalyticsViewModel transViewModel;
    private List<Transaction> incomes = new ArrayList<>();
    private List<Transaction> expenses = new ArrayList<>();
    private boolean isMonthAnalysis = true;


    public static ViewAnalysisTabFragment newInstance(ScopeAnalysis scope) {
        ViewAnalysisTabFragment fm = new ViewAnalysisTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCOPE, scope);
        fm.setArguments(args);
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_analysis_tab, container, false);
        scopeAnalysis = (ScopeAnalysis) getArguments().getSerializable(ARG_SCOPE);

        initView(v);
        setupViewModel();
        observeData();
        setupListener();

        return v;
    }

    private void observeData() {
        transViewModel.getIncomeListTransaction().observe(getViewLifecycleOwner(), incomeList -> {
            incomes = incomeList;
        });
        transViewModel.getExpenseListTransaction().observe(getViewLifecycleOwner(), expenseList -> {
            expenses = expenseList;
        });
        if (isMonthAnalysis) {
            transViewModel.getIncomeMonth().observe(getViewLifecycleOwner(), incomeMonth -> {
                tvIncome.setText(String.format(Locale.getDefault(), "%+,.0f đ", incomeMonth));
            });
            transViewModel.getExpenseMonth().observe(getViewLifecycleOwner(), expenseMonth -> {
                String formatted;
                if (expenseMonth == 0) {
                    formatted = "-0 đ";
                } else {
                    formatted = "-" + String.format(Locale.getDefault(), "%,.0f đ", expenseMonth);
                }
                tvExpense.setText(formatted);
            });
            transViewModel.getBalanceMonth().observe(getViewLifecycleOwner(), balanceMonth -> {
                tvBalance.setText(String.format(Locale.getDefault(), "%+,.0f đ", balanceMonth));
                if (balanceMonth < 0) {
                    tvBalance.setTextColor(Color.RED);
                } else {
                    tvBalance.setTextColor(Color.BLUE);
                }
            });

        } else {
            transViewModel.getIncomeYear().observe(getViewLifecycleOwner(), incomeYear -> {
                tvIncome.setText(String.format(Locale.getDefault(), "%+,.0f đ", incomeYear));
            });
            transViewModel.getExpenseYear().observe(getViewLifecycleOwner(), expenseYear -> {
                String formatted;
                if (expenseYear == 0) {
                    formatted = "-0 đ";
                } else {
                    formatted = "-" + String.format(Locale.getDefault(), "%,.0f đ", expenseYear);
                }
                tvExpense.setText(formatted);
            });
            transViewModel.getBalanceYear().observe(getViewLifecycleOwner(), balanceYear -> {
                tvBalance.setText(String.format(Locale.getDefault(), "%+,.0f đ", balanceYear));
                if (balanceYear < 0) {
                    tvBalance.setTextColor(Color.RED);
                } else {
                    tvBalance.setTextColor(Color.BLUE);
                }
            });
        }
    }

    private void setupViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory factory = new TransactionCalendarViewModelFactory(uid);
        transViewModel = new ViewModelProvider(requireActivity(), factory).get(TransactionAnalyticsViewModel.class);
    }

    private void setupListener() {
        if (isMonthAnalysis) {
            MonthPickerUtil.showMonthPickerDialog(requireContext(), tvTime,
                    chosen->monthHelper.setSelectedMonth(chosen),
                    ()->monthHelper.getSelectedMonth()
                    );
            btnPrevTime.setOnClickListener(v -> {
                monthHelper.prevMonth();
            });
            btnNextTime.setOnClickListener(v -> {
                monthHelper.nextMonth();
            });
            monthHelper.setOnMonthChangeListener(l->{
                updateSelectedTime();
            });
        } else {
            YearPickerUtil.showYearPickerDialog(requireContext(), tvTime, yearHelper);
            btnPrevTime.setOnClickListener(v -> {
                yearHelper.prevYear();
            });
            btnNextTime.setOnClickListener(v -> {
                yearHelper.nextYear();
            });
            yearHelper.setOnYearChangeListener(v->{
                updateSelectedTime();
            });
        }

        tabHeaderOVV.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TypeAnalysis type = tab.getPosition() == 0 ? TypeAnalysis.EXPENSE_ANALYSIS : TypeAnalysis.INCOME_ANALYSIS;
                Fragment selectedFragment = PieTransactionFragment.newInstance(type, scopeAnalysis);

                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tabContentChildOVV, selectedFragment)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateSelectedTime() {
        if (isMonthAnalysis) {
            LocalDate date = LocalDate.of(monthHelper.getSelectedMonth().get(Calendar.YEAR), monthHelper.getSelectedMonth().get(Calendar.MONTH) + 1, 1);
            transViewModel.setCurrentMonth(date);
            transViewModel.updateMonthOverview();
            transViewModel.updateMonthIncomeDataPieChart();
            transViewModel.updateMonthExpenseDataPieChart();
            transViewModel.updateMonthExpenseCategorySummary();
            transViewModel.updateMonthIncomeCategorySummary();
        } else {
            int year = yearHelper.getSelectedYear();
            transViewModel.setCurrentYear(year);
            transViewModel.updateYearOverview();
            transViewModel.updateYearIncomeDataPieChart();
            transViewModel.updateYearExpenseDataPieChart();
            transViewModel.updateYearExpenseCategorySummary();
            transViewModel.updateYearIncomeCategorySummary();
        }
    }

    private void initView(View v) {
        isMonthAnalysis = scopeAnalysis == ScopeAnalysis.MONTH_ANALYSIS;

        tvTime = v.findViewById(R.id.tvTimeOVV);
        btnNextTime = v.findViewById(R.id.btnNextTimeOVV);
        btnPrevTime = v.findViewById(R.id.btnPrevTimeOVV);

        if (isMonthAnalysis) {
            monthHelper = new MonthSelectorHelper(tvTime, btnNextTime);
        } else {
            yearHelper = new YearSelectorHelper(tvTime, btnNextTime);
        }

        tvIncome = v.findViewById(R.id.tvIncomeOVV);
        tvExpense = v.findViewById(R.id.tvExpenseOVV);
        tvBalance = v.findViewById(R.id.tvBalanceOVV);

        tabHeaderOVV= v.findViewById(R.id.tabHeaderChildOVV);
        tabHeaderOVV.addTab(tabHeaderOVV.newTab().setText("Chi tiêu"));
        tabHeaderOVV.addTab(tabHeaderOVV.newTab().setText("Thu nhập"));
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.tabContentChildOVV, PieTransactionFragment.newInstance(TypeAnalysis.EXPENSE_ANALYSIS, scopeAnalysis))
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        transViewModel.loadData();
    }
}
