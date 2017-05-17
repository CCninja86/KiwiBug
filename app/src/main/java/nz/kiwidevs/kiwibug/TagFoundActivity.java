package nz.kiwidevs.kiwibug;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Calendar;

import nz.kiwidevs.kiwibug.utils.NfcUtils;

public class TagFoundActivity extends AppCompatActivity implements WriteNFCFragment.OnFragmentInteractionListener  {

    TextView nfcUserMsg;
    private ProgressDialog progressDialog;
    private Globals globals;
    private Activity context;
    private FragmentTransaction fragmentTransaction;
    private EditText inputNfcMessage;
    WriteNFCFragment nfcFragment;
    boolean isWrite, isDialogDisplayed;
    static boolean isReturningFromWrite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_found);

        context = this;

        globals = Globals.getInstance();

        //if(globals.getCurrentLocation()!=null) {
            double lat = globals.getCurrentLocation().getLatitude();
            double lng = globals.getCurrentLocation().getLongitude();
       // }

        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String day = String.valueOf(calendar.get(Calendar.DATE));
        String hour = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(calendar.get(Calendar.MINUTE));
        String second = String.valueOf(calendar.get(Calendar.SECOND));

        if(Integer.parseInt(month) < 10){
            month = "0" + month;
        }

        if(Integer.parseInt(day) < 10){
            day = "0" + day;
        }

        if(Integer.parseInt(hour) < 10){
            hour = "0" + hour;
        }

        if(Integer.parseInt(minute) < 10){
            minute = "0" + minute;
        }

        if(Integer.parseInt(second) < 10){
            second = "0" + second;
        }

        String currentTimestamp = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;

        SharedPreferences sharedPreferences = getSharedPreferences("nz.kiwidevs.kiwibug.SHARED", Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString("username", "anonymous");


        nfcUserMsg = (TextView) findViewById(R.id.nfcUserMsg);
        String[] userMsg = getIntent().getStringArrayExtra("nfcData");
        nfcUserMsg.setText(userMsg[1]);

        String tagID = userMsg[0];



        Log.d("TagFoundActivity", "Before network block isWrite:" + isReturningFromWrite);
        if(!isReturningFromWrite) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating Tag Location...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();

            String url = "http://netweb.bplaced.net/kiwibug/api.php?action=insertTagData&lat=" + lat + "&lng=" + lng + "&username=" + username + "&id=" + tagID + "";

            Log.d("TagFoundActivity", "In network block isWrite:" + isReturningFromWrite);

            Ion.with(this)
                    .load(url)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                progressDialog = null;
                            }

                            if (result.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Tag Location Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to update Tag Location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            isReturningFromWrite = true;

        }


        FloatingActionButton floatingActionButtonSubmitHint = (FloatingActionButton) findViewById(R.id.btnSubmitHint);
        FloatingActionButton floatingActionButtonWriteMessage = (FloatingActionButton) findViewById(R.id.btnWriteMessage);

        floatingActionButtonSubmitHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Submit Hint");
                builder.setMessage("Enter your hint below");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String url = "http://netweb.bplaced.net/kiwibug/api.php?action=insertHint&hint=" + input.getText().toString() + "&user=" + username;
                        url = url.replaceAll(" ", "%20");

                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Submitting...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        Ion.with(context)
                                .load(url)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if(progressDialog != null && progressDialog.isShowing()){
                                            progressDialog.dismiss();
                                            progressDialog = null;
                                        }

                                        if(result.equals("success")){
                                            Toast.makeText(context, "You submitted a hint to the public hint board", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(context, "Hint was not submitted", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        floatingActionButtonWriteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isWrite = true;
                isReturningFromWrite = true;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Write a Message on the Tag");
                builder.setMessage("Enter your message below");

                inputNfcMessage = new EditText(context);
                inputNfcMessage.setInputType(InputType.TYPE_CLASS_TEXT);
                inputNfcMessage.setFilters(new InputFilter[] {new InputFilter.LengthFilter(110)});
                builder.setView(inputNfcMessage);

                builder.setPositiveButton("Write",new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        nfcFragment = (WriteNFCFragment) getSupportFragmentManager().findFragmentByTag(WriteNFCFragment.TAG);

                        if(nfcFragment == null){
                            nfcFragment = WriteNFCFragment.newInstance();
                        }

                        nfcFragment.show(getSupportFragmentManager(),WriteNFCFragment.TAG);

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();


            }
        });



    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);

        if(isWrite){
            if(isDialogDisplayed){
                NfcUtils.getTagIDFromTag(this, intent);

                String[] userMsg = getIntent().getStringArrayExtra("nfcData");



                nfcFragment = (WriteNFCFragment) getSupportFragmentManager().findFragmentByTag(WriteNFCFragment.TAG);
               nfcFragment.onNFCTagDetected(intent, inputNfcMessage.getText().toString(),userMsg[0]);



            }
        }


    }

    @Override
    protected void onPause(){
        super.onPause();


    }


    @Override
    protected void onResume() {
        super.onResume();

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);

    }




    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }

    @Override
    public void onDialogDismissed() {
        isDialogDisplayed = false;
        isWrite = false;

        finish();

    }
}


