package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.User;
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
import com.example.qrmonitorfood.MainActivity;
import com.example.qrmonitorfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    public TextInputLayout emailInputLayout, passwdInputLayout;
    Button btnLogIn;
    ImageButton btnBack;
    TextView signup;
    TextView reset;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    ProgressBar progressBar;
    DatabaseReference databaseUser;
    User user = new User();
    InternetConnectionSnackbar connectionSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailInputLayout = findViewById(R.id.loginEmail);
        passwdInputLayout = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        signup = findViewById(R.id.TVSignIn);
        reset = findViewById(R.id.TVReset);
        btnBack = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        databaseUser = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseUser);
        connectionSnackbar = new InternetConnectionSnackbar(LoginActivity.this, emailInputLayout);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, R.string.toast_sucessfull_sing_in, Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(I);
                }
            }
        };

        /*
         * listener pre text registrovať sa otvori RegisterActivity
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(I);
            }
        });

        /*
         * listener pre tlačidlo späť na mainActivity
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
         * listener pre text resetovat heslo otvori ResetPasswordActivity
         */
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(I);
            }
        });

        /*
         * listener pre tlačidlo prihlasit sa
         * kontroluje spravny format a vyplnenie formulara
         * po uspesnom vyplneny prihlasy uzivatela a vrati  hlavnu aktivitu
         */
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateEmail() | !validatePassword()  ) {
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(emailInputLayout.getEditText().getText().toString().trim(), passwdInputLayout.getEditText().getText().toString().trim()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, getString(R.string.toast_not_sucessfull_signIn) + ". " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {

                            addProducer();
                            finishAffinity();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
        );

    }

    /**
     * testuje spravnost vyplnenia emailu
     * @return true/false ak neiej prazdne pole vrati true
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

    /**
     * test spravnosti hesla
     * @return true / false true ak je dlžka hesla väčšia ako 5 znakov a nieje prázdne pole
     */
    private boolean validatePassword() {
        String passwordInput = passwdInputLayout.getEditText().getText().toString().trim();

     if (passwordInput.isEmpty()) {
         passwdInputLayout.setError(getString(R.string.toast_password));
         return false;
        } else   if (passwordInput.length()<6) {
         passwdInputLayout.setError(getString(R.string.password_short));
            return false;
        }

        else {
         passwdInputLayout.setError(null);
         return true;
     }

     }

    /**
     * kontrluje pripojenie a nastavi prihlasovaci posluchač stavu
     */
    @Override
    protected void onStart() {
        super.onStart();
        connectionSnackbar.checkConnection();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    /**
     * načitanie firmy pre prihlaseneho uživatela a uloženie do share preferences
     */

    public void addProducer() {
        firebaseAuth = FirebaseAuth.getInstance();

        databaseUser.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                SharedPreferences.Editor editor = getSharedPreferences("ID", MODE_PRIVATE).edit();
                editor.putString("ID", user.getProducerId() );
                editor.apply();
                progressBar.setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }
}
