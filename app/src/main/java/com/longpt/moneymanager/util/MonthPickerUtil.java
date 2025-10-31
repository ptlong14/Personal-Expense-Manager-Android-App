package com.longpt.moneymanager.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.longpt.moneymanager.helper.MonthSelectorHelper;

import java.util.Calendar;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MonthPickerUtil {
    public static void showMonthPickerDialog(Context context, TextView tvMonth, Consumer<Calendar> onMonthChosen, Supplier<Calendar> getCurrentMonth) {
        tvMonth.setOnClickListener(view -> {
            Calendar selectedMonth = getCurrentMonth.get();
            Calendar today = Calendar.getInstance();
            resetMonth(today);

            // NumberPicker: tháng
            final NumberPicker monthPicker = new NumberPicker(context);
            monthPicker.setMinValue(0);
            monthPicker.setMaxValue(11);
            monthPicker.setValue(selectedMonth.get(Calendar.MONTH));
            monthPicker.setDisplayedValues(new String[]{"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"});

            // NumberPicker: năm
            final NumberPicker yearPicker = new NumberPicker(context);
            int currentYear = today.get(Calendar.YEAR);
            yearPicker.setMinValue(currentYear - 50);
            yearPicker.setMaxValue(currentYear );
            yearPicker.setValue(selectedMonth.get(Calendar.YEAR));

            // Layout chứa picker
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);
            layout.setPadding(20, 20, 20, 20);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 0, 20, 0);

            layout.addView(monthPicker, lp);
            layout.addView(yearPicker, lp);

            // Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chọn tháng");
            builder.setView(layout);

            builder.setPositiveButton("OK", (dialog, which) -> {
                int y = yearPicker.getValue();
                int m = monthPicker.getValue();

                Calendar chosen = Calendar.getInstance();
                chosen.set(y, m, 1);
                resetMonth(chosen);

                // chặn tương lai
                Calendar thisMonth = Calendar.getInstance();
                thisMonth.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1);
                resetMonth(thisMonth);

                if (chosen.after(thisMonth)) {
                    chosen = (Calendar) thisMonth.clone();
                }

                onMonthChosen.accept(chosen);
            });

            builder.setNegativeButton("Hủy", null);

            builder.setNeutralButton("Tháng nay", (dialog, which) -> {
                Calendar now = Calendar.getInstance();
                resetMonth(now);
                now.set(Calendar.DAY_OF_MONTH, 1);
                onMonthChosen.accept(now);
            });

            builder.show();
        });
    }

    private static void resetMonth(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}

