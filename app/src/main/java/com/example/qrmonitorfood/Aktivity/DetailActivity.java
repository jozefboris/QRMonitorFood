package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
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
    DatabaseReference databaseProducer;
    private List<Product> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    DatabaseReference databaseProduct;
    Product product2 = new Product();
    String code;
    TextView titleText, dateText, date2text, countText, producerText, descriptionText,typeText;
    Producer producer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.datetext);
        date2text = findViewById(R.id.date2text);
        countText = findViewById(R.id.counttext);
        producerText = findViewById(R.id.producertext);
        descriptionText = findViewById(R.id.descriptiontext);
        typeText = findViewById(R.id.typetext);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        mAdapter = new RecyclerAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        emptyList = findViewById(R.id.emptyList);
        code = getIntent().getStringExtra(IntentConstants.idCode);

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
                Product movie = movieList.get(position);
                //  final Intent intent = new Intent(this, AboutProductActivity.class);
                intent.putExtra(IntentConstants.idCode, movie.getProduktId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
readProduct();
    }

    /**
     * zapis infornacii do edit text
     */

    private void writeData(){

        titleText.setText(product.getTitle());
        dateText.setText(product.getDateOfMade());
        date2text.setText(product.getDateExpiration());
        countText.setText(product.getCount());
        descriptionText.setText(product.getDecription());
        typeText.setText(getString(R.string.ingredients));
        readProducer(product.getProducerId());
        if (product.getProducts().size() != 0){
            for (int i =0; i<product.getProducts().size();i++) {
                readIngredients(product.getProducts().get(i));
            }
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }


    }

    /**
     * načitanie pre pridanie do listu
     * @param id
     */
    void readIngredients(final String id) {
        databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                product2 = snapshot.getValue(Product.class);
                if (snapshot.exists()) {
                    product2.setProduktId(id);
                    movieList.add(product2);
                    mAdapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError atabaseError) {

            }
        });
    }

    /**
     * priprava dat pre activitu
     */
    private void prepareMovieData() {
       writeData();
       mAdapter.notifyDataSetChanged();
    }

    /**
     * toast
     */
    void print(){

        Toast.makeText(this, R.string.database_not_found, Toast.LENGTH_SHORT).show();
    }

    public void readProducer(String id) {

        databaseProducer.child(IntentConstants.databaseProducer).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerText.setText(producer.getTitle());

                }
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }



    void readProduct(){
        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()){

                    prepareMovieData();}
                    else {
                    print();
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });


    }

}
