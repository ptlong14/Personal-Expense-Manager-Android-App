package com.longpt.moneymanager.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.longpt.moneymanager.helper.DateSelectorHelper;

import java.util.Calendar;

public class DatePickerUtil {
    public static void showDatePickerDialog(Context context, TextView tvDate, TextView btnPrev, TextView btnNext, DateSelectorHelper dateHelper) {
        tvDate.setOnClickListener(view -> {
            Calendar selectedDate = dateHelper.getSelectedDate();
            Calendar today = Calendar.getInstance();

            // NumberPicker cho ngày
            final NumberPicker dayPicker = new NumberPicker(context);
            dayPicker.setMinValue(1);
            dayPicker.setMaxValue(31);
            dayPicker.setValue(selectedDate.get(Calendar.DAY_OF_MONTH));

            // NumberPicker cho tháng
            final NumberPicker monthPicker = new NumberPicker(context);
            monthPicker.setMinValue(0);
            monthPicker.setMaxValue(11);
            monthPicker.setValue(selectedDate.get(Calendar.MONTH));
            monthPicker.setDisplayedValues(new String[]{
                    "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
                    "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
            });

            // NumberPicker cho năm
            final NumberPicker yearPicker = new NumberPicker(context);
            int currentYear = today.get(Calendar.YEAR);
            yearPicker.setMinValue(currentYear - 50);
            yearPicker.setMaxValue(currentYear);
            yearPicker.setValue(selectedDate.get(Calendar.YEAR));

            // Hàm tính số ngày của 1 tháng (có check năm nhuận)
            Runnable updateDays = () -> {
                int year = yearPicker.getValue();
                int month = monthPicker.getValue();

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);

                int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                dayPicker.setMaxValue(maxDay);
                if (dayPicker.getValue() > maxDay) {
                    dayPicker.setValue(maxDay);
                }
            };

            // Gán listener cho tháng và năm để auto update số ngày
            monthPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateDays.run());
            yearPicker.setOnValueChangedListener((picker, oldVal, newVal) -> updateDays.run());

            // Cập nhật ngày ban đầu
            updateDays.run();

            // Layout chứa 3 picker
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setGravity(Gravity.CENTER);
            layout.setPadding(20, 20, 20, 20);

            // margin cho đẹp
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 0, 20, 0);

            layout.addView(dayPicker, params);
            layout.addView(monthPicker, params);
            layout.addView(yearPicker, params);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Chọn ngày");
            builder.setView(layout);

            builder.setPositiveButton("OK", (dialog, which) -> {
                Calendar chosenDate = Calendar.getInstance();
                chosenDate.set(yearPicker.getValue(), monthPicker.getValue(), dayPicker.getValue());
                resetTime(today);

                if (chosenDate.after(today)) {
                    dateHelper.setSelectedDate(today);
                } else {
                    dateHelper.setSelectedDate(chosenDate);
                }
            });

            builder.setNegativeButton("Hủy", null);

            builder.setNeutralButton("Hôm nay", (dialog, which) -> {
                dateHelper.setSelectedDate(Calendar.getInstance());
            });

            builder.show();
        });

        btnPrev.setOnClickListener(view -> {
            dateHelper.prevDate();
        });

        btnNext.setOnClickListener(view -> {
            dateHelper.nextDate();
        });
    }

    private static void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}
