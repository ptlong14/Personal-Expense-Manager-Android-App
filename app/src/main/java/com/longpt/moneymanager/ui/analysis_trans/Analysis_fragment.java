package com.longpt.moneymanager.ui.analysis_trans;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.fragmenState.AnalysisOverviewTabStateAdapter;
import com.longpt.moneymanager.ui.search_transaction.SearchTransactionActivity;
import com.longpt.moneymanager.util.ScreenshotUtils;

public class Analysis_fragment extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private AnalysisOverviewTabStateAdapter adapter;
    private ImageView ivSearch, ivShare;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_analysis_container, container, false);

        viewPager2 = v.findViewById(R.id.vpAnalysisOverview);
        tabLayout = v.findViewById(R.id.tabAnalysisOverview);
        ivSearch = v.findViewById(R.id.ivSearchOVV);
        ivShare= v.findViewById(R.id.ivShareOVV);

        adapter = new AnalysisOverviewTabStateAdapter(getActivity());
        viewPager2.setAdapter(adapter);
        viewPager2.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1.0f - absPos * 0.3f);
            page.setScaleY(1.0f - absPos * 0.1f);
        });

        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position == 0) {
                tab.setText("Hàng tháng");
            } else {
                tab.setText("Hàng năm");
            }
        }).attach();

        ivSearch.setOnClickListener(view -> {
            startActivity(new Intent(getActivity(), SearchTransactionActivity.class));
        });

        ivShare.setOnClickListener(view->{
            ScreenshotUtils.captureAndShare(
                    requireActivity(),
                    requireActivity().getWindow().getDecorView().getRootView()
            );
        });
        return v;
    }
}
