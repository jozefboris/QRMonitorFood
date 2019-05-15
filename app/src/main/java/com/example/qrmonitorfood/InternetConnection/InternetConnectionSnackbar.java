package com.example.qrmonitorfood.InternetConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.qrmonitorfood.R;

public class InternetConnectionSnackbar {
    private Context context;
    private View view;

public InternetConnectionSnackbar(Context context, View view){

  this.context = context;
  this.view = view;

}

    /**
     * Zobrazí snackbar pri nedostupnosti internetu
     */

public void checkConnection(){

    if(!isNetworkAvailable()) {

        Snackbar snackbar = Snackbar
                .make(view, R.string.no_connection, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}


    /**
     * Metoda testuje dostupnost internetu
     * @return vrati frue/false podla dostupnosti internetu
     */

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
