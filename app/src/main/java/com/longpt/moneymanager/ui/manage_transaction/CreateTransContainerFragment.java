package com.longpt.moneymanager.ui.manage_transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.fragmenState.CreateTransTabStateAdapter;
public class CreateTransContainerFragment extends Fragment {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private CreateTransTabStateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fm_create_trans_container, container, false);

        viewPager2 = view.findViewById(R.id.vpCreateTrans);
        tabLayout = view.findViewById(R.id.tabEditTrans);

        adapter = new CreateTransTabStateAdapter(getActivity(), null);
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
}