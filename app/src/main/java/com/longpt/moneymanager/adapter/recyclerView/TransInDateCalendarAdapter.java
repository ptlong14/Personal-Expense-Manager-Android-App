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

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.Transaction;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransInDateCalendarAdapter extends RecyclerView.Adapter<TransInDateCalendarAdapter.TransInDateCalendarViewHolder> {

    private final List<Transaction> transactionList;
    private final Map<String, Category> categoryMap;
    private final ViewBinderHelper viewBinderHelper;
    private TransItemCalendarListener transItemCalendarListener;

    public TransInDateCalendarAdapter(List<Transaction> transactionList, Map<String, Category> categoryMap, ViewBinderHelper sharedBinderHelper) {
        this.transactionList = new ArrayList<>();
        if (transactionList != null) this.transactionList.addAll(transactionList);
        this.categoryMap = categoryMap;
        this.viewBinderHelper = sharedBinderHelper;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    public void setTransItemCalendarListener(TransItemCalendarListener transItemCalendarListener) {
        this.transItemCalendarListener = transItemCalendarListener;
    }

    public void setTransactions(List<Transaction> newTransactions) {
        transactionList.clear();
        if (newTransactions != null) {
            transactionList.addAll(newTransactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransInDateCalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trans_in_date_calendar, parent, false);
        return new TransInDateCalendarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransInDateCalendarViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        viewBinderHelper.bind(holder.swipeRevealLayout, transaction.getId());

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

    public interface TransItemCalendarListener {
        void onTransactionClick(Transaction t);

        void onDeleteTransactionClick(Transaction t);
    }

    public class TransInDateCalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNameCate, tvAmount;
        ImageView ivCateIcon;
        LinearLayout layoutContent, layoutDelete;
        SwipeRevealLayout swipeRevealLayout;

        public TransInDateCalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeRevealLayout = itemView.findViewById(R.id.swipeLayoutShowTrans_Calendar);
            tvNameCate = itemView.findViewById(R.id.tvCateTransName_Calendar);
            tvAmount = itemView.findViewById(R.id.tvTransAmount_Calendar);
            ivCateIcon = itemView.findViewById(R.id.ivCateTransIcon_Calendar);
            layoutContent = itemView.findViewById(R.id.layoutContentTransaction_Calendar);
            layoutDelete = itemView.findViewById(R.id.layoutDeleteTrans_Calendar);
            layoutContent.setOnClickListener(this);
            layoutDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && transItemCalendarListener != null) {
                int idView = v.getId();
                Transaction t = transactionList.get(position);
                if (idView == R.id.layoutContentTransaction_Calendar) {
                    transItemCalendarListener.onTransactionClick(t);
                } else if (idView == R.id.layoutDeleteTrans_Calendar) {
                    transItemCalendarListener.onDeleteTransactionClick(t);
                }
            }
        }
    }
}
