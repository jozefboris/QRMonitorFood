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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.qrmonitorfood.ListAdapter.RecyclerAdapter;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.example.qrmonitorfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UpdateProductActivity extends AppCompatActivity {

    Button buttonAdd;
    private TextInputLayout titleInputLayout;
    private TextInputLayout dateInputLayout;
    private TextInputLayout dateExpidationInputLayout;
    private TextInputLayout batchInputLayout;
    private TextInputLayout descriptionInputLayout;
    private TextInputLayout producerInputLayout;
    private List<Product> elementList = new ArrayList<>();
    protected RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    String code;
    Product productIngredients = new Product();
    Product product = new Product();
    DatabaseReference databaseProduct;
    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private BroadcastReceiver mNetworkReceiver;
    private MenuItem saveIcon;
    DatabaseReference databaseProducer;
    Producer producer;
    Boolean addIngredients = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        code = getIntent().getStringExtra(IntentConstants.idCode);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        buttonAdd = findViewById(R.id.add);
        titleInputLayout = findViewById(R.id.title);
        dateInputLayout = findViewById(R.id.date_input);
        dateExpidationInputLayout = findViewById(R.id.dateExpiration_input);
        batchInputLayout = findViewById(R.id.batch);
        producerInputLayout = findViewById(R.id.producer);
        descriptionInputLayout = findViewById(R.id.decription);


        // Listener pre tlačidlo naskenovat surovinu. Spusti kameru.

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(UpdateProductActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(getString(R.string.scan));
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });


        // list recycle view
        mAdapter = new RecyclerAdapter(elementList, 1);
        recyclerView =  findViewById(R.id.recycler_view);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());


        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        mNetworkReceiver = new NetworkChangeReceiver(UpdateProductActivity.this);
        registerNetworkBroadcast();

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {


            @Override
            public void onClick(View view, int position) {
                elementList.remove(position);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Spusti metody pre vypis dat a aktualizacia listu zo zoznamom surovín
     */


    private void prepareElementData() {
        writeData();
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Tlačidlo spať v menu, po ktorom stlačeni sa ukonči aktivita
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
     * Metoda pre detekciu pripojenia
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
     * Metoda pre nastavenie menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        return true;
    }

    /**
     * OnClick pre ikonu z menu uložiť, ktorá upravi potravinu
     */

    public void save(MenuItem item) {

        if (!validateTitle() | !validateDateMade() | !validateDateExpiration() | !validateProducer() | !validateDateBatch()) {
            return;
        }

        List<String> list = new ArrayList<>();


        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct).child(code);

        for (int i = 0; i < elementList.size(); i++) {
            list.add(elementList.get(i).getProduktId());

        }


        Product product = null;
        try {
            product = new Product(titleInputLayout.getEditText().getText().toString().trim(),
                    dateInputLayout.getEditText().getText().toString().trim(), dateExpidationInputLayout.getEditText().getText().toString().trim(),
                    batchInputLayout.getEditText().getText().toString().trim(), producer.getId(),
                    descriptionInputLayout.getEditText().getText().toString().trim(), list);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dR.setValue(product);
        Toast.makeText(this, R.string.update_product, Toast.LENGTH_SHORT).show();
        finish();



    }

    /**
     * Pre vyplnenie datumu vyroby
     */
    public void openDate1(View view) {
        updateDate();

    }

    /**
     * Pre vyplnenie datumu spotreby
     */
    public void opendateExpidation(View view) {
        updatedateExpidation();

    }

    /**
     * Otvorí aktivitu pre pridanie suroviny
     * posle extra data s hodnotou A, aby nasledujuca aktivita poslala tejto aktivite id pridanej suroviny
     */
    public void addIngre(View view){
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "A");
        startActivityForResult(i, 1);



    }

    /**
     * Testuje spravnost vyplnenia nazvu potraviny
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateTitle() {
        String titleInput = titleInputLayout.getEditText().getText().toString().trim();

        if (titleInput.isEmpty()) {
            titleInputLayout.setError(getString(R.string.error_title));
            return false;
        } else {
            titleInputLayout.setError(null);
            return true;
        }
    }

    /**
     * Testuje spravnost vyplnenia datumu výroby
     * @return true/false ak nieje prazdne pole vrati true
     */
    private boolean validateDateMade() {
        String dateOfMadeInput = dateInputLayout.getEditText().getText().toString().trim();

        if (dateOfMadeInput.isEmpty()) {
            dateInputLayout.setError(getString(R.string.error_date_made));
            return false;
        } else {
            dateInputLayout.setError(null);
            return true;
        }
    }


    /**
     * Testuje spravnost vyplnenia datumu spotreby
     * @return true/false ak niejE prazdne pole vrati true
     */
    private boolean validateDateExpiration() {
        String dateOfExpirationInput = dateExpidationInputLayout.getEditText().getText().toString().trim();

        if (dateOfExpirationInput.isEmpty()) {
            dateExpidationInputLayout.setError(getString(R.string.error_date_expiration));
            return false;
        } else {
            dateExpidationInputLayout.setError(null);
            return true;
        }
    }


    /**
     * Testuje spravnost vyplnenia šarže
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateBatch() {
        String batchInput = batchInputLayout.getEditText().getText().toString().trim();

        if (batchInput.isEmpty()) {
            batchInputLayout.setError(getString(R.string.error_batch));
            return false;
        } else {
            batchInputLayout.setError(null);
            return true;
        }
    }

    /**
     * Testuje či je náčitaný výrobca
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateProducer() {
        String producerInput = producerInputLayout.getEditText().getText().toString().trim();

        if (producerInput.isEmpty()) {
            producerInputLayout.setError(getString(R.string.no_read_company));
            return false;
        } else {
            producerInputLayout.setError(null);
            return true;
        }
    }

    /**
     * Metoda pre zapis dat do InputTextLayout
     */

    private void writeData(){

        titleInputLayout.getEditText().setText(product.getTitle());
        dateInputLayout.getEditText().setText(product.getDateOfMade());
        dateExpidationInputLayout.getEditText().setText(product.getDateExpiration());
        batchInputLayout.getEditText().setText(product.getBatch());
        descriptionInputLayout.getEditText().setText(product.getDecription());
        readProducer(product.getProducerId());

        if (!addIngredients){
            if (product.getProducts().size() != 0){
        for (int i =0; i<product.getProducts().size();i++) {
            addIngredients = true;
            readProducts(product.getProducts().get(i));
              }
           }
        }
    }

    /**
     * Metoda vloží id suroviny do zoznamu zo skenovania alebo z aktivity Add ingredients alebo z aktivity SelectedListActivity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                readProducts(strEditText);

            }
        }   if (requestCode == 2) {
            if(resultCode == RESULT_OK) {


                ArrayList array =data.getStringArrayListExtra("strings");

                for(int i = 0 ;i<array.size();i++) {
                    readProducts(array.get(i).toString());
                }


            } }
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
            }
            else {

                readProducts(result.getContents().substring(result.getContents().lastIndexOf("id=")+3).trim());

            }

        } }


    /**
     * Nastavuje date dialog pre datum výroby
     */
    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();


    }

    /**
     * Nastavuje date dialog pre datum spotreby
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
     * Aktualizuje datum výroby
     */
    private void updateTextLabel(){
        dateInputLayout.getEditText().setText(formatDateTime.format(dateTime.getTime()));
    }

    /**
     * Aktualizuje  datum spotreby
     */
    private void updateTextLabel2(){
        dateExpidationInputLayout.getEditText().setText(formatDateTime.format(dateTime.getTime()));
    }



    /**
     * Metoda pre pridanie surovin do zoznamu
     * @param id pruduktu
     */

    void readProducts(final String id) {

            databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    productIngredients = snapshot.getValue(Product.class);
                    if (snapshot.exists()) {

                        productIngredients.setProduktId(id);
                        elementList.add(productIngredients);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.delete_product, Toast.LENGTH_LONG).show();
                        finish();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    /**
     * OnClick pre tlačidlo pridať zo zoznamu, otvorí aktivitu SelectedListActivity
     * @param view
     */
    public void addFromList(View view){
        Intent i = new Intent(this, SelectedListActivity.class);
        startActivityForResult(i, 2);

    }

    /**
     * Metoda pre načitanie vyrobcu z databazy
     * @param id vyrobcu
     */
    public void readProducer(String id) {

        databaseProducer.child(IntentConstants.databaseProducer).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerInputLayout.getEditText().setText(producer.getTitle());
                    //   }}


                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     *  Načítanie potraviny z databázy
     */
    @Override
    protected void onStart() {
        super.onStart();

        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(snapshot.exists()) {
                     product = snapshot.getValue(Product.class);
                     product.setProduktId(code);
                     prepareElementData();

                } else {
                    Toast.makeText(UpdateProductActivity.this, R.string.database_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
}
