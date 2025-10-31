package com.longpt.moneymanager.ui.calendar_view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.kizitonwose.calendarview.CalendarView;
import com.kizitonwose.calendarview.model.CalendarDay;
import com.kizitonwose.calendarview.model.DayOwner;
import com.kizitonwose.calendarview.model.ScrollMode;
import com.kizitonwose.calendarview.ui.DayBinder;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.DateOfTransCalendarAdapter;
import com.longpt.moneymanager.adapter.recyclerView.TransInDateCalendarAdapter;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.helper.MonthCalendarSelectorHelper;
import com.longpt.moneymanager.ui.manage_transaction.EditTransactionActivity;
import com.longpt.moneymanager.ui.manage_transaction.TransactionResultCallback;
import com.longpt.moneymanager.ui.search_transaction.SearchTransactionActivity;
import com.longpt.moneymanager.util.DateConverter;
import com.longpt.moneymanager.util.MonthPickerUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {
    private CalendarView calendarView;

    private TextView tvMonth, btnPrevMonth, btnNextMonth, tvIncomeMonth, tvExpenseMonth, tvBalanceMonth, tvSearchAllTime;
    private RecyclerView recyclerViewDateTransactions;
    private DateOfTransCalendarAdapter adapter;

    private MonthCalendarSelectorHelper monthHelper;

    private TransactionAnalyticsViewModel transactionAnalyticsViewModel;

    private List<Transaction> incomeList = new ArrayList<>();
    private List<Transaction> expenseList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_calendar, container, false);
        initView(v);
        setupViewModel();
        setupCalendar();
        observeData();

        setupListener();

        return v;
    }

    private void setupListener() {
        btnPrevMonth.setOnClickListener(view -> {
            monthHelper.prevMonth();
            updateSelectedMonth();
        });

        btnNextMonth.setOnClickListener(view -> {
            monthHelper.nextMonth();
            updateSelectedMonth();
        });

        tvSearchAllTime.setOnClickListener(view -> {
            Intent t = new Intent(requireContext(), SearchTransactionActivity.class);
            startActivity(t);
        });
        MonthPickerUtil.showMonthPickerDialog(requireContext(),tvMonth,
            chosen-> monthHelper.setSelectedMonth(chosen),
                ()->monthHelper.getSelectedMonth()
                );
    }

    private void setupViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory transactionCalendarViewModelFactory = new TransactionCalendarViewModelFactory(uid);
        transactionAnalyticsViewModel = new ViewModelProvider(requireActivity(), transactionCalendarViewModelFactory).get(TransactionAnalyticsViewModel.class);
    }

    private void initView(View v) {
        calendarView = v.findViewById(R.id.calendarView);
        tvMonth = v.findViewById(R.id.tvMonth);
        btnPrevMonth = v.findViewById(R.id.btnPrevMonth_calendar_ovv);
        btnNextMonth = v.findViewById(R.id.btnNextMonth);

        tvSearchAllTime = v.findViewById(R.id.tvSearchAllTime);
        tvIncomeMonth = v.findViewById(R.id.tvCalendarIncome);
        tvExpenseMonth = v.findViewById(R.id.tvCalendarExpense);
        tvBalanceMonth = v.findViewById(R.id.tvCalendarSum);

        recyclerViewDateTransactions = v.findViewById(R.id.rvDayTransactions);

        monthHelper = new MonthCalendarSelectorHelper(tvMonth, btnNextMonth, calendarView);
    }

    private void observeData() {
        transactionAnalyticsViewModel.getIncomeListTransaction().observe(getViewLifecycleOwner(), incomes -> {
            incomeList = incomes;
            calendarView.notifyCalendarChanged();

        });

        transactionAnalyticsViewModel.getExpenseListTransaction().observe(getViewLifecycleOwner(), expenses -> {
            expenseList = expenses;
            calendarView.notifyCalendarChanged();

        });

        transactionAnalyticsViewModel.getIncomeMonth().observe(getViewLifecycleOwner(), incomeMonth -> {
            tvIncomeMonth.setText(String.format(Locale.getDefault(), "%+,.0f đ", incomeMonth));
        });

        transactionAnalyticsViewModel.getExpenseMonth().observe(getViewLifecycleOwner(), expenseMonth -> {
            String formatted;
            if (expenseMonth == 0) {
                formatted = "-0 đ";
            } else {
                formatted = "-" + String.format(Locale.getDefault(), "%,.0f đ", expenseMonth);
            }
            tvExpenseMonth.setText(formatted);
        });

        transactionAnalyticsViewModel.getBalanceMonth().observe(getViewLifecycleOwner(), balanceMonth -> {
            tvBalanceMonth.setText(String.format(Locale.getDefault(), "%+,.0f đ", balanceMonth));
            if (balanceMonth < 0) {
                tvBalanceMonth.setTextColor(Color.RED);
            } else {
                tvBalanceMonth.setTextColor(Color.BLUE);
            }
        });

        transactionAnalyticsViewModel.getMergedTransactionWithCategory().observe(getViewLifecycleOwner(), pair -> {
            List<DateOfTransaction> list = pair.first;
            Map<String, Category> map = pair.second;
            updateAdapter(list, map);
        });
    }

    private void updateAdapter(List<DateOfTransaction> dateList, Map<String, Category> categoryMap) {
        adapter = new DateOfTransCalendarAdapter(dateList, categoryMap);
        adapter.setTransactionItemListener(new TransInDateCalendarAdapter.TransItemCalendarListener() {
            @Override
            public void onTransactionClick(Transaction t) {
                Intent intent = new Intent(requireContext(), EditTransactionActivity.class);
                intent.putExtra("edit_transaction", t);
                startActivity(intent);
            }

            @Override
            public void onDeleteTransactionClick(Transaction transaction) {
                new AlertDialog.Builder(requireContext()).setTitle("Xác nhận xóa").setMessage("Bạn có chắc muốn xóa '" + transaction.getAmount() + "'?").setPositiveButton("Xóa", (dialog, which) -> {
                    transactionAnalyticsViewModel.deleteTransaction(transaction.getId(), new TransactionResultCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(requireContext(), "Xóa thành công!", Toast.LENGTH_SHORT).show();
                            transactionAnalyticsViewModel.loadData();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(requireContext(), "Xóa thất bại!", Toast.LENGTH_SHORT).show();
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
        recyclerViewDateTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewDateTransactions.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.closeOpenedSwipe();
        }
    }

    private void setupCalendar() {
        calendarView.setOrientation(CalendarView.HORIZONTAL);
        calendarView.setScrollMode(ScrollMode.PAGED);

        YearMonth currentMonth = YearMonth.from(LocalDate.now());
        YearMonth startMonth = currentMonth.minusYears(20);
        calendarView.setup(startMonth, currentMonth, DayOfWeek.MONDAY);
        calendarView.scrollToMonth(currentMonth);
        calendarView.setDayBinder(new DayBinder<DayViewCalendarContainer>() {
            @NonNull
            @Override
            public DayViewCalendarContainer create(@NonNull View view) {
                return new DayViewCalendarContainer(view);
            }

            @Override
            public void bind(@NonNull DayViewCalendarContainer container, CalendarDay day) {
                container.tvDay.setText(String.valueOf(day.getDate().getDayOfMonth()));

                if (day.getOwner() == DayOwner.THIS_MONTH) {
                    if (day.getDate().equals(LocalDate.now())) {
                        container.tvDay.setTypeface(null, Typeface.BOLD);
                    } else {
                        container.getView().setBackgroundResource(R.drawable.bg_item_day_in_month);
                    }
                } else {
                    container.getView().setBackgroundResource(R.drawable.bg_item_day_notin_month);
                }
                DayOfWeek dayOfWeek = day.getDate().getDayOfWeek();
                if (dayOfWeek == DayOfWeek.SATURDAY) {
                    container.tvDay.setTextColor(Color.BLUE);
                } else if (dayOfWeek == DayOfWeek.SUNDAY) {
                    container.tvDay.setTextColor(Color.RED);
                } else {
                    container.tvDay.setTextColor(Color.BLACK);
                }

                container.tvIncome.setText("");
                container.tvExpense.setText("");
                double incomeDay = transactionAnalyticsViewModel.getIncomeAndExpenseAtDate(incomeList, day.getDate());
                double expenseDay = transactionAnalyticsViewModel.getIncomeAndExpenseAtDate(expenseList, day.getDate());

                container.tvIncome.setText(incomeDay > 0 ? String.format(Locale.getDefault(), "%,.0f", incomeDay) : "");
                container.tvExpense.setText(expenseDay > 0 ? String.format(Locale.getDefault(), "%,.0f", expenseDay) : "");

                container.getView().setOnClickListener(v -> {
                    if (day.getOwner() == DayOwner.THIS_MONTH) {
                        LocalDate selectedDate = day.getDate();
                        LocalDate today = LocalDate.now();

                        if (selectedDate.isAfter(today)) {
                            selectedDate = today;
                        }
                        Calendar calendar= new DateConverter().localDateToCalendar(selectedDate);
                        DayTransactionBottomSheet.newInstance(calendar).show(getParentFragmentManager(), "DayTransactionBottomSheet");
                    }
                });
            }
        });
        calendarView.setMonthScrollListener(month -> {
            YearMonth yearMonth = month.getYearMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, yearMonth.getYear());
            calendar.set(Calendar.MONTH, yearMonth.getMonthValue() - 1);
            monthHelper.setSelectedMonth(calendar);
            transactionAnalyticsViewModel.setCurrentMonth(LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1));
            transactionAnalyticsViewModel.updateMonthOverview();
            transactionAnalyticsViewModel.updateDateOfTransaction();
            return null;
        });
    }

    private void updateSelectedMonth() {
        LocalDate date = LocalDate.of(monthHelper.getSelectedMonth().get(Calendar.YEAR), monthHelper.getSelectedMonth().get(Calendar.MONTH) + 1, 1);
        transactionAnalyticsViewModel.setCurrentMonth(date);
        transactionAnalyticsViewModel.updateMonthOverview();
    }
}