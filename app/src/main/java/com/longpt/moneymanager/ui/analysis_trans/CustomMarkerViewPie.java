package com.longpt.moneymanager.ui.analysis_trans;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.longpt.moneymanager.R;

import java.util.List;
import java.util.Locale;

public class CustomMarkerViewPie extends MarkerView {

    private final TextView tvCategoryName;
    private final TextView tvAmount;
    private final TextView tvPercent;
    private final List<PieEntry> pieEntries;

    public CustomMarkerViewPie(Context context, int layoutResource, List<PieEntry> pieEntries) {
        super(context, layoutResource);
        this.pieEntries = pieEntries;
        tvCategoryName = findViewById(R.id.tvCategoryNameMaker);
        tvAmount = findViewById(R.id.tvAmountMaker);
        tvPercent = findViewById(R.id.tvPercentMaker);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof PieEntry) {
            PieEntry pieEntry = (PieEntry) e;

            String name = pieEntry.getLabel();
            float value = pieEntry.getValue();

            // Tính tổng
            float total = 0f;
            for (PieEntry entry : pieEntries) {
                total += entry.getValue();
            }

            float percent = (value / total) * 100f;

            tvCategoryName.setText(name);
            tvAmount.setText(String.format(Locale.getDefault(), "%,.0f₫", value));
            tvPercent.setText(String.format(Locale.getDefault(), "%.1f %%", percent));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}
