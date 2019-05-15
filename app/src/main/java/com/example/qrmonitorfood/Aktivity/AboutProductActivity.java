package com.example.qrmonitorfood.Aktivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.Producer;
import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.InternetConnection.InternetConnectionSnackbar;
import com.example.qrmonitorfood.ListAdapter.RecyclerAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AboutProductActivity extends AppCompatActivity {


    ProgressBar progressBar;
    TextView emptyList;
    TextView title;
    Product product = new Product();
    Product productIngredients = new Product();
    private ImageView qrImage;
    private String code;
    final private String url = IntentConstants.url;
    private List<Product> elementList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    DatabaseReference databaseProduct;
    DatabaseReference databaseProducer;
    Producer producer;
    TextView titleText, dateText, dateExpidationtext, batchText, producerText, descriptionText, typeText;
    InternetConnectionSnackbar connectionSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        qrImage = (ImageView) findViewById(R.id.qrImage);
        title = findViewById(R.id.titleText);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        titleText = findViewById(R.id.titleText);
        dateText = findViewById(R.id.datetext);
        dateExpidationtext = findViewById(R.id.dateExpirationtext);
        batchText = findViewById(R.id.batchtext);
        producerText = findViewById(R.id.producertext);
        descriptionText = findViewById(R.id.descriptiontext);
        typeText = findViewById(R.id.typetext);
        databaseProduct = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseProduct);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        mAdapter = new RecyclerAdapter(elementList,0);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        emptyList = findViewById(R.id.emptyList);
        connectionSnackbar = new InternetConnectionSnackbar(AboutProductActivity.this,title);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        final Intent intent = new Intent(this, AboutProductActivity.class);

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

        code = getIntent().getStringExtra(IntentConstants.idCode);


        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(url + code, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }
    }

    /**
     * Nastavenie menu
     * @param menu menu
     * @return true
     */
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_about_product, menu);
        return true;
    }

    /**
     * OnClick tlačidla pre vymazanie potraviny
     * @param item menu
     */

    public void delete(MenuItem item) {

        if (product.getProducerId().equals(IntentConstants.idProducer)) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(AboutProductActivity.this);
            builder1.setMessage(getString(R.string.delete_item));
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    getString(R.string.dialog_yes),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            if (connectionSnackbar.isNetworkAvailable()) {
                                DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Products").child(code);
                                dR.removeValue();
                                dialog.cancel();
                                finish();
                            } else {
                                Toast.makeText(AboutProductActivity.this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            builder1.setNegativeButton(
                    getString(R.string.dialog_no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        } else {
            Toast.makeText(this, getString(R.string.no_authorization), Toast.LENGTH_SHORT).show();
            }
        }



    /**
     *OnClick tlačidla pre upravenie potraviny, presmerovanie na updateProductActivity
     * metoda posle do dalšej aktivity id potraviny pre upravu
     * @param item menu
     */

    public void update(MenuItem item) {
if (connectionSnackbar.isNetworkAvailable()) {
    if (product.getProducerId().equals(IntentConstants.idProducer)) {

        Intent intent = new Intent(this, UpdateProductActivity.class);
        intent.putExtra(IntentConstants.idCode, code);
        startActivity(intent);

    } else {
        Toast.makeText(this, getString(R.string.no_authorization), Toast.LENGTH_SHORT).show();

    }
} else{
    Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
}
}

    /**
     * OnClick pre tlačidlo zdieľať, zobrazi sa okno pre výber aplikacie pre zdielnie obrazka s popisom
     * @param item menu
     */

    public void share(MenuItem item) throws WriterException {

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url + code, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);

            try {
                File file = new File(this.getExternalCacheDir(), product.getTitle() + ".png");
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT, product.getTitle());
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/png");


                startActivity(Intent.createChooser(intent, "Share image via"));
            } catch (Exception e) {
                e.printStackTrace();
            }


    }


    /**
     * Tlačidlo späť v menu
     * @param item menu
     * @return item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Vyplenie textView po stiahnutí informácii z databázy
     */

    private void writeData(){
        titleText.setText(product.getTitle());
        dateText.setText(product.getDateOfMade());
        dateExpidationtext.setText(product.getDateExpiration());
        batchText.setText(product.getBatch());
        descriptionText.setText(product.getDecription());
        typeText.setText(getString(R.string.ingredients));

        readProducer(product.getProducerId());
        if (product.getProducts().size() != 0){
            for (int i =0; i<product.getProducts().size();i++) {

                readIngredients(product.getProducts().get(i));
                emptyList.setVisibility(View.INVISIBLE);
            }
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }



    }

    /**
     * Spušta meódy pre výpis dat do textInputLayout a aktualizovanie listu
     */
    private void prepareElementData() {
        writeData();
        mAdapter.notifyDataSetChanged();

    }


    /**
     * Metoda pre nacitanie produktu z databazy
     */
    @Override
    protected void onStart() {
        super.onStart();
        elementList.clear();

        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                product = snapshot.getValue(Product.class);
                if (snapshot.exists()){
                    product.setProduktId(code);
                    progressBar.setVisibility(View.GONE);
                    prepareElementData();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.delete_product, Toast.LENGTH_LONG).show();
                    finish();
                }

                connectionSnackbar.checkConnection();
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {

            }
        });


    }


    /**
     * Pridanie surovín do zoznamu
     * @param id produktu
     */

    void readIngredients(final String id) {
        elementList.clear();
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
     * Načita z databazy vyrobcu
     *
     */
    public void readProducer(String id) {

        databaseProducer.child(IntentConstants.databaseProducer).child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    producer = snapshot.getValue(Producer.class);
                    producer.setId(snapshot.getKey());
                    producerText.setText(producer.getTitle());
                }else {
                    Toast.makeText(AboutProductActivity.this, R.string.error_read_producer, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {

            }
        });
    }
}
