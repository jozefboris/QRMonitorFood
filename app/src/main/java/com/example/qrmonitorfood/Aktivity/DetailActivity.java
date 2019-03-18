package com.example.qrmonitorfood.Aktivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
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

  //  TextView textView;
    Product product = new Product();

    private List<Product> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    DatabaseReference databaseProduct;
    String code;
    TextView titleText, dateText, date2text, countText, producerText, descriptionText,typeText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
      //  textView=(TextView)findViewById(R.id.textView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        databaseProduct = FirebaseDatabase.getInstance().getReference("product");
        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.datetext);
        date2text = findViewById(R.id.date2text);
        countText = findViewById(R.id.counttext);
        producerText = findViewById(R.id.producertext);
        descriptionText = findViewById(R.id.descriptiontext);
        typeText = findViewById(R.id.typetext);

        mAdapter = new RecyclerAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        code = getIntent().getStringExtra("idCode");
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
                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

      //  prepareMovieData();
    }

    private void writeData(){

        titleText.setText(product.getTitle());
        dateText.setText(product.getDateOfMade());
        date2text.setText(product.getDateExpiration());
        countText.setText(product.getCount());
        producerText.setText(product.getProducer());
        descriptionText.setText(product.getDecription());
        typeText.setText("surovina");



    }

    private void prepareMovieData() {

       writeData();
        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);

             //   product.setProduktId(code);

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
