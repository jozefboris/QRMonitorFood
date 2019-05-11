package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
import com.example.qrmonitorfood.ListAdapter.RecyclerAdapter;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.example.qrmonitorfood.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    ProgressBar progressBar;
    TextView emptyList;
    Product product = new Product();
    Product productIngredients = new Product();
    DatabaseReference databaseProducer;
    DatabaseReference databaseProduct;
    private List<Product> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private String code;
    TextView titleText, dateText, dateExpidationtext, batchText, producerText, descriptionText,typeText;
    Producer producer;
    InternetConnectionSnackbar connectionSnackbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView =  findViewById(R.id.recycler_view);
        progressBar =  findViewById(R.id.progressBar);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.datetext);
        dateExpidationtext = findViewById(R.id.dateExpirationtext);
        batchText = findViewById(R.id.batchtext);
        producerText = findViewById(R.id.producertext);
        descriptionText = findViewById(R.id.descriptiontext);
        typeText = findViewById(R.id.typetext);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        mAdapter = new RecyclerAdapter(elementList,4);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        emptyList = findViewById(R.id.emptyList);
        code = getIntent().getStringExtra(IntentConstants.idCode);
        connectionSnackbar = new InternetConnectionSnackbar(DetailActivity.this,titleText);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        final Intent intent = new Intent(this, DetailActivity.class);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product element = elementList.get(position);
                intent.putExtra(IntentConstants.idCode, element.getProduktId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    /**
     * Metoda pre tlačidla spät
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
     * Zapis infornacii do text view
     */
    private void writeData(){

        titleText.setText(product.getTitle());
        dateText.setText(product.getDateOfMade());
        dateExpidationtext.setText(product.getDateExpiration());
        batchText.setText(product.getBatch());
        descriptionText.setText(product.getDecription());
        typeText.setText(getString(R.string.ingredients));
        if (product.getProducts().size() != 0){
            for (int i =0; i<product.getProducts().size();i++) {
                readIngredients(product.getProducts().get(i));
            }
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }


    }

    /**
     * Načitanie pre pridanie suroviny do listu
     * @param id produktu
     */
    void readIngredients(final String id) {
        databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                productIngredients = snapshot.getValue(Product.class);
                if (snapshot.exists()) {
                    productIngredients.setProduktId(id);
                    elementList.add(productIngredients);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError atabaseError) {

            }
        });
    }

    /**
     * Vloženie dat do TextInputLayout
     */
    private void prepareElementData() {
        readProducer(product.getProducerId());
       writeData();
       mAdapter.notifyDataSetChanged();
    }




    /**
     * Metoda pre načitanie vyrobcu
     * @param id vyrobcu
     */
    public void readProducer(String id) {

        databaseProducer.child(IntentConstants.databaseProducer).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerText.setText(producer.getTitle());

            } else {
                Toast.makeText(DetailActivity.this, R.string.error_read_producer, Toast.LENGTH_SHORT).show();
            }
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }

    /**
     * Metoda pre nacitanie produktu z databazy
     */

    protected void onStart() {
        super.onStart();
        elementList.clear();
        connectionSnackbar.checkConnection();
        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()){
                    prepareElementData();}
                    else {
                    Toast.makeText(DetailActivity.this, R.string.database_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }

            }


            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });


    }

}
