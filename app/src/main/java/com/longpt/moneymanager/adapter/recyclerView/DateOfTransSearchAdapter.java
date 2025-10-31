package com.longpt.moneymanager.adapter.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DateOfTransSearchAdapter extends RecyclerView.Adapter<DateOfTransSearchAdapter.DateTransSearchViewHolder> {
    private List<DateOfTransaction> dateOfTransactions;
    private Map<String, Category> categoryMap;
    private TransInDateSearchAdapter.TransItemSearchListener transItemSearchListener;

    public DateOfTransSearchAdapter(List<DateOfTransaction> dateOfTransactions, Map<String, Category> categoryMap) {
        this.dateOfTransactions = dateOfTransactions;
        this.categoryMap = categoryMap;
    }

    public void setTransItemSearchListener(TransInDateSearchAdapter.TransItemSearchListener transItemSearchListener) {
        this.transItemSearchListener = transItemSearchListener;
    }

    @NonNull
    @Override
    public DateTransSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_of_trans_search, parent, false);
        return new DateTransSearchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTransSearchViewHolder holder, int position) {
        DateOfTransaction dateOfTransaction = dateOfTransactions.get(position);
        holder.bind(dateOfTransaction);
    }

    @Override
    public int getItemCount() {
        return dateOfTransactions.size();
    }

    public class DateTransSearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvBalanceInDate;
        RecyclerView rvTransactionInDate;
        TransInDateSearchAdapter adapter;

        public DateTransSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate_Search);
            tvBalanceInDate = itemView.findViewById(R.id.tvBalanceInDate_Search);
            rvTransactionInDate = itemView.findViewById(R.id.rvTransInDate_Search);

            rvTransactionInDate.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            adapter = new TransInDateSearchAdapter(new ArrayList<>(), categoryMap);
            rvTransactionInDate.setAdapter(adapter);

            adapter.setTransItemSearchListener(new TransInDateSearchAdapter.TransItemSearchListener() {
                @Override
                public void onTransactionClick(Transaction t) {
                    if (transItemSearchListener != null) {
                        transItemSearchListener.onTransactionClick(t);
                    }
                }
            });
        }

        void bind(DateOfTransaction dateOfTransaction) {
            tvDate.setText(dateOfTransaction.getDate());
            double totalAmount = 0;
            for (Transaction transaction : dateOfTransaction.getTransactions()) {
                if ("income".equals(transaction.getType())) {
                    totalAmount += transaction.getAmount();
                } else if ("expense".equals(transaction.getType())) {
                    totalAmount -= transaction.getAmount();
                }
            }

            NumberFormat formatter = NumberFormat.getInstance();
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);

            String formattedAmount = formatter.format(Math.abs(totalAmount));
            String displayAmount = (totalAmount >= 0 ? "+ " : "- ") + formattedAmount + " đ";

            tvBalanceInDate.setText(displayAmount);

            adapter.setTransactionList(dateOfTransaction.getTransactions());
        }
    }
}
