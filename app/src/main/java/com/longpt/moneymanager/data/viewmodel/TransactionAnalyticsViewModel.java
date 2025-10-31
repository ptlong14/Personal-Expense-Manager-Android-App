package com.longpt.moneymanager.data.viewmodel;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.CategorySummary;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.repository.CategoryRepository;
import com.longpt.moneymanager.data.repository.TransactionRepository;
import com.longpt.moneymanager.ui.manage_transaction.TransactionResultCallback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TransactionAnalyticsViewModel extends ViewModel {
    private final MutableLiveData<List<Transaction>> incomeList = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> expenseList = new MutableLiveData<>();
    private final MutableLiveData<Double> incomeMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> expenseMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> balanceMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> incomeYear = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> expenseYear = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> balanceYear = new MutableLiveData<>(0.0);
    private final MediatorLiveData<Double> incomeAllTime = new MediatorLiveData<>();
    private final MediatorLiveData<Double> expenseAllTime = new MediatorLiveData<>();
    private final MediatorLiveData<Double> balanceAllTime = new MediatorLiveData<>();

    private final MutableLiveData<List<DateOfTransaction>> dateOfTransactions = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Category>> categoryMap = new MutableLiveData<>();
    private final MediatorLiveData<Pair<List<DateOfTransaction>, Map<String, Category>>> mergedData = new MediatorLiveData<>();
    private final MutableLiveData<List<PieEntry>> pieEntriesMonthIncome = new MutableLiveData<>();
    private final MutableLiveData<List<PieEntry>> pieEntriesMonthExpense = new MutableLiveData<>();
    private final MutableLiveData<List<PieEntry>> pieEntriesYearIncome = new MutableLiveData<>();
    private final MutableLiveData<List<PieEntry>> pieEntriesYearExpense = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> monthCategoryIncomeSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> monthCategoryExpenseSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> yearCategoryIncomeSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> yearCategoryExpenseSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> allTimeCategoryIncomeSummary = new MutableLiveData<>();
    private final MutableLiveData<List<CategorySummary>> allTimeCategoryExpenseSummary = new MutableLiveData<>();

    private final MutableLiveData<List<BarEntry>> barEntriesCategoryForMonth = new MutableLiveData<>();
    private final MutableLiveData<List<BarEntry>> barEntriesCategoryForYear = new MutableLiveData<>();
    private final MutableLiveData<Map<Integer, Double>> categoryTotalByMonth = new MutableLiveData<>();
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final MutableLiveData<List<PieEntry>> pieEntriesAllTimeIncome = new MediatorLiveData<>();
    private final MutableLiveData<List<PieEntry>> pieEntriesAllTimeExpense = new MediatorLiveData<>();

    private final MutableLiveData<List<BarEntry>> incomeYearlyList = new MutableLiveData<>();
    private final MutableLiveData<List<BarEntry>> expenseYearlyList = new MutableLiveData<>();
    private final MutableLiveData<List<Entry>> balanceYearlyList = new MutableLiveData<>();

    private LocalDate currentMonth = LocalDate.now();
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR);

    public TransactionAnalyticsViewModel(String uid) {
        transactionRepository = new TransactionRepository(uid);
        categoryRepository = new CategoryRepository(uid);

        loadData();
        mergedData.addSource(dateOfTransactions, list -> mergeTransactionWithCategory());
        mergedData.addSource(categoryMap, map -> mergeTransactionWithCategory());

        incomeAllTime.addSource(incomeList, incomes -> {
            incomeAllTime.setValue(sumTotalAmount(incomes));
            updateBalanceAllTime();
        });

        expenseAllTime.addSource(expenseList, expenses -> {
            expenseAllTime.setValue(sumTotalAmount(expenses));
            updateBalanceAllTime();
        });
    }

    public LiveData<List<BarEntry>> getBarEntriesCategoryForMonth() {
        return barEntriesCategoryForMonth;
    }

    public LiveData<List<BarEntry>> getBarEntriesCategoryForYear() {
        return barEntriesCategoryForYear;
    }

    private void mergeTransactionWithCategory() {
        List<DateOfTransaction> list = dateOfTransactions.getValue();
        Map<String, Category> map = categoryMap.getValue();
        if (list != null && map != null) {
            mergedData.setValue(new Pair<>(list, map));
        }
    }

    public void addTransaction(Transaction transaction, TransactionResultCallback callback) {
        transactionRepository.addTransaction(transaction, callback);
    }

    public void updateTransaction(Transaction transaction, TransactionResultCallback callback) {
        transactionRepository.updateTransaction(transaction, callback);
    }

    public void deleteTransaction(String id, TransactionResultCallback callback) {
        transactionRepository.deleteTransaction(id, callback);
    }

    public LiveData<List<Transaction>> getIncomeListTransaction() {
        return incomeList;
    }

    public LiveData<List<Transaction>> getExpenseListTransaction() {
        return expenseList;
    }

    public LiveData<Double> getIncomeMonth() {
        return incomeMonth;
    }

    public LiveData<Double> getExpenseMonth() {
        return expenseMonth;
    }

    public LiveData<Double> getBalanceMonth() {
        return balanceMonth;
    }

    public LiveData<Double> getIncomeYear() {
        return incomeYear;
    }

    public LiveData<Double> getExpenseYear() {
        return expenseYear;
    }

    public LiveData<Double> getBalanceYear() {
        return balanceYear;
    }

    public LiveData<Double> getExpenseAllTime() {
        return expenseAllTime;
    }

    public LiveData<Double> getIncomeAllTime() {
        return incomeAllTime;
    }

    public LiveData<Double> getBalanceAllTime() {
        return balanceAllTime;
    }

    public LiveData<List<PieEntry>> getPieEntriesAllTimeIncome() {
        return pieEntriesAllTimeIncome;
    }

    public LiveData<List<PieEntry>> getPieEntriesAllTimeExpense() {
        return pieEntriesAllTimeExpense;
    }

    public LiveData<List<PieEntry>> getPieEntriesMonthIncome() {
        return pieEntriesMonthIncome;
    }

    public LiveData<List<PieEntry>> getPieEntriesMonthExpense() {
        return pieEntriesMonthExpense;
    }

    public LiveData<List<PieEntry>> getPieEntriesYearIncome() {
        return pieEntriesYearIncome;
    }

    public LiveData<List<PieEntry>> getPieEntriesYearExpense() {
        return pieEntriesYearExpense;
    }

    public LiveData<List<CategorySummary>> getMonthIncomeSummary() {
        return monthCategoryIncomeSummary;
    }

    public LiveData<List<CategorySummary>> getMonthExpenseSummary() {
        return monthCategoryExpenseSummary;
    }

    public LiveData<List<CategorySummary>> getYearIncomeSummary() {
        return yearCategoryIncomeSummary;
    }

    public LiveData<List<CategorySummary>> getYearExpenseSummary() {
        return yearCategoryExpenseSummary;
    }

    public LiveData<List<CategorySummary>> getAllTimeCategoryIncomeSummary() {
        return allTimeCategoryIncomeSummary;
    }

    public LiveData<List<CategorySummary>> getAllTimeCategoryExpenseSummary() {
        return allTimeCategoryExpenseSummary;
    }

    public LiveData<List<BarEntry>> getExpenseYearlyList() {
        return expenseYearlyList;
    }

    public LiveData<List<BarEntry>> getIncomeYearlyList() {
        return incomeYearlyList;
    }

    public LiveData<List<Entry>> getBalanceYearlyList() {
        return balanceYearlyList;
    }

    public LiveData<Map<Integer, Double>> getCategoryTotalByMonth() {
        return categoryTotalByMonth;
    }

    public LiveData<Pair<List<DateOfTransaction>, Map<String, Category>>> getMergedTransactionWithCategory() {
        return mergedData;
    }

    public LocalDate getCurrentMonth() {
        return currentMonth;
    }

    public void setCurrentMonth(LocalDate date) {
        this.currentMonth = date;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void loadData() {
        transactionRepository.getTransactions(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Transaction> income = new ArrayList<>();
                List<Transaction> expense = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Transaction transaction = child.getValue(Transaction.class);
                    if (transaction != null && transaction.getType() != null) {
                        if (transaction.getType().equals("income")) {
                            income.add(transaction);
                        } else if (transaction.getType().equals("expense")) {
                            expense.add(transaction);
                        }
                    }
                }
                incomeList.setValue(income);
                expenseList.setValue(expense);

                loadCategories();
                updateMonthOverview();
                updateYearOverview();
                updateYearlySeries();
                updateDateOfTransaction();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TransactionAnalyticsViewModel", "Firebase error: " + error.getMessage());
            }
        });
    }

    private void loadCategories() {
        categoryRepository.getAndMapCategories("income", new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Category> map = new HashMap<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category c = child.getValue(Category.class);
                    if (c != null && c.getId() != null) {
                        map.put(c.getId(), c);
                    }
                }

                categoryRepository.getAndMapCategories("expense", new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot2) {
                        for (DataSnapshot child : snapshot2.getChildren()) {
                            Category c = child.getValue(Category.class);
                            if (c != null && c.getId() != null) {
                                map.put(c.getId(), c);
                            }
                        }
                        categoryMap.setValue(map);
                        updateMonthIncomeDataPieChart();
                        updateMonthExpenseDataPieChart();
                        updateYearIncomeDataPieChart();
                        updateYearExpenseDataPieChart();
                        updateMonthExpenseCategorySummary();
                        updateMonthIncomeCategorySummary();
                        updateYearIncomeCategorySummary();
                        updateYearExpenseCategorySummary();
                        updateAllTimeIncomeCategorySummary();
                        updateAllTimeExpenseCategorySummary();
                        updateAllTimeIncomeDataPieChart();
                        updateAllTimeExpenseDataPieChart();

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

    private double getYearOverView(List<Transaction> transactions, int year) {
        if (transactions == null) return 0;
        double total = 0;
        List<Transaction> filtered = filterTransactionsByYear(transactions, year);
        for (Transaction t : filtered) {
            total += t.getAmount();
        }
        return total;
    }

    private double getMonthOverview(List<Transaction> transactions, LocalDate date) {
        if (transactions == null) return 0;

        double total = 0;
        List<Transaction> filtered = filterTransactionsByMonth(transactions, date);
        for (Transaction t : filtered) {
            total += t.getAmount();
        }
        return total;
    }

    public void updateYearOverview() {
        double totalIncomeInYear = getYearOverView(incomeList.getValue(), currentYear);
        double totalExpenseInYear = getYearOverView(expenseList.getValue(), currentYear);
        incomeYear.setValue(totalIncomeInYear);
        expenseYear.setValue(totalExpenseInYear);
        balanceYear.setValue(totalIncomeInYear - totalExpenseInYear);
    }

    public void updateMonthOverview() {
        double totalIncomeInMonth = getMonthOverview(incomeList.getValue(), currentMonth);
        double totalExpenseInMonth = getMonthOverview(expenseList.getValue(), currentMonth);
        incomeMonth.setValue(totalIncomeInMonth);
        expenseMonth.setValue(totalExpenseInMonth);
        balanceMonth.setValue(totalIncomeInMonth - totalExpenseInMonth);
    }

    public double getIncomeAndExpenseAtDate(List<Transaction> transactions, LocalDate date) {
        if (transactions == null) return 0;
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getDate().equals(dateStr)) {
                total += Math.abs(t.getAmount());
            }
        }
        return total;
    }

    public void updateDateOfTransaction() {
        List<Transaction> all = new ArrayList<>();
        if (incomeList.getValue() != null) all.addAll(incomeList.getValue());
        if (expenseList.getValue() != null) all.addAll(expenseList.getValue());

        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();

        Map<String, List<Transaction>> map = new TreeMap<>();
        for (Transaction t : all) {
            String[] dates = t.getDate().split("/");
            if (dates.length != 3) continue;
            int m = Integer.parseInt(dates[1]);
            int y = Integer.parseInt(dates[2]);
            if (m == month && y == year) {
                map.computeIfAbsent(t.getDate(), k -> new ArrayList<>()).add(t);
            }
        }

        List<DateOfTransaction> res = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {
            res.add(new DateOfTransaction(entry.getKey(), entry.getValue()));
        }
        dateOfTransactions.setValue(res);
    }

    public void updateMonthIncomeDataPieChart() {
        List<PieEntry> data = getMonthDataPieChart(incomeList.getValue(), categoryMap.getValue(), currentMonth);
        pieEntriesMonthIncome.setValue(data);
    }

    public void updateMonthExpenseDataPieChart() {
        List<PieEntry> data = getMonthDataPieChart(expenseList.getValue(), categoryMap.getValue(), currentMonth);
        pieEntriesMonthExpense.setValue(data);
    }

    public void updateYearIncomeDataPieChart() {
        List<PieEntry> data = getYearDataPieChart(incomeList.getValue(), categoryMap.getValue(), currentYear);
        pieEntriesYearIncome.setValue(data);
    }

    public void updateYearExpenseDataPieChart() {
        List<PieEntry> data = getYearDataPieChart(expenseList.getValue(), categoryMap.getValue(), currentYear);
        pieEntriesYearExpense.setValue(data);
    }

    public List<PieEntry> getMonthDataPieChart(List<Transaction> transactions, Map<String, Category> categoryMap, LocalDate date) {
        if (transactions == null) return null;

        Map<String, Double> categoryTotals = sumAmountByCategory(filterTransactionsByMonth(transactions, date));
        return buildPieEntries(categoryTotals, categoryMap);
    }

    public List<PieEntry> getYearDataPieChart(List<Transaction> transactions, Map<String, Category> categoryMap, int year) {
        if (transactions == null) return null;

        Map<String, Double> categoryTotals = sumAmountByCategory(filterTransactionsByYear(transactions, year));
        return buildPieEntries(categoryTotals, categoryMap);
    }

    public List<PieEntry> getAllTimeDataPieChart(List<Transaction> transactions, Map<String, Category> categoryMap) {
        if (transactions == null) return null;

        Map<String, Double> categoryTotals = sumAmountByCategory(transactions);
        return buildPieEntries(categoryTotals, categoryMap);
    }

    public void updateAllTimeIncomeDataPieChart() {
        List<PieEntry> data = getAllTimeDataPieChart(incomeList.getValue(), categoryMap.getValue());
        pieEntriesAllTimeIncome.setValue(data);
    }

    public void updateAllTimeExpenseDataPieChart() {
        List<PieEntry> data = getAllTimeDataPieChart(expenseList.getValue(), categoryMap.getValue());
        pieEntriesAllTimeExpense.setValue(data);
    }

    private List<PieEntry> buildPieEntries(Map<String, Double> categoryTotals, Map<String, Category> categoryMap) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            Category c = categoryMap.get(entry.getKey());
            if (c != null) {
                double amount = entry.getValue();
                String label = (c.getName() != null) ? c.getName() : "Khác";
                pieEntries.add(new PieEntry((float) amount, label, amount));
            }
        }
        return pieEntries;
    }

    public void updateMonthIncomeCategorySummary() {
        monthCategoryIncomeSummary.setValue(buildMonthCategorySummary(incomeList.getValue(), categoryMap.getValue(), currentMonth));
    }

    public void updateMonthExpenseCategorySummary() {
        monthCategoryExpenseSummary.setValue(buildMonthCategorySummary(expenseList.getValue(), categoryMap.getValue(), currentMonth));
    }

    public void updateYearIncomeCategorySummary() {
        yearCategoryIncomeSummary.setValue(buildYearCategorySummary(incomeList.getValue(), categoryMap.getValue(), currentYear));
    }

    public void updateYearExpenseCategorySummary() {
        yearCategoryExpenseSummary.setValue(buildYearCategorySummary(expenseList.getValue(), categoryMap.getValue(), currentYear));
    }

    public void updateAllTimeIncomeCategorySummary() {
        allTimeCategoryIncomeSummary.setValue(buildAllTimeCategorySummary(incomeList.getValue(), categoryMap.getValue()));
    }

    public void updateAllTimeExpenseCategorySummary() {
        allTimeCategoryExpenseSummary.setValue(buildAllTimeCategorySummary(expenseList.getValue(), categoryMap.getValue()));
    }


    public List<CategorySummary> buildMonthCategorySummary(List<Transaction> transactions, Map<String, Category> categoryMap, LocalDate date) {
        if (transactions == null || categoryMap == null) return new ArrayList<>();

        Map<String, Double> categoryTotals = sumAmountByCategory(filterTransactionsByMonth(transactions, date));
        return buildCategorySummary(categoryTotals, categoryMap);
    }


    public List<CategorySummary> buildYearCategorySummary(List<Transaction> transactions, Map<String, Category> categoryMap, int year) {
        if (transactions == null || categoryMap == null) return new ArrayList<>();

        Map<String, Double> categoryTotals = sumAmountByCategory(filterTransactionsByYear(transactions, year));
        return buildCategorySummary(categoryTotals, categoryMap);
    }

    public List<CategorySummary> buildAllTimeCategorySummary(List<Transaction> transactions, Map<String, Category> categoryMap) {
        if (transactions == null || categoryMap == null) return new ArrayList<>();

        Map<String, Double> categoryTotals = sumAmountByCategory(transactions);
        return buildCategorySummary(categoryTotals, categoryMap);
    }

    public void updateBarChartDataByCategoryForYear(String categoryId) {
        List<Transaction> allTransactions = new ArrayList<>();
        if (incomeList.getValue() != null) allTransactions.addAll(incomeList.getValue());
        if (expenseList.getValue() != null) allTransactions.addAll(expenseList.getValue());

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            LocalDate transDate = parseDate(t);

            if (transDate.getYear() == currentYear && categoryId.equals(t.getCategoryId())) {
                filtered.add(t);
            }
        }

        // Tổng theo tháng
        Map<Integer, Double> monthTotals = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthTotals.put(m, 0.0);
        }
        for (Transaction t : filtered) {
            LocalDate date = parseDate(t);
            int month = date.getMonthValue();
            double prev = monthTotals.getOrDefault(month, 0.0);
            monthTotals.put(month, prev + t.getAmount());
        }

        // Tạo BarEntry cho 12 tháng
        List<BarEntry> entries = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            float value = monthTotals.containsKey(month) ? monthTotals.get(month).floatValue() : 0f;
            entries.add(new BarEntry(month, value));
        }
        categoryTotalByMonth.setValue(monthTotals);
        barEntriesCategoryForYear.setValue(entries);
    }

    public void updateBarChartDataByCategoryForMonth(String categoryId) {
        List<Transaction> allTransactions = new ArrayList<>();
        if (incomeList.getValue() != null) allTransactions.addAll(incomeList.getValue());
        if (expenseList.getValue() != null) allTransactions.addAll(expenseList.getValue());

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : allTransactions) {
            LocalDate transDate = parseDate(t);

            if (transDate.getYear() == currentMonth.getYear() && transDate.getMonthValue() == currentMonth.getMonthValue() && categoryId.equals(t.getCategoryId())) {
                filtered.add(t);
            }
        }

        Map<Integer, Double> dayTotals = new HashMap<>();
        for (Transaction t : filtered) {
            LocalDate date = parseDate(t);
            int day = date.getDayOfMonth();
            double prev = dayTotals.getOrDefault(day, 0.0);
            dayTotals.put(day, prev + t.getAmount());
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            float value = dayTotals.containsKey(day) ? dayTotals.get(day).floatValue() : 0f;
            entries.add(new BarEntry(day, value));
        }

        barEntriesCategoryForMonth.setValue(entries);
    }

    private List<CategorySummary> buildCategorySummary(Map<String, Double> categoryTotals, Map<String, Category> categoryMap) {
        List<CategorySummary> res = new ArrayList<>();
        double totalAmount = 0;
        for (double amount : categoryTotals.values()) {
            totalAmount += amount;
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            Category c = categoryMap.get(entry.getKey());
            if (c != null) {
                double amount = entry.getValue();
                double percent = (totalAmount == 0) ? 0 : amount / totalAmount;
                res.add(new CategorySummary(c, amount, percent));
            }
        }
        return res;
    }

    private LocalDate parseDate(Transaction t) {
        String[] parts = t.getDate().split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }


    private List<Transaction> filterTransactionsByYear(List<Transaction> transactions, int year) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (parseDate(t).getYear() == year) {
                result.add(t);
            }
        }
        return result;
    }

    private List<Transaction> filterTransactionsByMonth(List<Transaction> transactions, LocalDate date) {
        List<Transaction> result = new ArrayList<>();
        int month = date.getMonthValue();
        int year = date.getYear();
        for (Transaction t : transactions) {
            LocalDate transDate = parseDate(t);
            if (transDate.getYear() == year && transDate.getMonthValue() == month) {
                result.add(t);
            }
        }
        return result;
    }

    private double[] calculateDataBalanceReport(int Year, List<Transaction> transactions) {
        double[] res = new double[12];

        if (transactions == null || transactions.isEmpty()) {
            return res;
        }
        for (Transaction t : transactions) {
            LocalDate date = parseDate(t);
            if (date.getYear() == Year) {
                int month = date.getMonthValue();
                res[month - 1] += t.getAmount();
            }
        }
        return res;
    }

    private List<BarEntry> toBarEntries(double[] data) {
        List<BarEntry> entries = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            entries.add(new BarEntry(m, (float) data[m - 1]));
        }
        return entries;
    }

    private List<Entry> toLineEntries(double[] income, double[] expense) {
        List<Entry> entries = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            float balance = (float) (income[m - 1] - expense[m - 1]);
            entries.add(new Entry(m, balance));
        }
        return entries;
    }

    public void updateYearlySeries() {
        double[] incomeByMonth = calculateDataBalanceReport(currentYear, incomeList.getValue());
        double[] expenseByMonth = calculateDataBalanceReport(currentYear, expenseList.getValue());

        incomeYearlyList.setValue(toBarEntries(incomeByMonth));
        expenseYearlyList.setValue(toBarEntries(expenseByMonth));
        balanceYearlyList.setValue(toLineEntries(incomeByMonth, expenseByMonth));
    }

    private Map<String, Double> sumAmountByCategory(List<Transaction> transactions) {
        Map<String, Double> map = new HashMap<>();
        for (Transaction t : transactions) {
            String catId = t.getCategoryId();
            double currentAmount = 0;
            if (map.containsKey(catId)) {
                currentAmount = map.get(catId);
            }
            map.put(catId, currentAmount + t.getAmount());
        }
        return map;
    }

    private double sumTotalAmount(List<Transaction> transactions) {
        if (transactions == null) return 0.0;
        double total = 0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }
        return total;
    }

    private void updateBalanceAllTime() {
        Double income = incomeAllTime.getValue();
        Double expense = expenseAllTime.getValue();
        if (income == null) income = 0.0;
        if (expense == null) expense = 0.0;
        balanceAllTime.setValue(income - expense);
    }
}
