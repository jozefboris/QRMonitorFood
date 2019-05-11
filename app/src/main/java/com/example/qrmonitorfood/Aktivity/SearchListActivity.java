package com.example.qrmonitorfood.Aktivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
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
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
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
    private List<Product> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private Boolean typList ;
    ProgressBar progressBar;
    FirebaseDatabase database;
    DatabaseReference databaseProduct;
    ActionMode actionMode;
    private ActionMode.Callback callback;
    InternetConnectionSnackbar connectionSnackbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        typList = false;
        mAdapter = new RecyclerAdapter(elementList,0);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        connectionSnackbar = new InternetConnectionSnackbar(SearchListActivity.this,recyclerView);
        progressBar = findViewById(R.id.progress);
        emptyList=findViewById(R.id.emptyText);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        database = FirebaseDatabase.getInstance();
        databaseProduct = database.getReference(IntentConstants.databaseProduct);
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
                    Intent intent = new Intent(SearchListActivity.this, AboutProductActivity.class);
                    Product element = mAdapter.getNew().get(position);
                    intent.putExtra(IntentConstants.idCode, element.getProduktId());
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(SearchListActivity.this, AboutProductActivity.class);
                    Product element = elementList.get(position);
                    intent.putExtra(IntentConstants.idCode, element.getProduktId());
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
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
        }));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    /**
     * vytvoti menu
     * @param menu
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
     * tlačidlo späť
     * @param item menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            mAdapter.clearSelections();
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
        }
    }


    /**
     * Nastartuje action mode.
     */
    private void setActionMode(){
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
                    cancelActionMode();
                }

                return false;
            }

            /**
             * voláno pokud uživatel klikne na action modu na tlačítko zpět.
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
     * action tlačidko pre vymazanie vybratých položiek
      */

    public void actionDelete(MenuItem item) {

        if(connectionSnackbar.isNetworkAvailable()) {
    for (int i = 0; i < mAdapter.getSelectedCountItem(); i++) {
        DatabaseReference dR = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct).child(mAdapter.getSelectedItems().get(i));

        dR.removeValue();
    }
         mAdapter.notifyDataSetChanged();
         mAdapter.clearSelections();
         cancelActionMode();
         Toast.makeText(SearchListActivity.this, R.string.delete_select_item, Toast.LENGTH_LONG).show();

       } else {
            Toast.makeText(SearchListActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * metoda pre filtrovanie zoznamu

     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * metoda pre filtrovanie zoznamu

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
     * metoda po stusteni pre načítanie zoznamu potravín s id vyrobcom prihlaseneho uživatela
     */

    @Override
    protected void onResume() {
        super.onResume();
        connectionSnackbar.checkConnection();

        databaseProduct.orderByChild("producerId").equalTo(IntentConstants.idProducer).addValueEventListener(new ValueEventListener() {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * dialogove okno pre sortovanie
     * @param item položka z menu
     */
    public void sort(MenuItem item) {

        final AlertDialog.Builder mySortAlertDialog = new AlertDialog.Builder(this);
        mySortAlertDialog.setTitle("Zoradiť podla?");
        String[] r = {"Názvu A-Z ","Názvu Z-A ", "Dátumu výroby A-Z   ","Dátumu výroby Z-A","Dátumu spotreby A-Z ","Dátumu spotreby Z-A ","Šarše zostupne A-Z ","Šarše zostupne Z-A ",};
        mySortAlertDialog.setSingleChoiceItems(r,0 , null);

        mySortAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                    mAdapter.sortByDateOfMadeAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==3) {
                    mAdapter.sortByDateOfMadeDesc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==4) {
                    mAdapter.sortByDateOfExpidationAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==5) {
                    mAdapter.sortByDateOfExpidationDesc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==6) {
                    mAdapter.sortByBatchAsc();
                    mAdapter.notifyDataSetChanged();
                }else if (((AlertDialog)dialog).getListView().getCheckedItemPosition() ==7) {
                    mAdapter.sortByBatchDesc();
                    mAdapter.notifyDataSetChanged();
                }

            }
        });

        mySortAlertDialog.setNegativeButton("Zrušiť", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        mySortAlertDialog.create().show();
    }
}
