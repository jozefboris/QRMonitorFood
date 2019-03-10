package com.example.qrmonitorfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.support.v7.widget.DefaultItemAnimator;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.qrmonitorfood.Aktivity.AboutProductActivity;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
import com.example.qrmonitorfood.ListAdapter.RecyclerAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;

import static com.example.qrmonitorfood.R.drawable.ic_action_share;

public class SearchListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<Product> newList;
    private List<Product> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private Boolean typList ;
    private ImageView iconList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        typList = false;
        mAdapter = new RecyclerAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
       // iconList = (ImageView) findViewById(R.id.image_list);
        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

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
        final Intent intent = new Intent(this, AboutProductActivity.class);
        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (typList){
                    Product movie = newList.get(position);
                    intent.putExtra("idCode", movie.getProduktId());
                    // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Product movie = movieList.get(position);
                    intent.putExtra("idCode", movie.getProduktId());
                   // Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                }


                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        setFavoriteIcon();
        prepareMovieData();



    }
    private void setFavoriteIcon() {

     //iconList.setImageResource(Integer.parseInt(null));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.search_b);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        //    searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        //   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


        return true;
    }


/*
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mAdapter.getFilter().filter(newText);
            return false;
        }*/



    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String input = newText.toLowerCase();
        newList = new ArrayList<>();

        typList = true;



        for (int i = 0; i < movieList.size();i++) {

            if(movieList.get(i).getTitle().toLowerCase().contains(input))
            {
                newList.add(movieList.get(i));


            }
        }

        mAdapter.updateList(newList);
        return false;
    }






    private void prepareMovieData() {
        List<Product> p = null;
        Product product = new Product("Rohlik", "Banan","sdg",
                "15.3.2018","fs","gdf","xgx", p);
        movieList.add(product);


        Product product2 = new Product("Salat", "Jablko","sdg",
                "11.11.2019","fs","gdf","xgx", p);
        movieList.add(product2);


        Product product3 = new Product("Cesnak", "Rohlik","sdg",
                "17.4.2020","fs","gdf","xgx", p);
        movieList.add(product3);


        Product product4 = new Product("Sunka", "Chlieb","sdg",
                "24.7.2018","fs","gdf","xgx", p);
        movieList.add(product4);


        Product product5 = new Product("Jogurt", "Prorein","sdg",
                "31.3.2019","fs","gdf","xgx", p);
        movieList.add(product5);


        Product product6 = new Product("Kecup", "Pomazanka","sdg",
                "22.08.1019","fs","gdf","xgx", p);
        movieList.add(product6);

        Product product7 = new Product("Pomazanka", "Kecup","dfg",
                "22.07.2019","fs","gdf","xgx", p);
        movieList.add(product7);

        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }



}
