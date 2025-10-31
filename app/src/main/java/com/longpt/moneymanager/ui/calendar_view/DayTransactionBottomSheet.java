package com.longpt.moneymanager.ui.calendar_view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.fragmenState.CreateTransTabStateAdapter;

import java.util.Calendar;

public class DayTransactionBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_DATE = "arg_date";

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private CreateTransTabStateAdapter adapter;

    public static DayTransactionBottomSheet newInstance(Calendar date) {
        DayTransactionBottomSheet fragment = new DayTransactionBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fm_create_trans_container, container, false);

        if (getArguments() != null) {
            Calendar selectedDate = (Calendar) getArguments().getSerializable(ARG_DATE);
            TextView tvAction = view.findViewById(R.id.tvDismissSheetDialog);
            tvAction.setText("Bỏ qua");
            tvAction.setVisibility(View.VISIBLE);
            tvAction.setOnClickListener(v -> dismiss());

        }
        viewPager2 = view.findViewById(R.id.vpCreateTrans);
        tabLayout = view.findViewById(R.id.tabEditTrans);

        adapter = new CreateTransTabStateAdapter(getActivity(), (Calendar) getArguments().getSerializable(ARG_DATE));
        viewPager2.setAdapter(adapter);
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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            behavior.setSkipCollapsed(true);

            int maxHeight = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            parent.getLayoutParams().height = maxHeight;
            parent.requestLayout();
        }
    }

}
