package com.example.qrmonitorfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.qrmonitorfood.Aktivity.ListAddActivity;

public class MenuSingInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_sing_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void openAdd(View view){
        Intent intent = new Intent(this, ListAddActivity.class);

        startActivity(intent);

    }

    public void openScan(View view){
        Intent intent = new Intent(this, TryActivity.class);

        startActivity(intent);

    }

}
