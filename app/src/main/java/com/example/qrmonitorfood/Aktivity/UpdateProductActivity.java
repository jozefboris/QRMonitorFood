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

import com.example.qrmonitorfood.Aktivity.AboutProductActivity;
import com.example.qrmonitorfood.Aktivity.AddIngredientsActivity;
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
    private EditText btn_time;

    private MenuItem saveIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        code = getIntent().getStringExtra("idCode");
        // app.initializeApp(this);
        databaseProduct = FirebaseDatabase.getInstance().getReference("product");
        btn_date = (EditText) findViewById(R.id.date_input);
        btn_date2 = (EditText) findViewById(R.id.date2_input);
        final Activity activity = this;



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


        buttonAdd = (Button)findViewById(R.id.add);
        titleEditText = (EditText) findViewById(R.id.title);
        dateEditText = (EditText) findViewById(R.id.date_input);
        date2EditText = findViewById(R.id.date2_input);
        countEditText = findViewById(R.id.count);
        producerEditText = findViewById(R.id.producer);
        descriptionEditText = findViewById(R.id.decription);

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


        // list

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new RecyclerAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // horizontal RecyclerView
        // keep movie_list_row.xml width to `wrap_content`
        // RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

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

/*
        final ImageView delete = (ImageView) recyclerView.findViewById(R.id.kokos);
        delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                prepareMovieData("kokos");
            }
        });
*/

        //mAdapter.notifyDataSetChanged();

//prepareMovieData();



    }



    private void prepareMovieData() {
       writeData();


        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }





    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        //  setFavoriteIcon();
        return true;
    }

    public void save(MenuItem item) {
        boolean everythingOK = true;

        if (titleEditText.getText().toString().trim().equals("")) {  // stringy nie == ale equals
            titleEditText.setError(getString(R.string.error_title));
            everythingOK = false;
        }

        if (dateEditText.getText().toString().trim().equals("")) {  // stringy nie == ale equals
            dateEditText.setError(getString(R.string.error_date_made));
            everythingOK = false;
        }

        if (date2EditText.getText().toString().trim().equals("")) {  // stringy nie == ale equals
            date2EditText.setError(getString(R.string.error_date_expiration));
            everythingOK = false;
        }

        if (countEditText.getText().toString().trim().equals("")) {  // stringy nie == ale equals
            countEditText.setError(getString(R.string.error_count));
            everythingOK = false;
        }
        if (producerEditText.getText().toString().trim().equals("")) {  // stringy nie == ale equals
            producerEditText.setError(getString(R.string.error_producer));
            everythingOK = false;
        }

        List<String> list = new ArrayList<>();
        //List<Zlozky> list2 = null;

        if (everythingOK) {
            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(code);

            for (int i=0; i<movieList.size();i++){
                list.add(movieList.get(i).getProduktId());

            }


            Product product = new Product(code, titleEditText.getText().toString().trim(),
                    dateEditText.getText().toString().trim(), date2EditText.getText().toString().trim(),
                    countEditText.getText().toString().trim(), producerEditText.getText().toString().trim(),
                    descriptionEditText.getText().toString().trim(), list);
            dR.setValue(product);
            //databaseProduct.child(id).setValue(product);
            Toast.makeText(this, "Produkt update", Toast.LENGTH_SHORT).show();

            final Intent intent = new Intent(this, AboutProductActivity.class);
            intent.putExtra("idCode",code );
            // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();

            startActivity(intent);
        }
    }


    public void addIngre(View view){
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "A");
        startActivityForResult(i, 1);



    }

    private void writeData(){

        titleEditText.setText(product.getTitle());
        dateEditText.setText(product.getDateOfMade());
        date2EditText.setText(product.getDateExpiration());
        countEditText.setText(product.getCount());
        producerEditText.setText(product.getProducer());
        descriptionEditText.setText(product.getDecription());

  //      List<String> list = new ArrayList<>();
//        list.addAll(product.getProducts());
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
                //code = strEditText;
                add2(strEditText); // onStart();
               // makeText(this, strEditText, LENGTH_LONG).show();
            }
        }
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                makeText(this, "You cancelled the scanning", LENGTH_LONG).show();
            }
            else {

               /* MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                LayoutInflater layoutInflater =
                        (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View addView = layoutInflater.inflate(R.layout.row, null);
*/
  //              makeText(this, result.getContents(), LENGTH_LONG).show();
                //code= result.getContents();
    //            makeText(this, result.getContents(), LENGTH_LONG).show();
                //mAdapter.notifyDataSetChanged();
                add2(result.getContents());// onStart();

                //  prepareMovieData(result.getContents());
                final View.OnClickListener thisListener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                    }
                };

            }

        } }





    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
     //   code = getIntent().getStringExtra("data");


    }

    private void updateDate2(){
        new DatePickerDialog(this, d2, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

  /*  private void updateTime(){
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }*/

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };

   /* TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateTime.set(Calendar.MINUTE, minute);
            updateTextLabel();
        }
    };*/

    DatePickerDialog.OnDateSetListener d2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel2();
        }
    };

    private void updateTextLabel(){
        dateEditText.setText(formatDateTime.format(dateTime.getTime()));


    }
    private void updateTextLabel2(){
        date2EditText.setText(formatDateTime.format(dateTime.getTime()));


    }


    void add2(final String id) {




            databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    product2 = snapshot.getValue(Product.class);
                    if (snapshot.exists()) {

                        product2.setProduktId(id);

                        movieList.add(product2);

                        // progressBar.setVisibility(View.GONE);
                        //prints "Do you have data? You'll love Firebase."
                        // product = new Product( snapshot.getValue(Product.class));
                        mAdapter.notifyDataSetChanged();
                    } else {
                        //     print();
                        // makeText(this, "Produkt pridaný  do systému", LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(DatabaseError atabaseError) {

                }
            });
        }


 /*   void add() {
        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                product = snapshot.getValue(Product.class);
                if (snapshot.exists()) {
                    product.setProduktId(code);

                    movieList.add(product);

                    // progressBar.setVisibility(View.GONE);
                    //prints "Do you have data? You'll love Firebase."
                    // product = new Product( snapshot.getValue(Product.class));
                    mAdapter.notifyDataSetChanged();
                } else {
                    //   print();
                    // makeText(this, "Produkt pridaný  do systému", LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError atabaseError) {

            }
        });

    }*/


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
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }





}
