package com.example.qrmonitorfood.Aktivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrmonitorfood.Database.Product;
import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.example.qrmonitorfood.UpdateProductActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AboutProductActivity extends AppCompatActivity {
    Product uInfo = new Product();
    private MenuItem deleteIcon;
    private MenuItem updateIcon;
    private MenuItem shareIcon;

    ProgressBar progressBar;
   // Product track;
    String value;
    TextView title;
    Product product = new Product();
    private ImageView qrImage;
    private String code;
    final private String url = "www.qrfoodmonitor.com/id=";
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    DatabaseReference databaseProduct;
    DatabaseReference databaseComponents;
    private boolean isThere;

    TextView titleText, dateText, date2text, countText, producerText, descriptionText,typeText;
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
        date2text = findViewById(R.id.date2text);
        countText = findViewById(R.id.counttext);
        producerText = findViewById(R.id.producertext);
        descriptionText = findViewById(R.id.descriptiontext);
        typeText = findViewById(R.id.typetext);

        mAdapter = new MoviesAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);

        databaseProduct = FirebaseDatabase.getInstance().getReference("product");
        databaseComponents = FirebaseDatabase.getInstance().getReference("components");
        // vertical RecyclerView
        // keep movie_list_row.xml width to `match_parent`
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        progressBar = (ProgressBar) findViewById(R.id.progress);


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
                Movie movie = movieList.get(position);
                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        code = getIntent().getStringExtra("idCode");


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


       // prepareMovieData();


    }

  /*  private void showData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()){
            Product uInfo = new Product();
            uInfo.setProduktId(code); //set the name
            uInfo.setTitle(ds.child(code).getValue(Product.class).getTitle()); //set the email
            uInfo.setDateOfMade(ds.child(code).getValue(Product.class).getDateOfMade());
            uInfo.setDateExpiration(ds.child(code).getValue(Product.class).getDateOfMade());
            uInfo.setCount(ds.child(code).getValue(Product.class).getCount());
            uInfo.setProducer(ds.child(code).getValue(Product.class).getProducer());
            uInfo.setDecription(ds.child(code).getValue(Product.class).getDecription());
            //display all the information



        }
    }*/

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_about_product, menu);
        deleteIcon = menu.findItem(R.id.action_delete);
        shareIcon = menu.findItem(R.id.action_share);
        updateIcon = menu.findItem(R.id.action_update);
     //   qrImage = (ImageView) findViewById(R.id.qrImage);



       // databaseProduct = FirebaseDatabase.getInstance().getReference("product").child(code);

        return true;
    }

  /*  public void readData() {
      //  super.onStart();

        databaseProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Product track = postSnapshot.getValue(Product.classr);
                    movieList.add(track);
                }
                //TrackList trackListAdapter = new TrackList(ArtistActivity.this, tracks);
                //listViewTracks.setAdapter(trackListAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
  public void readData() {
      //  super.onStart();

      databaseComponents.orderByChild("id_produkt").equalTo(code).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              movieList.clear();
              for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                  DatabaseReference dR = FirebaseDatabase.getInstance().getReference("components").child(postSnapshot.getKey());
                  dR.removeValue();


              }
              //TrackList trackListAdapter = new TrackList(ArtistActivity.this, tracks);
              //listViewTracks.setAdapter(trackListAdapter);
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });
  }

          public void delete(MenuItem item) {

           writeData();

        Intent intent = getIntent();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AboutProductActivity.this);
        builder1.setMessage("Do you want delete item?" );
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseReference dR = FirebaseDatabase.getInstance().getReference("product").child(code);

                        //removing artist
                        dR.removeValue();
                      readData();

                     /*   databaseComponents.child("users").orderByKey().equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot postsnapshot :dataSnapshot.getChildren()) {

                                    String key = postsnapshot.getKey();
                                    dataSnapshot.getRef().removeValue();

                                }             dataSnapshot.getRef().removeValue();

                                                                                                                           }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }*/

                        Toast.makeText(getApplicationContext(), "Product Deleted", Toast.LENGTH_LONG).show();

                        finish();
                        dialog.cancel();
                    }
                }

                );

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void update(MenuItem item) {
        Intent intent = new Intent(this, UpdateProductActivity.class);

        startActivity(intent);

}



    public void share(MenuItem item) throws WriterException {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            BitMatrix bitMatrix = multiFormatWriter.encode(url+code, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);


        try {
            File file = new File(this.getExternalCacheDir(),"logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            Toast.makeText(this, "Vyber zlozku", Toast.LENGTH_SHORT).show();
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            Toast.makeText(this, "Vyber zlozku", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void saveImageToExternalStorage(Bitmap finalBitmap) {

        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path +  "Image-" +  ".jpg");
        dir.mkdirs();

        File file = new File(dir +  "Image-" +  ".jpg");

        OutputStream out = null;

        try {
            out = new FileOutputStream(file);

            finalBitmap.compress(Bitmap.CompressFormat.JPEG,100,out);
                    out.flush();
                    out.close();



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });

    }

    private void findData(){

        databaseProduct.child(code).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                product.setProduktId(code);

                progressBar.setVisibility(View.GONE);
                //prints "Do you have data? You'll love Firebase."
                // product = new Product( snapshot.getValue(Product.class));
            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });



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

 // code = "-L_ZSzdXafGK3z2ocQYX";
    //    findData();
   writeData();
         //  Toast.makeText(this,value, Toast.LENGTH_LONG).show();



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
                product.setProduktId(code);

                progressBar.setVisibility(View.GONE);
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
