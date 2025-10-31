package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;

import java.util.ArrayList;
import java.util.Map;

public class CategoryTotalByMonthAdapter extends RecyclerView.Adapter<CategoryTotalByMonthAdapter.CategoryTotalMonthlyViewHolder> {

    private Map<Integer, Double> mapCategoryTotalMonthly;
    private Context context;
    private CategoryTotalMonthlyItemListener listener;

    public CategoryTotalByMonthAdapter(Map<Integer, Double> mapCategoryTotalMonthly, Context context) {
        this.mapCategoryTotalMonthly = mapCategoryTotalMonthly;
        this.context = context;
    }

    public void setListener(CategoryTotalMonthlyItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryTotalMonthlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_category_total_by_month, parent, false);
        return new CategoryTotalMonthlyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryTotalMonthlyViewHolder holder, int position) {
        Map.Entry<Integer, Double> entry= new ArrayList<>(mapCategoryTotalMonthly.entrySet()).get(position);
        int month= entry.getKey();
        double total= entry.getValue();
        holder.bind(month, total);
    }

    @Override
    public int getItemCount() {
        return mapCategoryTotalMonthly.size();
    }

    public interface CategoryTotalMonthlyItemListener {
        void onItemMonthlyClick(View v, int position);
    }

    public class CategoryTotalMonthlyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvMonth;
        private final TextView tvTotal;
        private final LinearLayout layoutCategoryMonthly;
        private final ImageView ivViewDetail;

        public CategoryTotalMonthlyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tvMonth_YearBarChart);
            tvTotal = itemView.findViewById(R.id.tvTotal_YearBarChart);
            layoutCategoryMonthly = itemView.findViewById(R.id.layoutCategoryTotalMonthly);
            ivViewDetail = itemView.findViewById(R.id.ivViewMonthlyCategory);
            layoutCategoryMonthly.setOnClickListener(this);
        }

        void bind(int month, double value) {
            tvMonth.setText("Tháng " + month);
            tvTotal.setText(value + "đ");
            if (value != 0) {
                ivViewDetail.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onItemMonthlyClick(v, getAdapterPosition());
            }
        }
    }
}
