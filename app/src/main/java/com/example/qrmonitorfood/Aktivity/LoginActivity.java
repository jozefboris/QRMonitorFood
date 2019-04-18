package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.User;
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
    public EditText loginEmailId, logInpasswd;
    Button btnLogIn;
    ImageButton btnBack;
    TextView signup;
    TextView reset;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    ProgressBar progressBar;
    DatabaseReference databaseUser;
    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInpasswd = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        signup = findViewById(R.id.TVSignIn);
        reset = findViewById(R.id.TVReset);
        btnBack = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        databaseUser = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseUser);
        firebaseAuth.setLanguageCode("sk");
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginActivity.this, R.string.toast_sucessfull_sing_in, Toast.LENGTH_SHORT).show();
                    Intent I = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(I);
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_sing_in, Toast.LENGTH_SHORT).show();
                }
            }
        };

        /**
         * listener pre text registrovať sa
         */
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(I);
            }
        });

        /**
         * listener pre tlačidlo späť na mainActivity
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * listener pre text resetovat heslo
         */
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(I);
            }
        });

        /**
         * listener pre tlačidlo prihlasit sa
         */
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = loginEmailId.getText().toString();
                String userPaswd = logInpasswd.getText().toString();
                if (userEmail.isEmpty()) {
                    loginEmailId.setError(getString(R.string.toast_write_mail));
                    loginEmailId.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    logInpasswd.setError(getString(R.string.toast_password));
                    logInpasswd.requestFocus();
                } else if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.toast_empty_fields, Toast.LENGTH_SHORT).show();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            addProducer();
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, getString(R.string.toast_not_sucessfull_signIn) + ". " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                 finish();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, R.string.toast_error_sing_in, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

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
