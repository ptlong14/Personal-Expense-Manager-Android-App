package com.longpt.moneymanager.ui.manage_category;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.fragmenState.ViewCategoryTabStateAdapter;

public class ViewCategoryContainerActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private ImageButton ibBack;
    private ViewCategoryTabStateAdapter viewCategoryTabStateAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_show_category_container);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        viewPager2 = findViewById(R.id.vPCategory);
        tabLayout = findViewById(R.id.tabEditCategory);
        ibBack = findViewById(R.id.ibBackFromAddCate);

        viewCategoryTabStateAdapter = new ViewCategoryTabStateAdapter(this);
        viewPager2.setAdapter(viewCategoryTabStateAdapter);
        viewPager2.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1.0f - absPos * 0.3f);
            page.setScaleY(1.0f - absPos * 0.1f);
        });
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Tiền chi");
            } else {
                tab.setText("Tiền thu");
            }
        }).attach();

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}