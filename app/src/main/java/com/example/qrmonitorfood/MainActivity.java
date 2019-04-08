package com.example.qrmonitorfood;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.qrmonitorfood.Aktivity.AboutProductActivity;
import com.example.qrmonitorfood.Aktivity.AddIngredientsActivity;
import com.example.qrmonitorfood.Aktivity.AddProductActivity;
import com.example.qrmonitorfood.Aktivity.DetailActivity;
import com.example.qrmonitorfood.Aktivity.LoginActivity;
import com.example.qrmonitorfood.Aktivity.SearchListActivity;
import com.example.qrmonitorfood.Aktivity.UpdateProfilActivity;
import com.example.qrmonitorfood.Constants.IntentConstants;
import com.example.qrmonitorfood.Database.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;



public class MainActivity extends AppCompatActivity {

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    MenuItem logoutIcon;
    MenuItem signInIcon;
    MenuItem updateProfilIcon;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseUser;
    CardView search;
    User user;
    ProgressBar progressBar;
    DatabaseReference databaseProducer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = findViewById(R.id.button_search);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 =(FloatingActionButton) (FloatingActionButton) findViewById(R.id.floating_button1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_button2);
        databaseUser = FirebaseDatabase.getInstance().getReference(IntentConstants.databaseUser);
        progressBar = findViewById(R.id.progressBar);
        databaseProducer = FirebaseDatabase.getInstance().getReference();
        // dialog ak neje zariadenie pripojene k internetu
        if (!isNetworkAvailable()) {

            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.dialog_closing))
                    .setMessage(getString(R.string.dialog_no_connection))
                    .setPositiveButton(getString(R.string.dialog_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .show();

        }


    }

    /**
     * otvorí aktivitu pre pridanie produktu
     * @param view
     */

    public void openAddProduct(View view) {
        Intent intent = new Intent(this, AddProductActivity.class);
        startActivity(intent);

    }

    /**
     * * otvorí aktivitu pre pridanie indegrediencii
     */

    public void openAddIngredients(View view) {
        Intent i = new Intent(this, AddIngredientsActivity.class);
        i.putExtra("FROM_ACTIVITY", "B");
        startActivityForResult(i, 1);


    }

    /**
     * * otvorí aktivitu pre vyhľadávanie potravín
     * @param view
     */

    public void openSearch(View view) {
        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(this, SearchListActivity.class);
        startActivity(intent);
    }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        logoutIcon = menu.findItem(R.id.logout);
        signInIcon = menu.findItem(R.id.signIn);
        updateProfilIcon = menu.findItem(R.id.updateProfil);
        firebaseAuth = FirebaseAuth.getInstance();

        // ak  uživateľ nieje prihlasený nnemá viditelné tlačidla
        if (firebaseAuth.getCurrentUser() == null) {
            materialDesignFAM.setVisibility(View.INVISIBLE);
            search.setVisibility(View.INVISIBLE);
            logoutIcon.setVisible(false);
            updateProfilIcon.setVisible(false);
            progressBar.setVisibility(View.INVISIBLE);


        } else {
            addProducer();
            signInIcon.setVisible(false);

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.signIn) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *  otvorí aktivitu pre prihlásenie do aplikácie z ikony v menu
     * @param item
     */

    public void openAccess(MenuItem item) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }

    /**
     * * po kliknutí odhlásy uživatela
     * @param item
     */

    public void openLogout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, R.string.logout_sucessfull, Toast.LENGTH_LONG).show();
        Intent I = new Intent(MainActivity.this, MainActivity.class);
        startActivity(I);

    }

    /**
     * * otvorí aktivitu pre upravu účtu použivataľa
     * @param item
     */

    public void openUpdateProfil(MenuItem item) {
        Intent I = new Intent(MainActivity.this, UpdateProfilActivity.class);
        startActivity(I);

    }

    /**
     * otvorí kameru na naskenovanue qr kodu
     * @param view
     */

    public void openScan(View view) {

        if (isNetworkAvailable()){
        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getString(R.string.scan));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
        } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();

        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //výsledok skenovania qr kodu
        if (isNetworkAvailable()){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, R.string.toast_cancel_scanning, Toast.LENGTH_LONG).show();
            } else {

                //     testuje ak je uživatel je prihlasený otvorí aktivitu detailActivity ak nieje prihlaseny otvorí AboutActivity.
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
    } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();

        }
    }

    /**
     * metoda testuje dostupnost internetu
     * @return true, false
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void addProducer() {


        databaseUser.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                user = snapshot.getValue(User.class);
                progressBar.setVisibility(View.GONE);
                IntentConstants.idProducer = user.getProducerId();

    progressBar.setVisibility(View.GONE);



            }
            @Override
            public void onCancelled(DatabaseError atabaseError) {
            }
        });
    }




}