package com.example.qrmonitorfood.Aktivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.qrmonitorfood.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class AboutProductActivity extends AppCompatActivity {

    private MenuItem deleteIcon;
    private MenuItem updateIcon;
    private MenuItem shareIcon;
    private ImageView qrImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_about_product, menu);
        deleteIcon = menu.findItem(R.id.action_delete);
        shareIcon = menu.findItem(R.id.action_share);
        updateIcon = menu.findItem(R.id.action_update);
        qrImage = (ImageView) findViewById(R.id.qrImage);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        String sessionId= getIntent().getStringExtra("data");
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(sessionId, BarcodeFormat.QR_CODE,400,400);
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
        String data = intent.getStringExtra("data");
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AboutProductActivity.this);
        builder1.setMessage("Do you want delete item?" + data );
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

    }

    public void share(MenuItem item) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

      /*  Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, );
        shareIntent.setType("image/*");*/
    }

}
