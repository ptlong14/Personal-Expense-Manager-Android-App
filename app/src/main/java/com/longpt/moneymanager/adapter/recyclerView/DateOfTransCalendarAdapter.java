package com.longpt.moneymanager.adapter.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.DateOfTransaction;
import com.longpt.moneymanager.data.model.Transaction;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DateOfTransCalendarAdapter extends RecyclerView.Adapter<DateOfTransCalendarAdapter.DateTransCalendarViewHolder> {

    private final List<DateOfTransaction> dateOfTransactions;
    private final Map<String, Category> categoryMap;
    private final ViewBinderHelper globalBinderHelper = new ViewBinderHelper();
    private TransInDateCalendarAdapter.TransItemCalendarListener transItemCalendarListener;

    public DateOfTransCalendarAdapter(List<DateOfTransaction> dateOfTransactions, Map<String, Category> categoryMap) {
        this.dateOfTransactions = dateOfTransactions;
        this.categoryMap = categoryMap;
    }

    public void setTransactionItemListener(TransInDateCalendarAdapter.TransItemCalendarListener listener) {
        this.transItemCalendarListener = listener;
    }
    public void closeOpenedSwipe() {
        for (int i = 0; i < dateOfTransactions.size(); i++) {
            List<Transaction> tList = dateOfTransactions.get(i).getTransactions();
            for (Transaction t : tList) {
                globalBinderHelper.closeLayout(t.getId());
            }
        }
    }

    @NonNull
    @Override
    public DateTransCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_of_trans_calendar, parent, false);
        return new DateTransCalendarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTransCalendarViewHolder holder, int position) {
        DateOfTransaction dateOfTransaction = dateOfTransactions.get(position);
        holder.bind(dateOfTransaction);
    }

    @Override
    public int getItemCount() {
        return dateOfTransactions.size();
    }

    public ViewBinderHelper getGlobalBinderHelper() {
        return globalBinderHelper;
    }

    public class DateTransCalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvBalanceInDate;
        RecyclerView rvTransactionInDate;
        TransInDateCalendarAdapter adapter;

        public DateTransCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate_Calendar);
            tvBalanceInDate = itemView.findViewById(R.id.tvBalanceInDate_Calendar);
            rvTransactionInDate = itemView.findViewById(R.id.rvTransInDate_Calendar);

            rvTransactionInDate.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            adapter = new TransInDateCalendarAdapter(new ArrayList<>(), categoryMap, globalBinderHelper);
            rvTransactionInDate.setAdapter(adapter);


            adapter.setTransItemCalendarListener(new TransInDateCalendarAdapter.TransItemCalendarListener() {
                @Override
                public void onTransactionClick(Transaction t) {
                    if (transItemCalendarListener != null) {
                        transItemCalendarListener.onTransactionClick(t);
                    }
                }

                @Override
                public void onDeleteTransactionClick(Transaction t) {
                    if (transItemCalendarListener != null) {
                        transItemCalendarListener.onDeleteTransactionClick(t);
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

            adapter.setTransactions(dateOfTransaction.getTransactions());
        }
    }
}
