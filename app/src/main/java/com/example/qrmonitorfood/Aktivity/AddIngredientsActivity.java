package com.example.qrmonitorfood.Aktivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.Database.Zlozky;
import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.example.qrmonitorfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddIngredientsActivity extends AppCompatActivity {
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;

    EditText textIn;
    Button buttonAdd;
    Button buttonAdd2;

    private EditText titleEditText;
    private EditText dateEditText;
    private  EditText date2EditText;
    private EditText countEditText;
    private  EditText descriptionEditText;
    private  EditText producerEditText;
    Producer producer;


    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();

    DatabaseReference databaseComponents;
    DatabaseReference databaseProduct;
    DatabaseReference databaseProducer;
    private EditText btn_date;
    private  EditText btn_date2;
    private MenuItem saveIcon;
  //  private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        buttonAdd2 = (Button) findViewById(R.id.add2);
        textIn = findViewById(R.id.textin);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
     /*   spinner = findViewById(R.id.spinner);

        buttonAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.equals("Výber zložky")){
                prepareMovieData(item);
                item = "Výber zložky";
            } }

            ;

        });


        List<String> categories = new ArrayList<>();
        categories.add(0, "Výber zložky");
        categories.add("Bag");
        categories.add("Pen");
        categories.add("Pencil");
        categories.add("Eraser");
        categories.add("Bag");
        categories.add("Pen");
        categories.add("Pencil");
        categories.add("Eraser");

        //Style and populate the spinner
        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categories);

        //Dropdown layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (parent.getItemAtPosition(position).equals("Výber zložky")) {
                    Toast.makeText(parent.getContext(), "Vyber zlozku", LENGTH_SHORT).show();
                    item = "Výber zložky";

                } else {
                    //on selecting a spinner item
                    item = parent.getItemAtPosition(position).toString();


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // TODO Auto-generated method stub
            }
        });*/

        btn_date = (EditText) findViewById(R.id.date_input);
        btn_date2 = (EditText) findViewById(R.id.date2_input);
        buttonAdd = (Button) findViewById(R.id.add);
        buttonAdd2 = (Button) findViewById(R.id.add2);
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

/*
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!textIn.getText().toString().equals("")){
                prepareMovieData(textIn.getText().toString());
                textIn.setText(""); }
            }



        }); */


        // list
/*
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(movieList);
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
                Movie movie = movieList.get(position);
                movieList.remove(position);
                // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();

                mAdapter.notifyDataSetChanged();
            }



            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        final ImageView delete = (ImageView) recyclerView.findViewById(R.id.kokos);
        delete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                prepareMovieData("kokos");
            }
        });


        mAdapter.notifyDataSetChanged();


*/

    }


    /**
     * priprava dat
     * @param nazov nazov potraviny
     */
    private void prepareMovieData(String nazov) {
        Movie movie = new Movie(nazov,"");
        movieList.add(movie);
        mAdapter.notifyDataSetChanged();
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        //  setFavoriteIcon();
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
        if (producerEditText.getText().toString().trim().equals("")) {
            producerEditText.setError(getString(R.string.error_producer));
            everythingOK = false;
        }
        if (everythingOK) {

            String id = databaseProduct.push().getKey();
           List<String
                   > list = null;
            Product product = new Product(titleEditText.getText().toString().trim(),
                    dateEditText.getText().toString().trim(), date2EditText.getText().toString().trim(),
                    countEditText.getText().toString().trim(), producer.getId(),
                    descriptionEditText.getText().toString().trim(),list);
            databaseProduct.child(id).setValue(product);
            Toast.makeText(this, "Surovina pridana do systému", Toast.LENGTH_SHORT).show();

            for (int i = 0 ; i <movieList.size(); i++) {
                String id2 = databaseComponents.push().getKey();

                Zlozky zlozky = new Zlozky(movieList.get(i).getTitle(),id2,null,id);
                databaseComponents.child(id2).setValue(zlozky);
            }

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

  /*  private void updateTime(){
        new TimePickerDialog(this, t, dateTime.get(Calendar.HOUR_OF_DAY), dateTime.get(Calendar.MINUTE), true).show();
    }*/

  /**
     * ddialog pre datum
     */

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

    /**
     * dialog pre datum spotreby
     */
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
                    //   producer = snapshot.getValue(Producer.class);
                    //  for (DataSnapshot issue : snapshot.getChildren()) {

                    //    if (issue.exists()){
                    //    String id = issue.getKey();
                    //

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerEditText.setText(producer.getTitle());
                    //   }}


                }

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }

}
