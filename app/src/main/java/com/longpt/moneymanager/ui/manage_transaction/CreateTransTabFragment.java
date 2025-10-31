package com.longpt.moneymanager.ui.manage_transaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.CreateEditTrans_CateAdapter;
import com.longpt.moneymanager.data.factory.CategoryViewModelFactory;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.CategoryViewModel;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.enums.TypeTransaction;
import com.longpt.moneymanager.helper.DateSelectorHelper;
import com.longpt.moneymanager.ui.manage_category.ViewCategoryContainerActivity;
import com.longpt.moneymanager.util.DatePickerUtil;
import com.longpt.moneymanager.util.ShowCustomToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateTransTabFragment extends Fragment implements CreateEditTrans_CateAdapter.CategoryItemListener {
    public static final String ARG_TYPE = "type";
    public static final String ARG_SELECTED_DATE = "selected_date";
    private final List<Category> categories = new ArrayList<>();
    private TypeTransaction fragmentType;
    private TextView tvDate, btnPrevDate, btnNextDate, tvAmountCreateTrans;
    private EditText etNote, etAmount;
    private Button btAdd;
    private DateSelectorHelper dateHelper;
    private RecyclerView categoryRecyclerView;
    private CreateEditTrans_CateAdapter createEditTransCateAdapter;
    private CategoryViewModel categoryViewModel;
    private TransactionAnalyticsViewModel transactionAnalyticsViewModel;

    public static CreateTransTabFragment newInstance(TypeTransaction type, Calendar selectedDateInCalendarView) {
        CreateTransTabFragment fragment = new CreateTransTabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TYPE, type);
        args.putSerializable(ARG_SELECTED_DATE, selectedDateInCalendarView);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fm_create_trans_tab, container, false);
        fragmentType = (TypeTransaction) getArguments().getSerializable(ARG_TYPE);

        initView(v);
        initViewModel();
        setupDatePicker();
        setupRecyclerView(v);

        Observer<List<Category>> observer = data -> {
            this.categories.clear();
            for (Category c : data) {
                if (!defaultCategoryId().equals(c.getId())) {
                    this.categories.add(c);
                }
            }
            createEditTransCateAdapter.notifyDataSetChanged();


            if (this.categories.isEmpty()) {
                btAdd.setEnabled(false);
                btAdd.setBackgroundColor(Color.GRAY);
            } else {
                btAdd.setEnabled(true);
                btAdd.setBackgroundColor(Color.parseColor("#FF7A00"));
            }
            boolean isIncomeTab = (fragmentType == TypeTransaction.INCOME);
            btAdd.setText(isIncomeTab ? "Nhập khoản thu" : " Nhập khoản chi");
            tvAmountCreateTrans.setText(isIncomeTab ? "Tiền thu" : "Tiền chi");
            btAdd.setText(isIncomeTab ? "Nhập khoản thu" : " Nhập khoản chi");
            tvAmountCreateTrans.setText(isIncomeTab ? "Tiền thu" : "Tiền chi");

        };
        if (fragmentType == TypeTransaction.INCOME) {
            categoryViewModel.getIncomeCategory().observe(getViewLifecycleOwner(), observer);
        } else {
            categoryViewModel.getExpenseCategory().observe(getViewLifecycleOwner(), observer);
        }

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = etNote.getText().toString();
                if (note.isEmpty()) {
                    note = "";
                }

                String amountStr = etAmount.getText().toString();
                if (amountStr.isEmpty()) {
                    ShowCustomToast.show(requireContext(), "Vui lòng nhập số tiền!", R.drawable.ic_error);
                    return;
                }
                long amountValue = Long.parseLong(amountStr);
                if(amountValue==0){
                    ShowCustomToast.show(requireContext(), "Vui lòng nhập số tiền >0!", R.drawable.ic_error);
                    return;
                }
                double amount = Double.parseDouble(amountStr);

                Category selectedCategory = categories.get(createEditTransCateAdapter.getSelectedPosition());
                String categoryId = selectedCategory.getId();

                Calendar selectedDate = dateHelper.getSelectedDate();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String dateStr = sdf.format(selectedDate.getTime());

                String type = (fragmentType == TypeTransaction.INCOME) ? "income" : "expense";
                Transaction transaction = new Transaction(null, amount, note, dateStr, categoryId, type);
                transactionAnalyticsViewModel.addTransaction(transaction, new TransactionResultCallback() {
                    @Override
                    public void onSuccess() {
                        requireActivity().runOnUiThread(() -> {
                            ShowCustomToast.show(requireContext(), "Thêm giao dịch thành công!", R.drawable.ic_success);
                            Fragment parent = getParentFragmentManager().findFragmentByTag("DayTransactionBottomSheet");
                            if (parent instanceof BottomSheetDialogFragment) {
                                ((BottomSheetDialogFragment) parent).dismiss();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        requireActivity().runOnUiThread(() -> {
                            ShowCustomToast.show(requireContext(), "Thêm giao dịch thất bại: " + e.getMessage(), R.drawable.ic_error);
                        });
                    }
                });
            }
        });
        return v;
    }

    private void initView(View v) {
        tvDate = v.findViewById(R.id.tvDate_CreateTrans);
        btnPrevDate = v.findViewById(R.id.btnPrevDate_CreateTrans);
        btnNextDate = v.findViewById(R.id.btnNextDate_CreateTrans);
        tvAmountCreateTrans = v.findViewById(R.id.tvAmount_CreateTrans);
        etNote = v.findViewById(R.id.et_NoteAdd);
        etAmount = v.findViewById(R.id.etAmount_CreateTrans);
        btAdd = v.findViewById(R.id.btnAddTrans);
    }

    private void setupDatePicker() {
        dateHelper = new DateSelectorHelper(tvDate, btnNextDate);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_SELECTED_DATE)) {
            Calendar selectedDate = (Calendar) args.getSerializable(ARG_SELECTED_DATE);
            if (selectedDate != null) {
                dateHelper.setSelectedDate(selectedDate);
            }
        }
        DatePickerUtil.showDatePickerDialog(getContext(), tvDate, btnPrevDate, btnNextDate, dateHelper);
    }

    private void setupRecyclerView(View v) {
        categoryRecyclerView = v.findViewById(R.id.rvCategories_CreateTrans);
        createEditTransCateAdapter = new CreateEditTrans_CateAdapter(categories, getContext());
        createEditTransCateAdapter.setCategoryItemListener(this);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        categoryRecyclerView.setAdapter(createEditTransCateAdapter);
    }

    private void initViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CategoryViewModelFactory categoryViewModelFactory = new CategoryViewModelFactory(uid);
        categoryViewModel = new ViewModelProvider(this, categoryViewModelFactory).get(CategoryViewModel.class);
        TransactionCalendarViewModelFactory transactionCalendarViewModelFactory = new TransactionCalendarViewModelFactory(uid);
        transactionAnalyticsViewModel = new ViewModelProvider(this, transactionCalendarViewModelFactory).get(TransactionAnalyticsViewModel.class);
    }

    private String defaultCategoryId() {
        return fragmentType == TypeTransaction.INCOME ? "unknownIncomeId" : "unknownExpenseId";
    }

    @Override
    public void onCategoryClick(View v, int position) {
    }

    @Override
    public void onEditCategoryTextClick(View v) {
        startActivity(new Intent(getContext(), ViewCategoryContainerActivity.class));
    }
}