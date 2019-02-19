package com.example.qrmonitorfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.qrmonitorfood.Aktivity.AboutProductActivity;
import com.example.qrmonitorfood.Aktivity.GeneratorQRActivity;
import com.example.qrmonitorfood.Aktivity.ScanQRActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);

        startActivity(intent);

    }

    public void openScan(View view){
        Intent intent = new Intent(this, ScanQRActivity.class);

        startActivity(intent);

    }
    public void openGenerator(View view){
        Intent intent = new Intent(this, GeneratorQRActivity.class);

        startActivity(intent);

    }

    public void openAddProduct(View view){
        Intent intent = new Intent(this, com.example.qrmonitorfood.AddProductActivity.class);

        startActivity(intent);

    }

    public void openAboutProduct(View view){
        Intent intent = new Intent(this, AboutProductActivity.class);

        startActivity(intent);

    }

    public void openTry(View view){
        Intent intent = new Intent(this, TryActivity.class);

        startActivity(intent);

    }
}
