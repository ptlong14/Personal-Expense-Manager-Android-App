package com.longpt.moneymanager.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.longpt.moneymanager.helper.YearSelectorHelper;

import java.util.Calendar;

public class YearPickerUtil {
    public static void showYearPickerDialog(Context context, TextView tvYear, YearSelectorHelper yearSelectorHelper){
        tvYear.setOnClickListener(view->{
            int selectedYear= yearSelectorHelper.getSelectedYear();
            int currentYear= Calendar.getInstance().get(Calendar.YEAR);
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setTitle("Chọn năm");
            final NumberPicker yearPicker= new NumberPicker(context);
            yearPicker.setMaxValue(currentYear);
            yearPicker.setMinValue(currentYear-50);
            yearPicker.setValue(selectedYear);

            builder.setView(yearPicker);
            builder.setPositiveButton("OK", (dialog, which) -> {
                yearSelectorHelper.setSelectedYear(yearPicker.getValue());
            });
            builder.setNegativeButton("Hủy", null);
            builder.setNeutralButton("Năm nay", (dialog, which) -> {
               yearSelectorHelper.setSelectedYear(currentYear);
            });
            builder.show();
        });
    }
}
