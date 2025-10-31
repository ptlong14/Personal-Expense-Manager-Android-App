package com.longpt.moneymanager.helper;

import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateSelectorHelper {
    private Calendar selectedDate;
    private  TextView tvDate;
    private View btnNextDate;


    public DateSelectorHelper(TextView tvDate, View btnNextDate) {
        this.selectedDate = Calendar.getInstance();
        this.tvDate = tvDate;
        this.btnNextDate = btnNextDate;
        updateDateText();
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
        updateDateText();
    }

    public void prevDate() {
        selectedDate.add(Calendar.DAY_OF_MONTH, -1);
        updateDateText();
    }
    public void nextDate() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar nextDate = (Calendar) selectedDate.clone();
        nextDate.add(Calendar.DAY_OF_MONTH, 1);
        nextDate.set(Calendar.HOUR_OF_DAY, 0);
        nextDate.set(Calendar.MINUTE, 0);
        nextDate.set(Calendar.SECOND, 0);
        nextDate.set(Calendar.MILLISECOND, 0);

        if (!nextDate.after(today)) {
            selectedDate = nextDate;
            updateDateText();
        }
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate.getTime()));
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar currentSelected = (Calendar) selectedDate.clone();
        currentSelected.set(Calendar.HOUR_OF_DAY, 0);
        currentSelected.set(Calendar.MINUTE, 0);
        currentSelected.set(Calendar.SECOND, 0);
        currentSelected.set(Calendar.MILLISECOND, 0);

        boolean isToday = currentSelected.equals(today);
        if (isToday) {
            btnNextDate.setEnabled(false);
            btnNextDate.setAlpha(0.3f);
            btnNextDate.setClickable(false);
            btnNextDate.setFocusable(false);
        } else {
            btnNextDate.setEnabled(true);
            btnNextDate.setAlpha(1f);
            btnNextDate.setClickable(true);
            btnNextDate.setFocusable(true);
        }
    }
}
