package com.longpt.moneymanager.ui.others_features;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.enums.TypeAnalysis;

public class CategoryAllTimeReportActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_category_alltime_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tvBack= findViewById(R.id.tvBackFromCategoryAllTime);
        tabLayout= findViewById(R.id.tabLayoutCategoryAllTime);
        tabLayout.addTab(tabLayout.newTab().setText("Chi tiêu"));
        tabLayout.addTab(tabLayout.newTab().setText("Thu nhập"));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.tabContentCategoryAllTime, PieCategoryAllTimeFragment.newInstance(TypeAnalysis.EXPENSE_ANALYSIS))
                .commit();


        tvBack.setOnClickListener(view->{
            finish();
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TypeAnalysis type = tab.getPosition() == 0 ? TypeAnalysis.EXPENSE_ANALYSIS : TypeAnalysis.INCOME_ANALYSIS;
                Fragment selectedFragment = PieCategoryAllTimeFragment.newInstance(type);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.tabContentCategoryAllTime, selectedFragment)
                        .commit();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }
}