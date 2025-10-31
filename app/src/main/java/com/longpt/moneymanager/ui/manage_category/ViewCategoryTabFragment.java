package com.longpt.moneymanager.ui.manage_category;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.ViewCategoryHomeAdapter;
import com.longpt.moneymanager.data.factory.CategoryViewModelFactory;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.viewmodel.CategoryViewModel;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.enums.TypeTransaction;

import java.util.ArrayList;
import java.util.List;

public class ViewCategoryTabFragment extends Fragment implements ViewCategoryHomeAdapter.CategoryItemListener {
    public static final String ARG_TYPE = "type";
    private final List<Category> categories = new ArrayList<>();
    private TypeTransaction fragmentType;
    private RecyclerView recyclerView;
    private ViewCategoryHomeAdapter viewCategoryHomeAdapter;
    private CategoryViewModel categoryViewModel;
    private TransactionAnalyticsViewModel transactionAnalyticsViewModel;
    private LinearLayout layoutAddCate;

    public static ViewCategoryTabFragment newInstance(TypeTransaction type) {
        ViewCategoryTabFragment fragment = new ViewCategoryTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_show_category_tab, container, false);
        fragmentType = (TypeTransaction) getArguments().getSerializable(ARG_TYPE);

        recyclerView = v.findViewById(R.id.rvCategories_editCateHome);
        viewCategoryHomeAdapter = new ViewCategoryHomeAdapter(categories, getContext());
        viewCategoryHomeAdapter.setCategoryItemListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(viewCategoryHomeAdapter);
        layoutAddCate = v.findViewById(R.id.layoutAddCateRedirect);
        layoutAddCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEditCategoryActivity.class);
                if (fragmentType == TypeTransaction.INCOME) {
                    intent.putExtra("type_add", "income");
                } else {
                    intent.putExtra("type_add", "expense");
                }
                intent.putExtra("title_add", "Tạo mới");
                startActivity(intent);
            }
        });

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CategoryViewModelFactory categoryViewModelFactory = new CategoryViewModelFactory(uid);
        categoryViewModel = new ViewModelProvider(this, categoryViewModelFactory).get(CategoryViewModel.class);
        TransactionCalendarViewModelFactory transactionCalendarViewModelFactory = new TransactionCalendarViewModelFactory(uid);
        transactionAnalyticsViewModel = new ViewModelProvider(requireActivity(), transactionCalendarViewModelFactory).get(TransactionAnalyticsViewModel.class);
        if (fragmentType == TypeTransaction.INCOME) {
            categoryViewModel.getIncomeCategory().observe(getViewLifecycleOwner(), categories -> {
                this.categories.clear();
                for (Category c : categories) {
                    if (!"unknownIncomeId".equals(c.getId())) {
                        this.categories.add(c);
                    }
                }
                viewCategoryHomeAdapter.notifyDataSetChanged();
            });
        } else if (fragmentType == TypeTransaction.EXPENSE) {
            categoryViewModel.getExpenseCategory().observe(getViewLifecycleOwner(), categories -> {
                this.categories.clear();
                for (Category c : categories) {
                    if (!"unknownExpenseId".equals(c.getId())) {
                        this.categories.add(c);
                    }
                }
                viewCategoryHomeAdapter.notifyDataSetChanged();
            });
        }
        return v;

    }

    @Override
    public void onPause() {
        super.onPause();
        viewCategoryHomeAdapter.closeOpenSwipe();
    }

    @Override
    public void onCategoryClick(View v, int position) {
        Category c = categories.get(position);
        Intent t = new Intent(getActivity(), AddEditCategoryActivity.class);
        if (fragmentType == TypeTransaction.INCOME) {
            t.putExtra("type_update", "income");
        } else {
            t.putExtra("type_update", "expense");
        }
        t.putExtra("category_update", c);
        t.putExtra("title_update", "Chỉnh sửa");
        startActivity(t);
    }

    @Override
    public void onDeleteCategoryClick(Category category, int position) {
        new AlertDialog.Builder(requireContext()).setTitle("Xác nhận xóa").setMessage("Bạn có chắc muốn xóa '" + category.getName() + "'?").setPositiveButton("Xóa", (dialog, which) -> {
            categories.remove(position);
            viewCategoryHomeAdapter.notifyItemRemoved(position);

            String type = (fragmentType == TypeTransaction.INCOME) ? "income" : "expense";
            categoryViewModel.deleteCategory(type, category.getId(), new CategoryResultCallback() {
                @Override
                public void onSuccess() {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Xóa danh mục thành công!", Toast.LENGTH_SHORT).show());
                    transactionAnalyticsViewModel.loadData();
                }

                @Override
                public void onFailure(Exception e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(), "Xóa thất bại!", Toast.LENGTH_SHORT).show());
                }
            });
        }).setNegativeButton("Hủy", (dialog, which) -> viewCategoryHomeAdapter.notifyItemChanged(position)).show();
    }
}