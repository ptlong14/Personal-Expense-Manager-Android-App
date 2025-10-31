package com.longpt.moneymanager.adapter.fragmenState;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.longpt.moneymanager.enums.TypeTransaction;
import com.longpt.moneymanager.ui.manage_transaction.CreateTransTabFragment;

import java.util.Calendar;

public class CreateTransTabStateAdapter extends FragmentStateAdapter {
    private final Calendar selectedDateInCalendarView;
    public CreateTransTabStateAdapter(@NonNull FragmentActivity fragmentActivity, Calendar selectedDateInCalendarView) {
        super(fragmentActivity);
        this.selectedDateInCalendarView = selectedDateInCalendarView;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return CreateTransTabFragment.newInstance(TypeTransaction.EXPENSE, selectedDateInCalendarView);
            case 1:
                return CreateTransTabFragment.newInstance(TypeTransaction.INCOME, selectedDateInCalendarView);
            default:
                return CreateTransTabFragment.newInstance(TypeTransaction.EXPENSE,selectedDateInCalendarView);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
