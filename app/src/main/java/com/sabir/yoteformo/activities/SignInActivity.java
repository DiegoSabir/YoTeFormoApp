package com.sabir.yoteformo.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sabir.yoteformo.R;

public class SignInActivity extends AppCompatActivity {

    private TextInputEditText etUser, etPassword;
    private MaterialButton btnSignIn;
    private MaterialCheckBox cbRememberUser;

    private SharedPreferences sharedPreferences;

    private FirebaseAuth auth;
    private MaterialButton btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        etUser = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberUser = findViewById(R.id.cbRememberUser);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        auth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loadPreferences();

        btnSignIn.setOnClickListener(view -> {
            String email = etUser.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                validateLogin(email, password);
            }
            else {
                Toast.makeText(SignInActivity.this, "Introduzca los campos faltantes", Toast.LENGTH_SHORT).show();
            }
        });
        btnForgotPassword.setOnClickListener(view -> showForgotPasswordDialog());
    }


    private void loadPreferences() {
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);

        if (savedEmail != null && savedPassword != null) {
            etUser.setText(savedEmail);
            etPassword.setText(savedPassword);
            cbRememberUser.setChecked(true);
        }
    }

    private void validateLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                if (cbRememberUser.isChecked()) {
                    savePreferences(email, password);
                }
                else {
                    clearPreferences();
                }
                String userId = auth.getCurrentUser().getUid();
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(SignInActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnSend).setOnClickListener(view -> {
            String userEmail = etEmail.getText().toString();

            if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                Toast.makeText(SignInActivity.this, "Introduzca el correo electronico del usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(userEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Comprueba el correo electrónico", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(SignInActivity.this, "No se ha detectado ningun correo electronico", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(view -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }

    private void savePreferences(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }


    private void clearPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}