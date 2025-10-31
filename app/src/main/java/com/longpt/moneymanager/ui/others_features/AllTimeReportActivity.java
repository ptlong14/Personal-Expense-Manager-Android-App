package com.longpt.moneymanager.ui.others_features;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;

import java.util.Locale;

public class AllTimeReportActivity extends AppCompatActivity {

    private TextView tvIncome, tvExpense, tvBalance;
    private TextView tvBack;
    private TransactionAnalyticsViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_all_time_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvIncome= findViewById(R.id.tvIncomeAllTime);
        tvExpense= findViewById(R.id.tvExpenseAllTime);
        tvBalance= findViewById(R.id.tvBalanceAllTime);
        tvBack= findViewById(R.id.tvBackFromAllTimeReport);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TransactionCalendarViewModelFactory factory = new TransactionCalendarViewModelFactory(uid);
        viewModel = new ViewModelProvider(this, factory).get(TransactionAnalyticsViewModel.class);

        viewModel.getIncomeAllTime().observe(this, data->{
            tvIncome.setText(String.format(Locale.getDefault(), "%,.0f", data));
        });
        viewModel.getExpenseAllTime().observe(this, data->{
            tvExpense.setText(String.format(Locale.getDefault(), "%,.0f", data));
        });
        viewModel.getBalanceAllTime().observe(this, data->{
            tvBalance.setText(String.format(Locale.getDefault(), "%,.0f", data));
        });

        tvBack.setOnClickListener(v->{
            finish();
        });

    }
}