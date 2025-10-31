package com.longpt.moneymanager.helper;

import android.view.View;
import android.widget.TextView;

import com.kizitonwose.calendarview.CalendarView;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Locale;

public class MonthSelectorHelper {
    private Calendar selectedMonth;
    private TextView tvMonth;
    private View btnNextMonth;
    private OnMonthChangeListener listener;

    public MonthSelectorHelper(TextView tvMonth, View btnNextMonth) {
        this.tvMonth = tvMonth;
        this.btnNextMonth = btnNextMonth;
        this.selectedMonth = Calendar.getInstance();
        this.selectedMonth.set(Calendar.DAY_OF_MONTH, 1);
        updateMonthText();
    }
    public Calendar getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(Calendar selectedMonth) {
        Calendar clone = (Calendar) selectedMonth.clone();
        clone.set(Calendar.DAY_OF_MONTH, 1);
        this.selectedMonth = clone;
        updateMonthText();
        if(listener!=null){
            listener.onMonthChanged(selectedMonth);
        }
    }

    public void prevMonth() {
        selectedMonth.add(Calendar.MONTH, -1);
        selectedMonth.set(Calendar.DAY_OF_MONTH, 1);
        updateMonthText();
        if(listener!=null){
            listener.onMonthChanged(selectedMonth);
        }
    }

    public void nextMonth() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar nextMonth = (Calendar) selectedMonth.clone();
        nextMonth.add(Calendar.MONTH, 1);
        nextMonth.set(Calendar.DAY_OF_MONTH, 1);
        nextMonth.set(Calendar.HOUR_OF_DAY, 0);
        nextMonth.set(Calendar.MINUTE, 0);
        nextMonth.set(Calendar.SECOND, 0);
        nextMonth.set(Calendar.MILLISECOND, 0);

        if (!nextMonth.after(today)) {
            selectedMonth = nextMonth;
            updateMonthText();
        }
        if(listener!=null){
            listener.onMonthChanged(selectedMonth);
        }
    }

    public void setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.listener = listener;
    }

    private void updateMonthText() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM/yyyy", Locale.getDefault());
        String monthYear = monthFormat.format(selectedMonth.getTime());

        Calendar firstDay = (Calendar) selectedMonth.clone();
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        String firstDayStr = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(firstDay.getTime());

        Calendar lastDay = (Calendar) selectedMonth.clone();
        int lastDate = lastDay.getActualMaximum(Calendar.DAY_OF_MONTH);
        lastDay.set(Calendar.DAY_OF_MONTH, lastDate);
        String lastDayStr = new SimpleDateFormat("dd/MM", Locale.getDefault()).format(lastDay.getTime());

        tvMonth.setText(monthYear + " (" + firstDayStr + " - " + lastDayStr + ")");

        Calendar today = Calendar.getInstance();
        today.set(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);


        boolean isThisMonth = selectedMonth.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && selectedMonth.get(Calendar.MONTH) == today.get(Calendar.MONTH);

        btnNextMonth.setEnabled(!isThisMonth);
        btnNextMonth.setAlpha(isThisMonth ? 0.3f : 1f);
        btnNextMonth.setClickable(!isThisMonth);
        btnNextMonth.setFocusable(!isThisMonth);
    }
    public interface OnMonthChangeListener{
        void onMonthChanged(Calendar month);
    }
}
