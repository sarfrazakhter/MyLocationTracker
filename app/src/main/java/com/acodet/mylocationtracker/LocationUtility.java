package com.acodet.mylocationtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by Vikas on 11/3/2016.
 */
public class LocationUtility {

    //final static String message = " Your Location service is disabled, we recommend you to enable it, to enable click 'Enable' and Cancel to close.";

    final static String message = "Enable GPS Services to determine approximate location for PANIC, Check-In/ Check-Out, etc.";

    final static String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;

    static boolean locationServiceStatus;

    public static boolean isLocationServiceEnabled(final Context context) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationServiceStatus = true;
        }
        return locationServiceStatus;
    }

   /* public static void displayPromptForEnablingGPS(final Context context) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView( R.layout.popup_location_utility);
        dialog.setCancelable(true);

        TextView textView = (TextView) dialog.findViewById(R.id.textViewLocationErrorMsg);
        textView.setText(message);

        Button buttonEnable = (Button) dialog.findViewById(R.id.btn_locationServiceEnable);
        Button buttonCancelLocDialog = (Button) dialog.findViewById(R.id.btn_declinelocationService);

        buttonEnable.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                context.startActivity(new Intent(action));
                dialog.dismiss();
            }
        });

        buttonCancelLocDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        dialog.show();

    }
*/

    ////////////
    public static void displayPromptForEnablingGPS(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(callGPSSettingIntent);

                        // mapFrag.getMapAsync(HomeMainActivity.this);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

}

