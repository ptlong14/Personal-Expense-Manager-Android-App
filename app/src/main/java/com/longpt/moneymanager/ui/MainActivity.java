package com.longpt.moneymanager.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.bottomNavigation.BottomNavStateAdapter;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;
    private BottomNavStateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.btNav);
        viewPager2 = findViewById(R.id.viewPagerMain);
        viewPager2.setUserInputEnabled(false);
        adapter = new BottomNavStateAdapter(this);
        viewPager2.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int index = (item.getItemId());
            if (index == R.id.btNavCreate) viewPager2.setCurrentItem(0);
            if (index == R.id.btNavCalendar) viewPager2.setCurrentItem(1);
            if (index == R.id.btNavAnalysis) viewPager2.setCurrentItem(2);
            if (index == R.id.btNavOther) viewPager2.setCurrentItem(3);
            return true;
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.btNavCreate);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.btNavCalendar);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.btNavAnalysis);
                        break;
                    case 3:
                        bottomNavigationView.setSelectedItemId(R.id.btNavOther);
                        break;
                }
            }
        });
    }
}