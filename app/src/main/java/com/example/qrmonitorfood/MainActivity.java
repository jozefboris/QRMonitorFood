package com.example.qrmonitorfood;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.qrmonitorfood.Aktivity.AboutProductActivity;
import com.example.qrmonitorfood.Aktivity.AddIngredientsActivity;
import com.example.qrmonitorfood.Aktivity.AddProductActivity;
import com.example.qrmonitorfood.Aktivity.DetailActivity;
import com.example.qrmonitorfood.Aktivity.LoginActivity;
import com.example.qrmonitorfood.Aktivity.SearchListActivity;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    MenuItem logoutIcon;
    MenuItem signInIcon;
    FirebaseAuth firebaseAuth;
    CardView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = findViewById(R.id.button_search);
        materialDesignFAM = findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = findViewById(R.id.floating_button1);
        floatingActionButton2 = findViewById(R.id.floating_button2);
    }

    /**
     * OnClick na tlačidlo Pridat produkt - metoda otvorí aktivitu pre pridanie produktu AddProductActivity
     */

    public void openAddProduct(View view) {
        Intent intent = new Intent(this, AddProductActivity.class);
        startActivity(intent);
        materialDesignFAM.close(true);

    }



    /**
     * OnClick na tlačidlo Pridat surovinu - metoda otvorí aktivitu pre pridanie suroviny AddIngredientActivity
     * posle extra data s hodnotou B, aby nasledujuca aktivita vedela z ktorej aktivity sa spusta
     */

    public void openAddIngredients(View view) {
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "B");
        startActivityForResult(i, 1);
        materialDesignFAM.close(true);


    }


    /**
     * OnClick na tlačidlo Zoznam potravín - metoda otvorí aktivitu pre zobrazenie zoznamu potravín SearchListActivity
     */

    public void openSearch(View view) {
            Intent intent = new Intent(this, SearchListActivity.class);
            startActivity(intent);
            materialDesignFAM.close(true);

    }


    /**
     * Zobrazenie menu
     * @param menu menu_main
     * @return true
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        logoutIcon = menu.findItem(R.id.logout);
        signInIcon = menu.findItem(R.id.signIn);
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPreferences prefs = getSharedPreferences("ID", MODE_PRIVATE);
        IntentConstants.idProducer = prefs.getString("ID", null);
        if (firebaseAuth.getCurrentUser() == null) {
            materialDesignFAM.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
            logoutIcon.setVisible(false);


        } else {
            signInIcon.setVisible(false);

        }
        return true;
    }


    /**
     * Zobrazenie v menu
     * @param item položka menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.signIn) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * OnClick na ikonu person - metoda otvorí aktivitu pre prihlasenie LoginActivity
     */
    public void openAccess(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    /**
     * OnClick na tlačidlo Odhlásiť sa - metoda odhlasy použivatela zo systému
     */

    public void openLogout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, R.string.logout_sucessfull, Toast.LENGTH_LONG).show();
        finishAffinity();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }




    /**
     * OnClick na tlačidlo Skenovať surovinu - metoda otvorí kameru a nasnima Qr kod, ktorý spracuje v metode onActivityResult
     */
    public void openScan(View view) {


        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getString(R.string.scan));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        materialDesignFAM.close(true);



    }


    /**
     *Metoda vezme id potraviny z načitaneho QR kodu a otvory aktivitu ak je použivatel prihlaseny AboutProductActivity
     * ak nieje uživatel prihlasený otvory intent aktvity DetailActivity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {

            } else {

                if (firebaseAuth.getCurrentUser() == null) {

                    final Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(IntentConstants.idCode, result.getContents().substring(result.getContents().lastIndexOf("id=")+3).trim()  );
                    startActivity(intent);



                } else {
                    final Intent intent = new Intent(this, AboutProductActivity.class);
                    intent.putExtra(IntentConstants.idCode, result.getContents().substring(result.getContents().lastIndexOf("id=")+3).trim());
                    startActivity(intent);

                }
            }

        }

    }

}