package com.example.qrmonitorfood.Aktivity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
import com.example.qrmonitorfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout;
    private Button btnReset;
    private ImageButton btnBack;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    InternetConnectionSnackbar connectionSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        emailInputLayout = findViewById(R.id.email);
        btnReset = findViewById(R.id.btn_reset_password);
        btnBack = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        connectionSnackbar = new InternetConnectionSnackbar(ResetPasswordActivity.this,emailInputLayout);
        connectionSnackbar.checkConnection();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

// Listener pre tlačidlo resetovať heslo
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(validateEmail())) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(emailInputLayout.getEditText().getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ResetPasswordActivity.this, R.string.toast_send_mail, Toast.LENGTH_SHORT).show();
                                     finish();
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.toast_falled_send_email) +". " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }

    /**
     * Testuje správnosť vyplnenia hesla
     * @return boolean podla spravnosti vyplnenia
     */
    private boolean validateEmail() {
        String emailInput = emailInputLayout.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(emailInput)) {
            emailInputLayout.setError(getString(R.string.toast_reset_add_email));

            return false;

        } else {
            emailInputLayout.setError(null);
            return true;
        }
    }

}
