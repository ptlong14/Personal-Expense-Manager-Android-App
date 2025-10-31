package com.longpt.moneymanager;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.longpt.moneymanager.enums.ScopeAnalysis;
import com.longpt.moneymanager.enums.TypeAnalysis;
import com.longpt.moneymanager.ui.analysis_trans.PieTransactionFragment;
import com.longpt.moneymanager.ui.analysis_trans.ViewAnalysisTabFragment;

public class CategoryYearReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_category_year_report);
        findViewById(R.id.tvBackFromCategoryYear).setOnClickListener(v -> {
            onBackPressed(); // hoặc finish();
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    // mở ViewAnalysisTabFragment với scope = YEAR_ANALYSIS
                    .replace(R.id.fmCategoryYearContainer,
                            ViewAnalysisTabFragment.newInstance(ScopeAnalysis.YEAR_ANALYSIS))
                    .commit();
        }
    }
}