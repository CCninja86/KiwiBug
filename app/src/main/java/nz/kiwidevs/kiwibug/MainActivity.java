package nz.kiwidevs.kiwibug;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import nz.kiwidevs.kiwibug.utils.NfcUtils;

public class MainActivity extends AppCompatActivity implements MapsFragment.OnFragmentInteractionListener, NewUserFragment.OnFragmentInteractionListener {

    private FragmentTransaction fragmentTransaction;

    private static final int PERMISSION_FINE_LOCATION = 1;

    private NfcUtils nfcutils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity context = this;

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED){
            startApp();
        } else {
            AlertDialog.Builder permissionExplanation = new AlertDialog.Builder(this);
            permissionExplanation.setTitle("Need Location Permission");
            permissionExplanation.setMessage("This app needs access to your location in order to function. Please grant permission when prompted.");
            permissionExplanation.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                }
            });

            permissionExplanation.show();


        }

        nfcutils = new NfcUtils(this);

        //No NFC or NFC disabled, close activity
        if(!nfcutils.setupNfcTagIntent()){
            finish();
        }

        nfcutils.getTagInfoForIntent(getIntent());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startApp();
                } else {

                }

                break;
        }
    }

    @Override
    public void onFragmentInteraction(String action) {
        if(action.equals("continue")){
            MapsFragment mapsFragment = new MapsFragment();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container, mapsFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onResume(){
        super.onResume();

        nfcutils.enableForegroundDispatch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d("Main", "I get executed onNewIntent");
        nfcutils.getTagInfoForIntent(intent);
    }

    private void startApp(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("nz.kiwidevs.kiwibug.SHARED", Context.MODE_PRIVATE);
        boolean firstTime = sharedPreferences.getBoolean("first_time", true);

        if(firstTime){
            NewUserFragment newUserFragment = new NewUserFragment();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, newUserFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_time", false);
            editor.commit();
        } else {
            MapsFragment mapsFragment = new MapsFragment();
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, mapsFragment);
            fragmentTransaction.commit();
        }
    }
}
