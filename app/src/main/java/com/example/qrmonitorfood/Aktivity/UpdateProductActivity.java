package com.example.qrmonitorfood.Aktivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.qrmonitorfood.Database.Product;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class UpdateProductActivity extends AppCompatActivity {

    Button buttonAdd;
    private EditText titleEditText;
    private EditText dateEditText;
    private  EditText date2EditText;
    private EditText countEditText;
    private  EditText descriptionEditText;
    private  EditText producerEditText;
    private List<Product> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    String code;
    Product product2 = new Product();
    Product product = new Product();
    DatabaseReference databaseProduct;
    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private EditText btn_date;
    private  EditText btn_date2;
    private MenuItem saveIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        code = getIntent().getStringExtra("idCode");
        databaseProduct = FirebaseDatabase.getInstance().getReference("Products");
        btn_date = (EditText) findViewById(R.id.date_input);
        btn_date2 = (EditText) findViewById(R.id.date2_input);
        final Activity activity = this;
        buttonAdd = (Button)findViewById(R.id.add);
        titleEditText = (EditText) findViewById(R.id.title);
        dateEditText = (EditText) findViewById(R.id.date_input);
        date2EditText = findViewById(R.id.date2_input);
        countEditText = findViewById(R.id.count);
        producerEditText = findViewById(R.id.producer);
        descriptionEditText = findViewById(R.id.decription);

// listener pre edit text datum výroby na vybehnutie dialogu nastavenia datumu
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateDate();
            }
        });

  // listener pre edit text datum spotrby na vybehnutie dialogu nastavenia datumu
        btn_date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate2();
            }
        });

        // listener pre tlačidlo naskenovat surovinu. Spusti kameru.

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(getString(R.string.scan));
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });


        // list recycle view

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new RecyclerAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerViewn
        // keep moviayoutManager = new LinearLayoutManae_list_row.xml width to `wrap_content`
        //        // RecyclerView.LayoutManager mLger(getApplicationCotext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
        // recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product movie = movieList.get(position);
                movieList.remove(position);
                // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();

                mAdapter.notifyDataSetChanged();
            }



            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        start();

    }

    /**
     * vypis dat a aktualizacia listu
     */

    private void prepareMovieData() {
       writeData();
        mAdapter.notifyDataSetChanged();
    }





    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        return true;
    }

    /**
     * onClick pre tlačidlo z menu uložiť
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
        if (producerEditText.getText().toString().trim().equals("")) {
            producerEditText.setError(getString(R.string.error_producer));
            everythingOK = false;
        }

        List<String> list = new ArrayList<>();

        if (everythingOK) {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Products").child(code);

            for (int i=0; i<movieList.size();i++){
                list.add(movieList.get(i).getProduktId());

            }


            Product product = new Product(code, titleEditText.getText().toString().trim(),
                    dateEditText.getText().toString().trim(), date2EditText.getText().toString().trim(),
                    countEditText.getText().toString().trim(), producerEditText.getText().toString().trim(),
                    descriptionEditText.getText().toString().trim(), list);
            dR.setValue(product);
            Toast.makeText(this, R.string.update_product, Toast.LENGTH_SHORT).show();

            final Intent intent = new Intent(this, AboutProductActivity.class);
            intent.putExtra("idCode",code );
            finish();
            startActivity(intent);

        }
    }

    /**
     * otvorí aktivitu pre pridanie suroviny
     * @param view
     */
    public void addIngre(View view){
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "A");
        startActivityForResult(i, 1);



    }

    /**
     * zapis dat do editText
     */

    private void writeData(){

        titleEditText.setText(product.getTitle());
        dateEditText.setText(product.getDateOfMade());
        date2EditText.setText(product.getDateExpiration());
        countEditText.setText(product.getCount());
        producerEditText.setText(product.getProducerId());
        descriptionEditText.setText(product.getDecription());

if (product.getProducts().size() != 0){
        for (int i =0; i<product.getProducts().size();i++) {

            add2(product.getProducts().get(i));
            }
         }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                add2(strEditText);

            }
        }
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                makeText(this, R.string.toast_cancel_scanning, LENGTH_LONG).show();
            }
            else {

                add2(result.getContents().substring(result.getContents().lastIndexOf("id=")+3).trim());

            }

        } }


    /**
     * nastavuje date dialog
     */
    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();


    }

    /**
     * nastavuje date dialog 2
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
     * aktualizuje pipis datum
     */
    private void updateTextLabel(){
        dateEditText.setText(formatDateTime.format(dateTime.getTime()));
    }

    /**
     * aktualizuje popis datum 2
     */
    private void updateTextLabel2(){
        date2EditText.setText(formatDateTime.format(dateTime.getTime()));
    }

    /**
     * vypis toastu
     */

    void print(){

        Toast.makeText(this, R.string.database_not_found, Toast.LENGTH_SHORT).show();
    }


    /**
     * pridanie do zoznamu
     * @param id pruduktu
     */

    void add2(final String id) {

            databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    product2 = snapshot.getValue(Product.class);
                    if (snapshot.exists()) {

                        product2.setProduktId(id);
                        movieList.add(product2);
                        mAdapter.notifyDataSetChanged();
                    } else {
                            print();


                    }
                }

                @Override
                public void onCancelled(DatabaseError atabaseError) {

                }
            });
        }


/*
    @Override
    protected void onStart() {
        super.onStart();

        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);

                product.setProduktId(code);
                             //  progressBar.setVisibility(View.GONE);
                //prints "Do you have data? You'll love Firebase."
                // product = new Product( snapshot.getValue(Product.class));
                prepareMovieData();
                print2();

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }
    */

    /**
     * začiatocne načítanie z databázy
     */
    public void  start(){
        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                product = snapshot.getValue(Product.class);
                product.setProduktId(code);
                prepareMovieData();

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });

    }
}
