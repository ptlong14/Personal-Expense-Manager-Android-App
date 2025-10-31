package com.longpt.moneymanager.adapter.recyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.Transaction;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransInDateSearchAdapter extends RecyclerView.Adapter<TransInDateSearchAdapter.TransInDateSearchViewHolder> {

    private final Map<String, Category> categoryMap;
    private List<Transaction> transactionList;
    private TransItemSearchListener transItemSearchListener;

    public TransInDateSearchAdapter(List<Transaction> transactionList, Map<String, Category> categoryMap) {
        this.transactionList = new ArrayList<>();
        if (transactionList != null) this.transactionList.addAll(transactionList);
        this.categoryMap = categoryMap;
    }

    public void setTransItemSearchListener(TransItemSearchListener transItemSearchListener) {
        this.transItemSearchListener = transItemSearchListener;
    }

    public void setTransactionList(List<Transaction> newTransactionList) {
        transactionList.clear();
        if(newTransactionList!=null){
            transactionList.addAll(newTransactionList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransInDateSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trans_in_date_seacrh, parent, false);

        return new TransInDateSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransInDateSearchViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        Category category = categoryMap.get(transaction.getCategoryId());
        if (category != null) {
            holder.tvNameCate.setText(category.getName());
            IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), GoogleMaterial.Icon.valueOf(category.getIcon()));
            iconDrawable.setColorFilter(Color.parseColor(category.getColor()), PorterDuff.Mode.SRC_IN);
            iconDrawable.setBounds(0, 0, 48, 48);
            holder.ivCateIcon.setImageDrawable(iconDrawable);
        }

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);

        String formattedAmount = formatter.format(Math.abs(transaction.getAmount()));
        String displayAmount = (transaction.getType().equals("income") ? "+ " : "- ") + formattedAmount + " đ";
        holder.tvAmount.setText(displayAmount);
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public interface TransItemSearchListener {
        void onTransactionClick(Transaction t);
    }

    public class TransInDateSearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNameCate, tvAmount;
        ImageView ivCateIcon;
        LinearLayout layoutContent;

        public TransInDateSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameCate = itemView.findViewById(R.id.tvCateTransName_Search);
            tvAmount = itemView.findViewById(R.id.tvTransAmount_Search);
            ivCateIcon = itemView.findViewById(R.id.ivCateTransIcon_Search);
            layoutContent = itemView.findViewById(R.id.layoutContentTransaction_Search);
            layoutContent.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Transaction t = transactionList.get(pos);
            if (pos != RecyclerView.NO_POSITION && transItemSearchListener != null) {
                transItemSearchListener.onTransactionClick(t);
            }
        }
    }
}
