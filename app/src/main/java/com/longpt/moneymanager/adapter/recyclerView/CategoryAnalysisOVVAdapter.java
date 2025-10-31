package com.longpt.moneymanager.adapter.recyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.CategorySummary;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class CategoryAnalysisOVVAdapter extends RecyclerView.Adapter<CategoryAnalysisOVVAdapter.CategorySummaryViewHolder> {
    private final List<CategorySummary> categorySummaryList;
    private final Context context;
    private CategorySummaryItemListener listener;
    private boolean showNextIcon = false;

    public CategoryAnalysisOVVAdapter(List<CategorySummary> categorySummaryList, Context context) {
        this.categorySummaryList = categorySummaryList;
        this.context = context;
    }
    public void setShowNextIcon(boolean isShow) {
        this.showNextIcon = isShow;
        notifyDataSetChanged();
    }

    public void setListener(CategorySummaryItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategorySummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category_analysis_ovv, parent, false);
        return new CategorySummaryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategorySummaryViewHolder holder, int position) {
        CategorySummary c = categorySummaryList.get(position);
        holder.bind(c);
    }

    @Override
    public int getItemCount() {
        return categorySummaryList.size();
    }


    public interface CategorySummaryItemListener {
        void onCategorySummaryClick(View v, int position);
    }

    public class CategorySummaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvCateName;
        private final TextView tvCateAmount;
        private final TextView tvCatePercent;
        private final ImageView ivCateIcon, ivNext;

        public CategorySummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCateName = itemView.findViewById(R.id.tvCateName_OVV);
            tvCateAmount = itemView.findViewById(R.id.tvTotalCateAmount_OVV);
            tvCatePercent = itemView.findViewById(R.id.tvTotalCateAmountPercent_OVV);
            ivCateIcon = itemView.findViewById(R.id.ivCateIcon_OVV);
            ivNext= itemView.findViewById(R.id.ivNext);
        }

        void bind(CategorySummary categorySummary) {

            tvCateName.setText(categorySummary.getCategory().getName());

            try {
                IconicsDrawable iconDrawable = new IconicsDrawable(context, GoogleMaterial.Icon.valueOf(categorySummary.getCategory().getIcon()));
                iconDrawable.setColorFilter(Color.parseColor(categorySummary.getCategory().getColor()), PorterDuff.Mode.SRC_IN);
                iconDrawable.setBounds(0, 0, 48, 48);
                ivCateIcon.setImageDrawable(iconDrawable);
            } catch (Exception e) {
                ivCateIcon.setImageDrawable(null);
            }

            double percent = categorySummary.getPercent() * 100;
            String formatted = new DecimalFormat("##0.00").format(percent) + " %";
            tvCatePercent.setText(formatted);

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
            String formattedAmount = formatter.format(Math.abs(categorySummary.getTotalAmount()));
            String displayAmount = formattedAmount + " đ";
            tvCateAmount.setText(displayAmount);
            ivNext.setVisibility(showNextIcon ? View.VISIBLE : View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onCategorySummaryClick(v, getAdapterPosition());
            }
        }
    }
}
