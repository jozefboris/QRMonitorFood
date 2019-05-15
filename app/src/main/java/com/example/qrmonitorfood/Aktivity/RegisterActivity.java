package com.example.qrmonitorfood.Aktivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.User;
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
import com.example.qrmonitorfood.MainActivity;
import com.example.qrmonitorfood.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    public TextInputLayout emailInputLayout, passwdInputLayout;
    Button btnSignUp;
    ImageButton btnBack;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUser;
    DatabaseReference databaseProducer;
    ProgressBar progressBar;
    private Spinner spinner;
    Producer producer;
    List<Producer> producers = new ArrayList<>();
    InternetConnectionSnackbar connectionSnackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        emailInputLayout = findViewById(R.id.ETemail);
        passwdInputLayout = findViewById(R.id.ETpassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnBack = findViewById(R.id.back);
        progressBar = findViewById(R.id.progressBar);
        databaseUser = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseUser);
        databaseProducer = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProducer);
        spinner = findViewById(R.id.spinner);
        connectionSnackbar = new InternetConnectionSnackbar(RegisterActivity.this,emailInputLayout);
        /*
         * listener pre tlačidlo registrovat sa
         * kontroluje spravny format a vyplnenie formulara
         * po uspesnom vyplneny registruje a zaroven prihlasy pouzivatela a vrati  hlavnu aktivitu
         */

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateEmail() | !validatePassword()  ) {
                    return;
                }

                if (producer.getTitle().equals(getString(R.string.select_producer))) {
                    Toast.makeText(RegisterActivity.this, (R.string.select_producer_error), LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(emailInputLayout.getEditText().getText().toString().trim(), passwdInputLayout.getEditText().getText().toString().trim()).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this.getApplicationContext(),

                                        getString(R.string.toast_not_sucessfull)+ ". " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            } else {

                                SharedPreferences.Editor editor = getSharedPreferences("ID", MODE_PRIVATE).edit();
                                editor.putString("ID", producer.getId() );
                                editor.apply();
                                User user = new User(producer.getId()) ;
                                 databaseUser.child(firebaseAuth.getUid()).setValue(user);

                                finishAffinity();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);


                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            }
        });

        /*
         * listener pre tlačidlo späť na prihlásenie
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /*
         * spiner pre výber firmy
         */

        Producer user1 = new Producer(getString(R.string.select_producer), "");
        producers.add(user1);

        ArrayAdapter<Producer> adapter = new ArrayAdapter<Producer>(this,
                android.R.layout.simple_spinner_item, producers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 producer = (Producer) parent.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
}

    /**
     * Metoda pre aktualizovanie zoznamu firiem
     */
@Override
protected void onRestart() {
    super.onRestart();
    producers.clear();
    Producer user1 = new Producer(getString(R.string.select_producer), "");
    producers.add(user1);


}

    /**
     * Radenie listu výrobcov
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sortByTitle() {

        producers.sort(new Comparator<Producer>() {
            @Override
            public int compare(Producer o1, Producer o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        });

    }


    /**
     * Testuje spravnost vyplnenia emailu
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
     * Test spravnosti hesla
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
     * Metoda pre aktualizovanie zoznamu firiem
     */
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        connectionSnackbar.checkConnection();
        databaseProducer.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Producer track = postSnapshot.getValue(Producer.class);
                    track.setId(postSnapshot.getKey());
                    producers.add(track);
                    progressBar.setVisibility(View.GONE);

                }
  sortByTitle();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * OnClick pre tlačidlo + na pridanie firmy AddProducerActivity
     */
    public void openAddPruducer(View view) {

        Intent intent = new Intent(this, AddProducerActivity.class);
        startActivity(intent);

    }





}
