package com.longpt.moneymanager.adapter.fragmenState;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longpt.moneymanager.enums.ScopeAnalysis;
import com.longpt.moneymanager.ui.analysis_trans.ViewAnalysisTabFragment;

public class AnalysisOverviewTabStateAdapter extends FragmentStateAdapter {
    public AnalysisOverviewTabStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ViewAnalysisTabFragment.newInstance(ScopeAnalysis.MONTH_ANALYSIS);
            case 1:
                return ViewAnalysisTabFragment.newInstance(ScopeAnalysis.YEAR_ANALYSIS);
            default:
                return ViewAnalysisTabFragment.newInstance(ScopeAnalysis.MONTH_ANALYSIS);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
