package com.example.qrmonitorfood.Aktivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.qrmonitorfood.CheckInternet;
import com.example.qrmonitorfood.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =  new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);

                finish();

            }
        }, 1000);



    }

}
