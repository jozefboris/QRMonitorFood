package com.example.qrmonitorfood.Aktivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
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
import android.widget.Toast;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.InternetConnection.NetworkChangeReceiver;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import static android.widget.Toast.*;

public class AddProductActivity extends AppCompatActivity {

    Button buttonAdd;
    private TextInputLayout titleInputLayout;
    private TextInputLayout dateInputLayout;
    private  TextInputLayout dateExpidationInputLayout;
    private TextInputLayout batchInputLayout;
    private  TextInputLayout descriptionInputLayout;
    private  TextInputLayout producerInputLayout;
    private List<Product> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    Producer producer;
    DatabaseReference databaseProducer;
    Product product = new Product();
    DatabaseReference databaseProduct;
    DateFormat formatDateTime = DateFormat.getDateInstance();
    Calendar dateTime = Calendar.getInstance();
    private MenuItem saveIcon;
    private BroadcastReceiver mNetworkReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        buttonAdd = (Button)findViewById(R.id.add);
        titleInputLayout = findViewById(R.id.title);
        dateInputLayout =  findViewById(R.id.date_input);
        dateExpidationInputLayout = findViewById(R.id.dateExpiration_input);
        batchInputLayout = findViewById(R.id.batch);
        producerInputLayout = findViewById(R.id.producer);
        descriptionInputLayout = findViewById(R.id.decription);
        final Activity activity = this;




/**
 * listener pre tlačidlo naskenovat potravinu
 */
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
        mAdapter = new RecyclerAdapter(elementList,1);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mNetworkReceiver = new NetworkChangeReceiver(AddProductActivity.this);
        registerNetworkBroadcast();

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Product element = elementList.get(position);
                elementList.remove(position);
                mAdapter.notifyDataSetChanged();
            }



            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * k detekcii pripojenia internetu
     */
    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    /**
     * metoda vezme id suroviny do zoznamu zo skenovania alebo z aktivity Add ingredients
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String strEditText = data.getStringExtra("editTextValue");
                readIndredients(strEditText);
            }
         }   if (requestCode == 2) {
            if(resultCode == RESULT_OK) {

                ArrayList array =data.getStringArrayListExtra("strings");

                for(int i = 0 ;i<array.size();i++) {
                    readIndredients(array.get(i).toString());
                }
            }
        }

        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
            }
            else {


                readIndredients(result.getContents().substring(result.getContents().lastIndexOf("id=")+3).trim());

                final View.OnClickListener thisListener = new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                     }
                };

            }

        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_product, menu);
        saveIcon = menu.findItem(R.id.action_save);
        return true;
    }

    /**
     * listener na tlacidlo uložit v menu
     * uloži produkt do databazy
     */


    public void save(MenuItem item) throws ParseException {
        if (!validateTitle() | !validateDateMade() | !validateDateExpiration() | !validateProducer() | !validateDateBatch()    ) {
            return;
        }


        List<String> list = new ArrayList<>();

           String id = databaseProduct.push().getKey();

           for (int i=0; i<elementList.size();i++){
               list.add(elementList.get(i).getProduktId());

           }
             Product  product = new Product( titleInputLayout.getEditText().getText().toString().trim(),
                       dateInputLayout.getEditText().getText().toString().trim(), dateExpidationInputLayout.getEditText().getText().toString().trim(),
                       batchInputLayout.getEditText().getText().toString().trim(), producer.getId(),
                       descriptionInputLayout.getEditText().getText().toString().trim(), list);

           databaseProduct.child(id).setValue(product);
                   makeText(this, getString(R.string.add_product_sucessful), LENGTH_SHORT).show();

           final Intent intent = new Intent(this, AboutProductActivity.class);
           intent.putExtra(IntentConstants.idCode,id );
           finish();
           startActivity(intent);


    }

    /**
     * testuje spravnost vyplnenia nazvu potraviny
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateTitle() {
        String titleInput = titleInputLayout.getEditText().getText().toString().trim();

        if (titleInput.isEmpty()) {
            titleInputLayout.setError(getString(R.string.error_title));
            return false;
        } else {
            titleInputLayout.setError(null);
            return true;
        }
    }

    /**
     * testuje spravnost vyplnenia datumu výroby
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateMade() {
        String dateOfMadeInput = dateInputLayout.getEditText().getText().toString().trim();

        if (dateOfMadeInput.isEmpty()) {
            dateInputLayout.setError(getString(R.string.error_date_made));
            return false;
        } else {
            dateInputLayout.setError(null);
            return true;
        }
    }


    /**
     * testuje spravnost vyplnenia datumu spotreby
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateExpiration() {
        String dateOfExpirationInput = dateExpidationInputLayout.getEditText().getText().toString().trim();

        if (dateOfExpirationInput.isEmpty()) {
            dateExpidationInputLayout.setError(getString(R.string.error_date_expiration));
            return false;
        } else {
            dateExpidationInputLayout.setError(null);
            return true;
        }
    }


    /**
     * testuje spravnost vyplnenia šarže
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateDateBatch() {
        String batchInput = batchInputLayout.getEditText().getText().toString().trim();

        if (batchInput.isEmpty()) {
            batchInputLayout.setError(getString(R.string.error_batch));
            return false;
        } else {
            batchInputLayout.setError(null);
            return true;
        }
    }

    /**
     * testuje či je náčitaný výrobca
     * @return true/false ak neiej prazdne pole vrati true
     */
    private boolean validateProducer() {
        String producerInput = producerInputLayout.getEditText().getText().toString().trim();

        if (producerInput.isEmpty()) {
            producerInputLayout.setError(getString(R.string.no_read_company));
            return false;
        } else {
            producerInputLayout.setError(null);
            return true;
        }
    }


    /**
     *onClick pre tlacidlo pridat surovinu
     */

    public void addIngre(View view){
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "A");
        startActivityForResult(i, 1);



    }

    /**
     * listenet tlačidla pridať zo zoznamu
     * @param view onCLick
     */
    public void addFromList(View view){
        Intent i = new Intent(this, SelectedListActivity.class);

        startActivityForResult(i, 2);



    }

    /**
     * zavolenie dialog pre pridanie datumu
     */
    private void updateDate(){
        new DatePickerDialog(this, d, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();

    }

    /**
     * zavolenie dialog pre pridanie datumu
     */
    private void updatedateExpidation(){
        new DatePickerDialog(this, d2, dateTime.get(Calendar.YEAR),dateTime.get(Calendar.MONTH),dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateTextLabel();
        }
    };



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
     * update datum
     */
    private void updateTextLabel(){
        dateInputLayout.getEditText().setText(formatDateTime.format(dateTime.getTime()));


    }

    /**
     * update datum
     */
    private void updateTextLabel2(){
        dateExpidationInputLayout.getEditText().setText(formatDateTime.format(dateTime.getTime()));


    }



  /*
  vypis toustu
   */



    /**
     * pridanie suroviny k produktu
     * @param id suroviny
     */
    void readIndredients(final String id) {
    databaseProduct.child(id).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {

            product = snapshot.getValue(Product.class);
            if (snapshot.exists()) {
                product.setProduktId(id);
                elementList.add(product);
                mAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AddProductActivity.this, getString(R.string.database_not_found), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError atabaseError) {

        }
    });

}

    /**
     * pri starte aktivity načita vyrobcu podla id uživateloveho id vyrobcu
     */
    protected void onStart() {
        super.onStart();

        databaseProducer.child(IntentConstants.databaseProducer).child(IntentConstants.idProducer).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
               producer = snapshot.getValue(Producer.class);
               producer.setId(snapshot.getKey());
               producerInputLayout.getEditText().setText(producer.getTitle());
   }

            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {

                finish();
            }
        });
    }

    /**
     * onClick pre text input layout datum výroby
     *  @param view on click
     */
    public void openDate1(View view) {
        updateDate();

    }

    /**
     * onClick pre text input layout datum spotreby
     *  @param view on click
     */
    public void opendateExpidation(View view) {
        updatedateExpidation();

    }
}
