package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.User;
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
import java.util.List;
import static android.widget.Toast.LENGTH_SHORT;

public class RegisterActivity extends AppCompatActivity {

    public EditText emailId, passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUser;
    DatabaseReference databaseProducer;
    ProgressBar progressBar;
    private Spinner spinner;
    Producer producer;
    List<Producer> producers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ETemail);
        passwd = findViewById(R.id.ETpassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.TVSignIn);
        progressBar = findViewById(R.id.progressBar);
        databaseUser = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseUser);
        databaseProducer = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProducer);
        spinner = findViewById(R.id.spinner);



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = emailId.getText().toString().trim();
                String paswd = passwd.getText().toString().trim();
                if (emailID.isEmpty()) {
                    emailId.setError(getString(R.string.toast_write_mail));
                    emailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    passwd.setError(getString(R.string.toast_set_password));
                    passwd.requestFocus(); }
                    else if (producer.getTitle().equals(getString(R.string.select_producer))) {
                    Toast.makeText(RegisterActivity.this, (R.string.select_producer_error), LENGTH_SHORT).show();
                } else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_empty_fields, LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this.getApplicationContext(),
                                        getString(R.string.toast_not_sucessfull)+ ". " + task.getException().getMessage(),
                                        LENGTH_SHORT).show();
                            } else {
                                User user = new User(producer.getId()) ;
                                 databaseUser.child(firebaseAuth.getUid()).setValue(user);
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, R.string.toast_error_registration, LENGTH_SHORT).show();
                }
            }
        });

        /**
         * listener pre tlačidlo späť na prihlásenie
         */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(I);
            }
        });


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


@Override
protected void onRestart() {
    super.onRestart();
    producers.clear();
    Producer user1 = new Producer(getString(R.string.select_producer), "");
    producers.add(user1);


}
    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        databaseProducer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Producer track = postSnapshot.getValue(Producer.class);
                    track.setId(postSnapshot.getKey());
                    producers.add(track);
                    progressBar.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void openAddPruducer(View view) {

        Intent intent = new Intent(this, AddProducerActivity.class);
        startActivity(intent);

    }

}
