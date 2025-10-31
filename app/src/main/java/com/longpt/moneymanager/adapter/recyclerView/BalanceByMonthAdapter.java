package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BalanceByMonthAdapter extends RecyclerView.Adapter<BalanceByMonthAdapter.BalanceByMonthViewHolder> {
    private final Context context;
    private Map<Integer, Double> mapMonthBalance = new HashMap<>();

    public BalanceByMonthAdapter(Context context, Map<Integer, Double> mapMonthBalance) {
        this.context = context;
        this.mapMonthBalance = mapMonthBalance;
    }

    @NonNull
    @Override
    public BalanceByMonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_balance_by_month, parent, false);
        return new BalanceByMonthViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BalanceByMonthViewHolder holder, int position) {
        Map.Entry<Integer, Double> entry = new ArrayList<>(mapMonthBalance.entrySet()).get(position);
        int month = entry.getKey();
        double total = entry.getValue();
        holder.bind(month, total);
    }

    @Override
    public int getItemCount() {
        return mapMonthBalance.size();
    }

    public static class BalanceByMonthViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMonth;
        private final TextView tvBalance;

        public BalanceByMonthViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth_BalanceReport);
            tvBalance = itemView.findViewById(R.id.tvTotal_BalanceReport);
        }

        void bind(int month, double value) {
            tvMonth.setText("Tháng " + month);
            if (value > 0) {
                tvBalance.setText("+" + value + "đ");
                tvBalance.setTextColor(Color.parseColor("#4CAF50"));
            } else if (value < 0) {
                tvBalance.setText(value + "đ");
                tvBalance.setTextColor(Color.RED);
            } else {
                tvBalance.setText("0đ");
                tvBalance.setTextColor(Color.BLUE);
            }
        }
    }
}
