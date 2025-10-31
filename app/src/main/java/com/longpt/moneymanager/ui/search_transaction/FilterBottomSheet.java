package com.longpt.moneymanager.ui.search_transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.longpt.moneymanager.R;

import java.time.LocalDate;

public class FilterBottomSheet extends BottomSheetDialogFragment {
    private TextView tvAllTime, tvYear, tvCancel;
    private OnFilterSelectedListener listener;
    public  interface OnFilterSelectedListener{
        void onFilterSelected(boolean isAllTime);
    }
    public void setOnFilterSelectedListener(OnFilterSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.bottom_sheet_filter, container, false);
        tvAllTime= v.findViewById(R.id.tvAllTime);
        tvYear= v.findViewById(R.id.tvYear);
        tvCancel= v.findViewById(R.id.tvCancel);

        tvAllTime.setOnClickListener(view -> {
            if (listener != null) {
                listener.onFilterSelected(true);
            }
            dismiss();
        });

        tvYear.setOnClickListener(view -> {
            if (listener != null) {
                listener.onFilterSelected(false);
            }
            dismiss();
        });


        tvCancel.setOnClickListener(view->
            dismiss());
        return v;
    }
}
