package com.longpt.moneymanager.ui.manage_transaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.CreateEditTrans_CateAdapter;
import com.longpt.moneymanager.data.factory.CategoryViewModelFactory;
import com.longpt.moneymanager.data.factory.TransactionCalendarViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.model.Transaction;
import com.longpt.moneymanager.data.viewmodel.CategoryViewModel;
import com.longpt.moneymanager.data.viewmodel.TransactionAnalyticsViewModel;
import com.longpt.moneymanager.helper.DateSelectorHelper;
import com.longpt.moneymanager.ui.manage_category.ViewCategoryContainerActivity;
import com.longpt.moneymanager.util.DatePickerUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditTransactionActivity extends AppCompatActivity implements CreateEditTrans_CateAdapter.CategoryItemListener {

    private final List<Category> categories = new ArrayList<>();
    private TextView tvDate, btnPrevDate, btnNextDate, tvAmountEditTrans;
    private ImageButton ivBackFromEditTrans;
    private EditText etNote, etAmount;
    private Button btEdit;
    private DateSelectorHelper dateHelper;
    private CreateEditTrans_CateAdapter createEditTransCateAdapter;
    private RecyclerView categoryRecyclerView;
    private CategoryViewModel categoryViewModel;
    private TransactionAnalyticsViewModel transactionAnalyticsViewModel;

    private Transaction editTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_edit_trans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initView();
        initViewModel();
        setupDatePicker();
        getIntentData();
        setupRecyclerView();

        boolean isIncome = "income".equals(editTransaction.getType());
        Observer<List<Category>> observer = data -> {
            this.categories.clear();
            String defaultId = isIncome ? "unknownIncomeId" : "unknownExpenseId";
            for (Category c : data) {
                if (!defaultId.equals(c.getId())) {
                    this.categories.add(c);
                }
            }
            btEdit.setText(isIncome ? "Sửa khoản thu" : "Sửa khoản chi");
            tvAmountEditTrans.setText(isIncome ? "Tiền thu" : "Tiền chi");

            int selectedIndex = RecyclerView.NO_POSITION;
            for (int i = 0; i < this.categories.size(); i++) {
                if (this.categories.get(i).getId().equals(editTransaction.getCategoryId())) {
                    selectedIndex = i;
                    break;
                }
            }
            createEditTransCateAdapter.setSelectedPosition(selectedIndex);

            if (this.categories.isEmpty() || selectedIndex == RecyclerView.NO_POSITION) {
                btEdit.setEnabled(false);
                btEdit.setBackgroundColor(Color.GRAY);
            } else {
                btEdit.setEnabled(true);
                btEdit.setBackgroundColor(Color.parseColor("#FF7A00"));
            }
            createEditTransCateAdapter.notifyDataSetChanged();
        };
        if (isIncome) {
            categoryViewModel.getIncomeCategory().observe(this, observer);
        } else {
            categoryViewModel.getExpenseCategory().observe(this, observer);
        }

        ivBackFromEditTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editNote = etNote.getText().toString();
                if (editNote.isEmpty()) {
                    editNote = "";
                }
                String editAmountStr = etAmount.getText().toString();
                if (editAmountStr.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập số tiền!", Toast.LENGTH_SHORT).show();
                    return;
                }
                double editAmount = Double.parseDouble(editAmountStr);
                Category selectedCategory= categories.get(createEditTransCateAdapter.getSelectedPosition());
                String categoryId= selectedCategory.getId();
                Calendar selectedDate= dateHelper.getSelectedDate();
                SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String editDateStr= sdf.format(selectedDate.getTime());

                editTransaction.setDate(editDateStr);
                editTransaction.setNote(editNote);
                editTransaction.setAmount(editAmount);
                editTransaction.setCategoryId(categoryId);

                transactionAnalyticsViewModel.updateTransaction(editTransaction, new TransactionResultCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(()->{
                            Toast.makeText(getApplicationContext(), "Sửa giao dịch thành công!", Toast.LENGTH_SHORT).show();
                        });
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(()->{
                            Toast.makeText(getApplicationContext(), "Sửa giao dịch thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
        });
    }


    private void setupRecyclerView() {
        categoryRecyclerView = findViewById(R.id.rvCategories_EditTrans);
        createEditTransCateAdapter = new CreateEditTrans_CateAdapter(categories, this);
        createEditTransCateAdapter.setCategoryItemListener(this);
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        categoryRecyclerView.setAdapter(createEditTransCateAdapter);
    }

    private void setupDatePicker() {
        dateHelper = new DateSelectorHelper(tvDate, btnNextDate);

        DatePickerUtil.showDatePickerDialog(this, tvDate, btnPrevDate, btnNextDate, dateHelper);
    }

    private void getIntentData() {
        Intent t = getIntent();
        editTransaction = (Transaction) t.getSerializableExtra("edit_transaction");
        if (editTransaction != null) {
            tvDate.setText(editTransaction.getDate());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(editTransaction.getDate());
                if (date != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    dateHelper.setSelectedDate(cal);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            etNote.setText(editTransaction.getNote());
            etAmount.setText(String.valueOf((long) editTransaction.getAmount()));
        }
    }

    private void initView() {
        tvDate = findViewById(R.id.tvDate_EditTrans);
        btnPrevDate = findViewById(R.id.btnPrevDate_EditTrans);
        btnNextDate = findViewById(R.id.btnNextDate_EditTrans);
        tvAmountEditTrans = findViewById(R.id.tvAmount_EditTrans);
        etNote = findViewById(R.id.etNote_EditTrans);
        etAmount = findViewById(R.id.etAmount_EditTrans);
        btEdit = findViewById(R.id.btnEditTrans);
        ivBackFromEditTrans = findViewById(R.id.ivBackFromEditTrans);
    }

    private void initViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CategoryViewModelFactory categoryViewModelFactory = new CategoryViewModelFactory(uid);
        categoryViewModel = new ViewModelProvider(this, categoryViewModelFactory).get(CategoryViewModel.class);

        TransactionCalendarViewModelFactory transactionCalendarViewModelFactory = new TransactionCalendarViewModelFactory(uid);
        transactionAnalyticsViewModel = new ViewModelProvider(this, transactionCalendarViewModelFactory).get(TransactionAnalyticsViewModel.class);
    }

    @Override
    public void onCategoryClick(View v, int position) {
        btEdit.setEnabled(true);
        btEdit.setBackgroundColor(Color.parseColor("#FF7A00"));
    }

    @Override
    public void onEditCategoryTextClick(View v) {
        startActivity(new Intent(EditTransactionActivity.this, ViewCategoryContainerActivity.class));
    }
}