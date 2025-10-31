package com.longpt.moneymanager.adapter.fragmenState;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longpt.moneymanager.enums.TypeTransaction;
import com.longpt.moneymanager.ui.manage_category.ViewCategoryTabFragment;

public class ViewCategoryTabStateAdapter extends FragmentStateAdapter {
    public ViewCategoryTabStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return ViewCategoryTabFragment.newInstance(TypeTransaction.EXPENSE);
            case 1:
                return ViewCategoryTabFragment.newInstance(TypeTransaction.INCOME);
            default:
                return ViewCategoryTabFragment.newInstance(TypeTransaction.EXPENSE);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
