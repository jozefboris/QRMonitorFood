package com.example.qrmonitorfood.Aktivity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.DefaultItemAnimator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.ListAdapter.RecyclerAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SearchListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    List<Product> newList;
    private MenuItem action;
    TextView emptyList;
    private List<Product> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private Boolean typList ;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference databaseProduct;
    ActionMode actionMode;
    private ActionMode.Callback callback;
    String idProducer;


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

        progressBar = findViewById(R.id.progress);
        emptyList=findViewById(R.id.emptyText);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        database = FirebaseDatabase.getInstance();
        databaseProduct = database.getReference("Products");
       progressBar = (ProgressBar) findViewById(R.id.progress);
        idProducer = getIntent().getStringExtra("idProducer");
        recyclerView.setLayoutManager(mLayoutManager);

        // adding inbuilt divider line
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        // adding custom divider line with padding 16dp
       //  recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
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
                if (typList){
                    Product movie = newList.get(position);
                    startActionMode();
                    mAdapter.onClick(view,position,movie.getProduktId() );

                    actionMode.setTitle("" + mAdapter.getSelectedItemCount());
                    if (mAdapter.getSelectedItemCount()==0){
                        cancelActionMode();
                    }

                } else {
                    startActionMode();
                    Product movie = movieList.get(position);
                    mAdapter.onClick(view,position, movie.getProduktId());
                     actionMode.setTitle("" + mAdapter.getSelectedItemCount());
                    if (mAdapter.getSelectedItemCount()==0){
                        cancelActionMode();
                    }
            }
            }
        }));




        // aktivace šipky zpět na toolbaru.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu_option, menu);
        action = menu.findItem(R.id.action);

       MenuItem searchItem = menu.findItem(R.id.action);
       SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.action){
        }

        if (id == android.R.id.home){
            mAdapter.clearSelections();
            // pokud uzivatel klikne na sipku zpet tak se ukonci soucasna aktivita.
            finish();
        }


        return super.onOptionsItemSelected(item);
    }


    /**
     * Nastartuje action mode.
     */
    private void startActionMode(){
        if (actionMode == null){
            setActionMode();
            actionMode = startSupportActionMode(callback);
           // actionMode.setTitle(""+mAdapter.getSelectedItemCount());
        }


    }


    /**
     * Nastartuje action mode.
     */
    private void setActionMode(){
        // inicializace callbacku pro action mode. Definuje akce co se maji stat.
        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_search, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                // click na tlačítko na toolbaru při aktivovaném action modu.
                if (item.getItemId() == R.id.cancel){
                    Toast.makeText(SearchListActivity.this, "Neco", Toast.LENGTH_LONG).show();
                    cancelActionMode();
                }

                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // voláno pokud uživatel klikne na action modu na tlačítko zpět.
                mAdapter.clearSelections();
                actionMode = null;
            }
        };


    }


    /**
     * Ukončuje action mode
     */
    private void cancelActionMode(){
        if (actionMode != null){
            actionMode.finish();
            actionMode = null;
        }
    }

    public void actionOnClick(MenuItem item) {
    }

    // action tlačidko pre vymazanie vybratých položiek
    public void actionDelete(MenuItem item) {

  for (int i = 0; i<mAdapter.getSelectedItemCount();i++){
      DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Products").child(mAdapter.getSelectedItems().get(i));

      dR.removeValue();
  }
        mAdapter.notifyDataSetChanged();
        mAdapter.clearSelections();
       cancelActionMode();
        Toast.makeText(SearchListActivity.this, R.string.delete_select_item , Toast.LENGTH_LONG).show();

    }

    /**
     * metody pre filtrovanie zoznamu
     * @param query
     * @return
     */
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

    /**
     * metoda po stusteni pre načítanie z databázy
     */

    @Override
    protected void onStart() {
        super.onStart();


        databaseProduct.orderByChild("producerId").equalTo(IntentConstants.idProducer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product track = postSnapshot.getValue(Product.class);
                    track.setProduktId(postSnapshot.getKey());
                    movieList.add(track);
                    progressBar.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }

        if (movieList.size()==0){
             emptyList.setVisibility(View.VISIBLE);
             progressBar.setVisibility(View.INVISIBLE);
             mAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
