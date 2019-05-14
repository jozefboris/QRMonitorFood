package com.example.qrmonitorfood.Aktivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SelectedListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    List<Product> newList;
    private MenuItem action;
    TextView emptyList;
    private List<Product> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private Boolean typList ;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference databaseProduct;
    DatabaseReference databaseProducer;
    ActionMode actionMode;
    private ActionMode.Callback callback;
    Producer producer = new Producer();
    InternetConnectionSnackbar connectionSnackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        typList = false;
        mAdapter = new RecyclerAdapter(elementList,2);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        connectionSnackbar = new InternetConnectionSnackbar(SelectedListActivity.this,recyclerView);

        progressBar = findViewById(R.id.progress);
        emptyList=findViewById(R.id.emptyText);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        database = FirebaseDatabase.getInstance();
        databaseProduct = database.getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        progressBar = findViewById(R.id.progress);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {


            @Override
            public void onClick(View view, int position) {

                if (typList){
                    Product element = newList.get(position);
                    startActionMode();
                    mAdapter.onClick(position,element.getProduktId() );

                    actionMode.setTitle("" + mAdapter.getSelectedCountItem());
                    if (mAdapter.getSelectedCountItem()==0){
                        cancelActionMode();
                    }
                } else {
                    startActionMode();
                    Product element = elementList.get(position);
                    mAdapter.onClick(position, element.getProduktId());
                    actionMode.setTitle("" + mAdapter.getSelectedCountItem());
                    if (mAdapter.getSelectedCountItem()==0){
                        cancelActionMode();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    /**
     * Nastaví menu
     * @param menu menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu_option, menu);
        action = menu.findItem(R.id.action);

        MenuItem searchItem = menu.findItem(R.id.action);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    /**
     * Pokudpouzivatel klikne na sipku späť tak sa ukonci soucasna aktivita.
     * @param item položka menu
     * @return item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.action){
        }

        if (id == android.R.id.home){
            mAdapter.clearSelections();
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Onclick tlačila uložiť v action menu
     * @param item menu
     */
    public void save(MenuItem item) {

        Intent intent = new Intent();
        intent.putStringArrayListExtra("strings", (ArrayList<String>) mAdapter.getSelectedItems());
        setResult(RESULT_OK, intent);
        finish();


    }

    /**
     * Nastartuje action mode.
     */
    private void startActionMode(){
        if (actionMode == null){
            setActionMode();
            actionMode = startSupportActionMode(callback);
        }


    }

    /**
     * Načita suroviny do listu
     */
    public void readProducers(){

        for(int i =0; i<elementList.size();i++){
            databaseProducer.child(elementList.get(i).getProducerId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        producer = snapshot.getValue(Producer.class);
                    }
                }
                @Override
                public void onCancelled(DatabaseError atabaseError) {

                    finish();
                }
            });
        }
        mAdapter.notifyDataSetChanged();
    }


    /**
     * Nastartuje action mode.
     */
    private void setActionMode(){
        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.menu_selected, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Click na tlačítko na toolbaru pri aktivovanom action mode.
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                if (item.getItemId() == R.id.cancel){
                    cancelActionMode();
                }
                return false;
            }

            /**
             * Voláne ak použivatel klikne na action modu na tlačítko späť.
             * @param mode action mode
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {

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


    /**
     * Metoda pre filtrovanie zoznamu

     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    /**
     * Metoda pre filtrovanie zoznamu
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        String input = newText.toLowerCase();
        newList = new ArrayList<>();
        typList = true;
        for (int i = 0; i < elementList.size();i++) {

            if(elementList.get(i).getTitle().toLowerCase().contains(input))
            {
                newList.add(elementList.get(i));
            }
        }

        mAdapter.updateList(newList);
        return false;
    }

    /**
     * Metoda po spusteni  načíta zoznam potravín s id vyrobcom prihlaseneho použivatela
     */

    @Override
    protected void onResume() {
        super.onResume();
connectionSnackbar.checkConnection();

        databaseProduct.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                elementList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product track = postSnapshot.getValue(Product.class);
                    track.setProduktId(postSnapshot.getKey());
                    elementList.add(track);
                    progressBar.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                }

                if (elementList.size()==0){
                    emptyList.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    mAdapter.notifyDataSetChanged();

                }
               readProducers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Dialogove okno pre sortovanie
     * @param item položka z menu
     */


    public void sort(MenuItem item) {

        final AlertDialog.Builder mySortAlertDialog = new AlertDialog.Builder(this);
        mySortAlertDialog.setTitle(getString(R.string.sort_by));
        String[] r = {getString(R.string.sort_by_title) + String.format("%33s", "A - Z") ,getString(R.string.sort_by_title) + String.format("%33s", "Z - A"), getString(R.string.sort_by_batch) + String.format("%34s", "A - Z"),getString(R.string.sort_by_batch) + String.format("%34s", "Z - A"), getString(R.string.sort_by_date_made) + String.format("%17s", "A - Z"), getString(R.string.sort_by_date_made) + String.format("%17s", "Z - A"),getString(R.string.sort_by_date_expiration) + String.format("%13s", "A - Z"),getString(R.string.sort_by_date_expiration) + String.format("%13s", "Z - A")};
        mySortAlertDialog.setSingleChoiceItems(r,0 , null);

        mySortAlertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==0) {
                    mAdapter.sortByTitleAsc();
                    mAdapter.notifyDataSetChanged();

                } else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==1) {
                    mAdapter.sortByTitleDesc();
                    mAdapter.notifyDataSetChanged();
                } else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==2) {
                    mAdapter.sortByBatchAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==3) {
                    mAdapter.sortByBatchDesc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==4) {
                    mAdapter.sortByDateOfMadeAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==5) {
                    mAdapter.sortByDateOfMadeDesc();
                    mAdapter.notifyDataSetChanged();;
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==6) {
                    mAdapter.sortByDateOfExpidationAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==7) {
                    mAdapter.sortByDateOfExpidationDesc();
                    mAdapter.notifyDataSetChanged();
                }

            }
        });

        mySortAlertDialog.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        mySortAlertDialog.create().show();
    }
}
