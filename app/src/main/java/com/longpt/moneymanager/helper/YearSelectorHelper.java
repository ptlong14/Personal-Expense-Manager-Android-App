package com.longpt.moneymanager.helper;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class YearSelectorHelper {
    private final TextView tvYear;
    private final View btnNextYear;
    private int selectedYear;
    private OnYearChangeListener listener;

    public YearSelectorHelper(TextView tvYear, View btnNextYear) {
        this.tvYear = tvYear;
        this.btnNextYear = btnNextYear;
        this.selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        updateYearText();
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int year) {
        this.selectedYear = year;
        updateYearText();
        if(listener!=null){
            listener.onYearChanged(selectedYear);
        }
    }

    public void prevYear() {
        this.selectedYear--;
        updateYearText();
        if(listener!=null){
            listener.onYearChanged(selectedYear);
        }
    }

    public void nextYear() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (selectedYear < currentYear) {
            selectedYear++;
            updateYearText();
        }
        if(listener!=null){
            listener.onYearChanged(selectedYear);
        }
    }
    public void setOnYearChangeListener(OnYearChangeListener listener) {
        this.listener = listener;
    }
    private void updateYearText() {
        String yearText = selectedYear + " (01/01 - 31/12)";
        tvYear.setText(yearText);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        boolean isThisYear = selectedYear == currentYear;

        btnNextYear.setEnabled(!isThisYear);
        btnNextYear.setAlpha(isThisYear ? 0.3f : 1f);
        btnNextYear.setClickable(!isThisYear);
        btnNextYear.setFocusable(!isThisYear);
    }
    public interface OnYearChangeListener {
        void onYearChanged(int newYear);
    }
}