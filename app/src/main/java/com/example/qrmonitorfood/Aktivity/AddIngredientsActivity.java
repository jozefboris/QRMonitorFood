package com.example.qrmonitorfood.Aktivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

import static android.widget.Toast.makeText;

public class AddIngredientsActivity extends AppCompatActivity {

    Button buttonAdd;
    private EditText titleEditText;
    private EditText dateEditText;
    private  EditText date2EditText;
    private EditText countEditText;
    private  EditText descriptionEditText;
    private  EditText producerEditText;
    Producer producer;


    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    DatabaseReference databaseProduct;
    DatabaseReference databaseProducer;
    private EditText btn_date;
    private  EditText btn_date2;
    private MenuItem saveIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();


        btn_date = (EditText) findViewById(R.id.date_input);
        btn_date2 = (EditText) findViewById(R.id.date2_input);
        buttonAdd = (Button) findViewById(R.id.add);
        titleEditText = (EditText) findViewById(R.id.title);
        dateEditText = (EditText) findViewById(R.id.date_input);
        date2EditText = findViewById(R.id.date2_input);
        countEditText = findViewById(R.id.count);
        producerEditText = findViewById(R.id.producer);
        descriptionEditText = findViewById(R.id.decription);


        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDate();
            }
        });
        btn_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate2();
            }
        });



    }




    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        return true;
    }

    public void koko(ImageView item) {
        Intent intent = new Intent(this, UpdateProductActivity.class);

        startActivity(intent);

    }

    /**
     * onClick pre tlačidlo uložiť v menu
     * @param item
     */
    public void save(MenuItem item) {

        boolean everythingOK = true;

        if (titleEditText.getText().toString().trim().equals("")) {
            titleEditText.setError(getString(R.string.error_title));
            everythingOK = false;
        }

        if (dateEditText.getText().toString().trim().equals("")) {
            dateEditText.setError(getString(R.string.error_date_made));
            everythingOK = false;
        }

        if (date2EditText.getText().toString().trim().equals("")) {
            date2EditText.setError(getString(R.string.error_date_expiration));
            everythingOK = false;
        }

        if (countEditText.getText().toString().trim().equals("")) {
            countEditText.setError(getString(R.string.error_count));
            everythingOK = false;
        }

        if (everythingOK) {

            String id = databaseProduct.push().getKey();
            Product product = new Product(titleEditText.getText().toString().trim(),
                    dateEditText.getText().toString().trim(), date2EditText.getText().toString().trim(),
                    countEditText.getText().toString().trim(), producer.getId(),
                    descriptionEditText.getText().toString().trim(),null);
            databaseProduct.child(id).setValue(product);
            Toast.makeText(this, "Surovina pridana do systému", Toast.LENGTH_SHORT).show();


            Intent mIntent = getIntent();
            String previousActivity= mIntent.getStringExtra("FROM_ACTIVITY");



            if (previousActivity.equals("A")) {
                Intent intent = new Intent();
                intent.putExtra("editTextValue", id);
                setResult(RESULT_OK, intent);
                finish();

            } else if (previousActivity.equals("B")){

                final Intent intent = new Intent(this, AboutProductActivity.class);
                intent.putExtra("idCode",id );
                startActivity(intent);
                finish();
            }
        }
    }

    /**
     * aktualizuje datum
     */

    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * aktualizuje datum spotreby
     */
    private void updateDate2(){
        new DatePickerDialog(this, d2, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }



    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };


    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel2();
        }
    };

    /**
     * upravý editText datum výroby
     */
    private void updateTextLabel(){
        dateEditText.setText(formatDateTime.format(dateTime.getTime()));


    }

    /**
     * upravy editView datum spotreby
     */
    private void updateTextLabel2(){
        date2EditText.setText(formatDateTime.format(dateTime.getTime()));


    }

    protected void onStart() {
        super.onStart();

        databaseProducer.child(IntentConstants.databaseProducer).child(IntentConstants.idProducer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerEditText.setText(producer.getTitle());


                }

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }

}
