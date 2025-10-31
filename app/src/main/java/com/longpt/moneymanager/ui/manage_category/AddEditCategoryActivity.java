package com.longpt.moneymanager.ui.manage_category;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.adapter.recyclerView.ColorAddAdapter;
import com.longpt.moneymanager.adapter.recyclerView.IconAddAdapter;
import com.longpt.moneymanager.data.factory.CategoryViewModelFactory;
import com.longpt.moneymanager.data.model.Category;
import com.longpt.moneymanager.data.viewmodel.CategoryViewModel;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEditCategoryActivity extends AppCompatActivity {

    private final List<Integer> colors = new ArrayList<>(Arrays.asList(Color.RED, Color.GREEN, Color.YELLOW, Color.GRAY, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.rgb(255, 165, 0), Color.rgb(0, 128, 0), Color.rgb(255, 192, 203), Color.rgb(255, 105, 180), Color.rgb(22, 24, 25), Color.rgb(255, 128, 0), Color.rgb(255, 0, 0), Color.TRANSPARENT));

    private RecyclerView rvIcon, rvColor;
    private IconAddAdapter iconAddAdapter;
    private ColorAddAdapter colorAddAdapter;
    private ImageButton imgBack;
    private TextView tvTitle;
    private EditText etNameAdd;
    private Button btAddCate;

    private CategoryViewModel categoryViewModel;
    private String type_add;
    private String type_update;
    private Category categoryUpdate;

    private boolean isAddCategory = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_add_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imgBack = findViewById(R.id.ibBackFromAddCate);
        etNameAdd = findViewById(R.id.et_NoteAdd);
        rvIcon = findViewById(R.id.rvCateIconAdd);
        rvColor = findViewById(R.id.rvCateColorAdd);
        btAddCate = findViewById(R.id.btnAdd_Cate);
        tvTitle= findViewById(R.id.tvTitleCreateOrUpdateCategory);


        imgBack.setOnClickListener(v -> finish());

        rvIcon.setLayoutManager(new GridLayoutManager(this, 4));
        iconAddAdapter = new IconAddAdapter(getIconList(), this);
        rvIcon.setAdapter(iconAddAdapter);

        rvColor.setLayoutManager(new GridLayoutManager(this, 5));
        colorAddAdapter = new ColorAddAdapter(colors, (color, isCustom) -> {
            if (isCustom) {
                new ColorPickerDialog.Builder(this).setTitle("Chọn màu nâng cao 🌈").setPositiveButton("Xác nhận", (ColorEnvelopeListener) (envelope, fromUser) -> {
                    int pickedColor = envelope.getColor();
                    iconAddAdapter.setColorForSelected(pickedColor);
                }).setNegativeButton("Huỷ", (dialogInterface, i) -> dialogInterface.dismiss()).attachAlphaSlideBar(true).attachBrightnessSlideBar(true).setBottomSpace(12).show();
            } else {
                iconAddAdapter.setColorForSelected(color);
            }
        });
        rvColor.setAdapter(colorAddAdapter);
        Intent t = getIntent();
        if (t != null && t.hasExtra("type_add")) {
            type_add = getIntent().getStringExtra("type_add");
            tvTitle.setText(getIntent().getStringExtra("title_add"));
            isAddCategory = true;
        } else if (t != null && t.hasExtra("type_update")) {
            type_update = getIntent().getStringExtra("type_update");
            categoryUpdate = (Category) getIntent().getSerializableExtra("category_update");
            tvTitle.setText(getIntent().getStringExtra("title_update"));
            isAddCategory = false;
            iconAddAdapter.setColorForSelected(Color.parseColor(categoryUpdate.getColor()));
            etNameAdd.setText(categoryUpdate.getName());
            iconAddAdapter.setSelectedIcon(categoryUpdate.getIcon());
            rvIcon.scrollToPosition(iconAddAdapter.getSelectedPosition());
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CategoryViewModelFactory categoryViewModelFactory = new CategoryViewModelFactory(uid);
        categoryViewModel = new ViewModelProvider(this, categoryViewModelFactory).get(CategoryViewModel.class);


        btAddCate.setOnClickListener(v -> handleAddCategory());
    }

    private void handleAddCategory() {
        if (isAddCategory) handleAdd();
        else handleUpdate();

    }


    private List<GoogleMaterial.Icon> getIconList() {
        List<GoogleMaterial.Icon> icons = new ArrayList<>();
        for (GoogleMaterial.Icon icon : GoogleMaterial.Icon.values()) {
            icons.add(icon);
            if (icons.size() >= 1500) break;
        }
        return icons;
    }


    private void handleAdd() {
        String name = etNameAdd.getText().toString().trim();
        GoogleMaterial.Icon selectedIcon = iconAddAdapter.getSelectedIconEnum();
        int color = iconAddAdapter.getSelectedColor();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedIcon == null) {
            Toast.makeText(this, "Chọn 1 biểu tượng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (color == Color.TRANSPARENT) {
            Toast.makeText(this, "Chọn 1 màu!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Category> list;
        if ("income".equals(type_add)) {
            list = categoryViewModel.getIncomeCategory().getValue();
        } else {
            list = categoryViewModel.getExpenseCategory().getValue();
        }

        boolean isDuplicate = false;
        if (list != null) {
            for (Category c : list) {
                if (c.getName().equals(name)) {
                    isDuplicate = true;
                    break;
                }
            }
        }

        if (isDuplicate) {
            Toast.makeText(this, "Tên danh mục đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }

        Category c = new Category();
        c.setId(null);
        c.setName(name);
        c.setIcon(selectedIcon.name());
        c.setColor(String.format("#%06X", (0xFFFFFF & color)));

        categoryViewModel.addCategory(type_add, c, new CategoryResultCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditCategoryActivity.this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditCategoryActivity.this, "Thêm danh mục thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void handleUpdate() {
        String name = etNameAdd.getText().toString().trim();
        GoogleMaterial.Icon selectedIcon = iconAddAdapter.getSelectedIconEnum();
        int color = iconAddAdapter.getSelectedColor();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedIcon == null) {
            Toast.makeText(this, "Chọn 1 biểu tượng!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (color == Color.TRANSPARENT) {
            Toast.makeText(this, "Chọn 1 màu!", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Category> list;
        if ("income".equals(type_update)) {
            list = categoryViewModel.getIncomeCategory().getValue();
        } else {
            list = categoryViewModel.getExpenseCategory().getValue();
        }

        boolean isDuplicate = false;
        if (list != null) {
            for (Category c : list) {
                if (c.getName().equalsIgnoreCase(name) && !c.getId().equals(categoryUpdate.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
        }

        if (isDuplicate) {
            Toast.makeText(this, "Tên danh mục đã tồn tại!", Toast.LENGTH_SHORT).show();
            return;
        }
        Category c = new Category();
        c.setId(categoryUpdate.getId());
        c.setName(name);
        c.setIcon(selectedIcon.name());
        c.setColor(String.format("#%06X", (0xFFFFFF & color)));

        categoryViewModel.updateCategory(type_update, c, new CategoryResultCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditCategoryActivity.this, "Sửa danh mục thành công", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(AddEditCategoryActivity.this, "Sửa danh mục thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}