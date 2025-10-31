package com.longpt.moneymanager.ui.others_features;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.longpt.moneymanager.R;

public class CustomMarkerViewCombine extends MarkerView {
    private final TextView tvContent;
    private final String[] months;

    // Constructor thực tế sẽ dùng khi gắn vào chart
    public CustomMarkerViewCombine(Context context, int layoutResource, String[] months) {
        super(context, layoutResource);
        this.months = months;
        tvContent = findViewById(R.id.tvContent);
    }

    // Các constructor mặc định để Android Studio không báo lỗi
    public CustomMarkerViewCombine(Context context) {
        super(context, R.layout.custom_maker_view_combine);
        this.months = new String[0];
        tvContent = findViewById(R.id.tvContent);
    }

    public CustomMarkerViewCombine(Context context, AttributeSet attrs) {
        super(context, R.layout.custom_maker_view_combine);
        this.months = new String[0];
        tvContent = findViewById(R.id.tvContent);
    }

    public CustomMarkerViewCombine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, R.layout.custom_maker_view_combine);
        this.months = new String[0];
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int xIndex = (int) highlight.getX();
        String monthLabel = (xIndex >= 0 && xIndex < months.length) ? months[xIndex] : "";

        if (e instanceof BarEntry) {
            float value = e.getY();
            String label = getDataSetLabel(highlight);
            tvContent.setText(monthLabel + " - " + label + ": " + value);
        } else {
            tvContent.setText(monthLabel + " - Balance: " + e.getY());
        }

        super.refreshContent(e, highlight);
    }

    private String getDataSetLabel(Highlight highlight) {
        BarData barData = ((CombinedChart) getChartView()).getBarData();
        if (barData != null) {
            IBarDataSet set = barData.getDataSetByIndex(highlight.getDataSetIndex());
            if (set != null) return set.getLabel();
        }
        return "";
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight());
    }
}