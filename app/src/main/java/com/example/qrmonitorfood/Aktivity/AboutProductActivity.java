package com.example.qrmonitorfood.Aktivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.example.qrmonitorfood.ListAdapter.Movie;
import com.example.qrmonitorfood.ListAdapter.MoviesAdapter;
import com.example.qrmonitorfood.R;
import com.example.qrmonitorfood.ListAdapter.RecyclerTouchListener;
import com.example.qrmonitorfood.UpdateProductActivity;
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
import java.util.List;

public class AboutProductActivity extends AppCompatActivity {

    private MenuItem deleteIcon;
    private MenuItem updateIcon;
    private MenuItem shareIcon;
    private ImageView qrImage;
    private String code;
    final private String url = "www.qrfoodmonitor.com/id=";
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        qrImage = (ImageView) findViewById(R.id.qrImage);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(movieList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);


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
                Movie movie = movieList.get(position);
                Toast.makeText(getApplicationContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        prepareMovieData();


    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_about_product, menu);
        deleteIcon = menu.findItem(R.id.action_delete);
        shareIcon = menu.findItem(R.id.action_share);
        updateIcon = menu.findItem(R.id.action_update);
     //   qrImage = (ImageView) findViewById(R.id.qrImage);

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

        return true;
    }

    public void delete(MenuItem item) {
        Intent intent = getIntent();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AboutProductActivity.this);
        builder1.setMessage("Do you want delete item?" + code );
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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
