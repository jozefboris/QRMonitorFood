package com.example.qrmonitorfood.InternetConnection;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import com.example.qrmonitorfood.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

    Context context;

    public NetworkChangeReceiver(Context context){
        this.context = context;
    }

    /**
     * testuje dostupnost pripojenia a podla toho zavola metodu s dialogom
     * @param context aktivity
     */

    @Override
    public void onReceive(Context context, Intent intent)
    {
        try
        {
            if (!isOnline(context)) {
                    dialog();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Metoda na zistenie dostupnosti intenetu
     * @param context aktivity
     * @return true/false podla dostupnosti intenetu
     */
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * dialog na zobrazenie v priapde edostupnosti internetu
     */

    public void dialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(R.string.dialog_closing);
        alertDialog.setMessage(R.string.dialog_no_connection);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((Activity)context).finish();
                    arg0.cancel();
                }
                return true;
            }
        });

            alertDialog.setNegativeButton(R.string.try_connection, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!isOnline(context)) {

                        dialog();
                    }
                }
            });
           alertDialog.show();
    }
}