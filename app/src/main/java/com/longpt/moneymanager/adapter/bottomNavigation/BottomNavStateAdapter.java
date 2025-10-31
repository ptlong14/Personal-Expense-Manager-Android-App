package com.longpt.moneymanager.adapter.bottomNavigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longpt.moneymanager.ui.analysis_trans.Analysis_fragment;
import com.longpt.moneymanager.ui.calendar_view.CalendarFragment;
import com.longpt.moneymanager.ui.manage_transaction.CreateTransContainerFragment;
import com.longpt.moneymanager.ui.others_features.Other_Fragment;

public class BottomNavStateAdapter extends FragmentStateAdapter {
    public BottomNavStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new CreateTransContainerFragment();
            case 1: return new CalendarFragment();
            case 2: return new Analysis_fragment();
            case 3: return new Other_Fragment();
            default: return new CreateTransContainerFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
