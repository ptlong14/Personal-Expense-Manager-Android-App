package com.longpt.moneymanager.ui.others_features;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.longpt.moneymanager.CategoryYearReportActivity;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.ui.LoginActivity;
import com.longpt.moneymanager.ui.search_transaction.SearchTransactionActivity;

public class Other_Fragment extends Fragment {
    private LinearLayout llFindTransaction, llLogOut, llAllTimeReport, llCategoryYear, llCategoryAllTime, llBalanceReport;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_others, container, false);

        llLogOut = v.findViewById(R.id.llLogOut);
        llFindTransaction = v.findViewById(R.id.llFindTransaction);
        llAllTimeReport = v.findViewById(R.id.llAllTimeReport);
        llCategoryYear = v.findViewById(R.id.llCategoryYear);
        llCategoryAllTime = v.findViewById(R.id.llCategoryAlTime);
        llBalanceReport = v.findViewById(R.id.llBalanceReport);

        llBalanceReport.setOnClickListener(view->{
            startActivity(new Intent(requireActivity(), BalanceReportActivity.class));
        });

        llCategoryAllTime.setOnClickListener(v1 -> {
            startActivity(new Intent(requireActivity(), CategoryAllTimeReportActivity.class));
        });
        llCategoryYear.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), CategoryYearReportActivity.class));
        });

        llFindTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), SearchTransactionActivity.class));
            }
        });
        llAllTimeReport.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), AllTimeReportActivity.class));
        });

        llLogOut.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
            View dialogView = layoutInflater.inflate(R.layout.custom_logout_confirm_dialog, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

            btnCancel.setOnClickListener(v1 -> dialog.dismiss());

            btnConfirm.setOnClickListener(v1 -> {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    auth.signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
                dialog.dismiss();
            });

            dialog.show();
        });
        return v;
    }
}
