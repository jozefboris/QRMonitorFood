package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
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

import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.google.firebase.database.collection.LLRBNode;

public class ListAddActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<Movie> newList;
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    private Boolean typList ;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
         getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
      typList = false;
       mAdapter = new MoviesAdapter(movieList);
      recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

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
                Movie movie = newList.get(position);
                    Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                } else {
                    Movie movie = movieList.get(position);
                    Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
                }


                startActivity(intent);


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        prepareMovieData();



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
        Movie movie = new Movie("Mad Max: Fury Road", "Action & Adventure");
        movieList.add(movie);

        movie = new Movie("Inside Out", "Animation, Kids & Family");
        movieList.add(movie);

        movie = new Movie("Star Wars: Episode VII - The Force Awakens", "Action");
        movieList.add(movie);

        movie = new Movie("Shaun the Sheep", "Animation");
        movieList.add(movie);

        movie = new Movie("The Martian", "Science Fiction & Fantasy");
        movieList.add(movie);

        movie = new Movie("Mission: Impossible Rogue Nation", "Action");
        movieList.add(movie);

        movie = new Movie("Up", "Animation");
        movieList.add(movie);

        movie = new Movie("Star Trek", "Science Fiction");
        movieList.add(movie);

        movie = new Movie("The LEGO Movie", "Animation");
        movieList.add(movie);

        movie = new Movie("Iron Man", "Action & Adventure");
        movieList.add(movie);

        movie = new Movie("Aliens", "Science Fiction");
        movieList.add(movie);

        movie = new Movie("Chicken Run", "Animation");
        movieList.add(movie);

        movie = new Movie("Back to the Future", "Science Fiction");
        movieList.add(movie);

        movie = new Movie("Raiders of the Lost Ark", "Action & Adventure");
        movieList.add(movie);

        movie = new Movie("Goldfinger", "Action & Adventure");
        movieList.add(movie);

        movie = new Movie("Guardians of the Galaxy", "Science Fiction & Fantasy");
        movieList.add(movie);

        // notify adapter about data set changes
        // so that it will render the list with new data
        mAdapter.notifyDataSetChanged();
    }



}
