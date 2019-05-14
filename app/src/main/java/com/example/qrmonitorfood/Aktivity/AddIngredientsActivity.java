package com.example.qrmonitorfood.Aktivity;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.InternetConnection.NetworkChangeReceiver;
import com.example.qrmonitorfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

public class AddIngredientsActivity extends AppCompatActivity {

    Button buttonAdd;
    private TextInputLayout titleEditText;
    private TextInputLayout dateEditText;
    private  TextInputLayout dateExpidationEditText;
    private TextInputLayout batchEditText;
    private  TextInputLayout descriptionEditText;
    private  TextInputLayout producerEditText;
    Producer producer = new Producer();
    private BroadcastReceiver mNetworkReceiver;
    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    DatabaseReference databaseProduct;
    DatabaseReference databaseProducer;
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

        mNetworkReceiver = new NetworkChangeReceiver(AddIngredientsActivity.this);
        registerNetworkBroadcast();
        buttonAdd = findViewById(R.id.add);
        titleEditText =  findViewById(R.id.title);
        dateEditText =  findViewById(R.id.date_input);
        dateExpidationEditText = findViewById(R.id.dateExpiration_input);
        batchEditText = findViewById(R.id.batch);
        producerEditText = findViewById(R.id.producer);
        descriptionEditText = findViewById(R.id.decription);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });
        dateExpidationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedateExpidation();
            }
        });



    }

    /**
     * Tlačidlo späť v menu
     * @param item menu
     * @return menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Vytvorenie menu
     * @param menu menu
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        return true;
    }

    /**
     * K detekcii pripojenia internetu
     */
    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    /**
     * OnClick pre tlačidlo uložiť v menu, uloži surovinu do databazy
     * Ak je predchadzajuca aktivita A, čiže Update alebo addProductActivity otvori vrati id vytvorenej potraviny a ukonci aktivitu
     * Ak je predchadzajuca aktivita B, čiže mainActivity otvori aktivitu AboutProductActivity
     * @param item menu
     */

    public void save(MenuItem item) throws ParseException {

        if (!validateTitle() | !validateDateMade() | !validateDateExpiration() | !validateProducer() | !validateDateBatch()    ) {
            return;
        }

            String id = databaseProduct.push().getKey();
            Product product = new Product(titleEditText.getEditText().getText().toString().trim(),
                    dateEditText.getEditText().getText().toString().trim(), dateExpidationEditText.getEditText().getText().toString().trim(),
                    batchEditText.getEditText().getText().toString().trim(), producer.getId(),
                    descriptionEditText.getEditText().getText().toString().trim(),null);
                    databaseProduct.child(id).setValue(product);
                    Toast.makeText(this, R.string.successful_add_ingredients, Toast.LENGTH_SHORT).show();

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

    /**
     * Testuje spravnost vyplnenia nazvu potraviny
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateTitle() {
        String titleInput = titleEditText.getEditText().getText().toString().trim();

        if (titleInput.isEmpty()) {
            titleEditText.setError(getString(R.string.error_title));
            return false;
        } else {
            titleEditText.setError(null);
            return true;
        }
    }

    /**
     * Testuje spravnost vyplnenia datumu výroby
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateMade() {
        String dateOfMadeInput = dateEditText.getEditText().getText().toString().trim();

        if (dateOfMadeInput.isEmpty()) {
            dateEditText.setError(getString(R.string.error_date_made));
            return false;
        } else {
            dateEditText.setError(null);
            return true;
        }
    }


    /**
     * Testuje spravnost vyplnenia datumu spotreby
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateExpiration() {
        String dateOfExpirationInput = dateExpidationEditText.getEditText().getText().toString().trim();

        if (dateOfExpirationInput.isEmpty()) {
            dateExpidationEditText.setError(getString(R.string.error_date_expiration));
            return false;
        } else {
            dateExpidationEditText.setError(null);
            return true;
        }
    }


    /**
     * Testuje spravnost vyplnenia šarže
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateBatch() {
        String batchInput = batchEditText.getEditText().getText().toString().trim();

        if (batchInput.isEmpty()) {
            batchEditText.setError(getString(R.string.error_batch));
            return false;
        } else {
            batchEditText.setError(null);
            return true;
        }
    }

    /**
     * Testuje či je náčitaný výrobca
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateProducer() {
        String producerInput = producerEditText.getEditText().getText().toString().trim();

        if (producerInput.isEmpty()) {
            producerEditText.setError(getString(R.string.no_read_company));
            return false;
        } else {
            producerEditText.setError(null);
            return true;
        }
    }


    /**
     * Metoda aktualizuje datum vyroby
     */

    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Metoda aktualizuje datum spotreby
     */
    private void updatedateExpidation(){
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
     * Upravý textInputLayout datum výroby
     */
    private void updateTextLabel(){
        dateEditText.getEditText().setText(formatDateTime.format(dateTime.getTime()));


    }

    /**
     * Upravy textInputLayout datum spotreby
     */
    private void updateTextLabel2(){
        dateExpidationEditText.getEditText().setText(formatDateTime.format(dateTime.getTime()));


    }

    /**
     * Načita vyrobcu z databazy
     */

    protected void onStart() {
        super.onStart();

        databaseProducer.child(IntentConstants.databaseProducer).child(IntentConstants.idProducer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerEditText.getEditText().setText(producer.getTitle());


                }

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }


    /**
     * OnClick pre textInputLayout datum vyroby
     * @param view on click
     */
    public void openDate1(View view) {
        updateDate();

    }

    /**
     * OnClick pre textInputLayout datum spotreby
     *  @param view on click
     */
    public void opendateExpidation(View view) {
updatedateExpidation();

    }

}
