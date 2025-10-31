package com.longpt.moneymanager.ui.calendar_view;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kizitonwose.calendarview.ui.ViewContainer;
import com.longpt.moneymanager.R;

public class DayViewCalendarContainer extends ViewContainer {

    public TextView tvDay, tvIncome, tvExpense;
    public DayViewCalendarContainer(@NonNull View view) {
        super(view);
        tvDay= view.findViewById(R.id.txtDay);
        tvIncome= view.findViewById(R.id.txtIncome);
        tvExpense= view.findViewById(R.id.txtExpense);
    }
}
