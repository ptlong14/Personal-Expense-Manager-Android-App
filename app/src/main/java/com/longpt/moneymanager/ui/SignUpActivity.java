package com.longpt.moneymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.longpt.moneymanager.R;
import com.longpt.moneymanager.data.model.Category;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, confirmSignupPassword;
    private Button signupButton;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.act_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.edtSignupEmail);
        signupPassword = findViewById(R.id.edtSignupPassword);
        signupButton = findViewById(R.id.btnSignup);
        tvToLogin = findViewById(R.id.tvToLogin);
        confirmSignupPassword = findViewById(R.id.edtConfirmSignupPassword);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signupEmail.getText().toString();
                String password = signupPassword.getText().toString();
                String confirmPassword = confirmSignupPassword.getText().toString();
                if (email.isEmpty()) {
                    signupEmail.setError("Email không được để trống!");
                    return;
                }
                if (password.isEmpty()) {
                    signupPassword.setError("Mật khẩu không được để trống!");
                    return;
                }

                if (confirmPassword.isEmpty()) {
                    confirmSignupPassword.setError("Nhập lại mật khẩu!");
                    return;
                }

                if (!confirmPassword.equals(password)) {
                    confirmSignupPassword.setError("Mật khẩu nhập lại không khớp");
                    return;
                }
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            //Tạo danh sách danh mục thu chi cho người mới
                            createDefaultCategories(uid);

                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công. Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại. Lỗi: " + task.getException().getMessage() + "!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }

    private void createDefaultCategories(String uid) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference cateRef = db.child(uid).child("categories");

        String unknownIdIncome = "unknownIncomeId"; // ID đặc biệt để phân biệt
        Category unknownCate = new Category(unknownIdIncome, "Không rõ", GoogleMaterial.Icon.gmd_help_outline.name(), "#9E9E9E");

        cateRef.child("income").child(unknownIdIncome).setValue(unknownCate);

        String incomeId1 = cateRef.child("income").push().getKey();
        cateRef.child("income").child(incomeId1).setValue(new Category(incomeId1, "Lương", GoogleMaterial.Icon.gmd_attach_money.name(), "#4CAF50"));

        String incomeId2 = cateRef.child("income").push().getKey();
        cateRef.child("income").child(incomeId2).setValue(new Category(incomeId2, "Thưởng", GoogleMaterial.Icon.gmd_card_giftcard.name(), "#FF9800"));

        String incomeId3 = cateRef.child("income").push().getKey();
        cateRef.child("income").child(incomeId3).setValue(new Category(incomeId3, "Đầu tư", GoogleMaterial.Icon.gmd_trending_up.name(), "#03A9F4"));

        String incomeId4 = cateRef.child("income").push().getKey();
        cateRef.child("income").child(incomeId4).setValue(new Category(incomeId4, "Bán hàng", GoogleMaterial.Icon.gmd_shopping_cart.name(), "#E91E63"));

        String unknownIdExpense = "unknownExpenseId";
        Category unknownCate1 = new Category(unknownIdExpense, "Không rõ", GoogleMaterial.Icon.gmd_help_outline.name(), "#9E9E9E");

        cateRef.child("expense").child(unknownIdExpense).setValue(unknownCate1);
        String expenseId1 = cateRef.child("expense").push().getKey();
        cateRef.child("expense").child(expenseId1).setValue(new Category(expenseId1, "Ăn uống", GoogleMaterial.Icon.gmd_restaurant.name(), "#FF5722"));

        String expenseId2 = cateRef.child("expense").push().getKey();
        cateRef.child("expense").child(expenseId2).setValue(new Category(expenseId2, "Giải trí", GoogleMaterial.Icon.gmd_local_play.name(), "#3F51B5"));

        String expenseId3 = cateRef.child("expense").push().getKey();
        cateRef.child("expense").child(expenseId3).setValue(new Category(expenseId3, "Mua sắm", GoogleMaterial.Icon.gmd_shopping_basket.name(), "#9E9E9E"));

        String expenseId4 = cateRef.child("expense").push().getKey();
        cateRef.child("expense").child(expenseId4).setValue(new Category(expenseId4, "Đi lại", GoogleMaterial.Icon.gmd_directions_car.name(), "#795548"));

    }
}