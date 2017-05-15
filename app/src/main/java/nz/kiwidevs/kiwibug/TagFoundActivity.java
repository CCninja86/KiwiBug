package nz.kiwidevs.kiwibug;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Calendar;

public class TagFoundActivity extends AppCompatActivity {

    TextView nfcUserMsg;
    private ProgressDialog progressDialog;
    private Globals globals;
    private Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_found);

        context = this;

        globals = Globals.getInstance();

        double lat = globals.getCurrentLocation().getLatitude();
        double lng = globals.getCurrentLocation().getLongitude();

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

        String tagID = "test";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Tag Location...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://netweb.bplaced.net/kiwibug/api.php?action=insertTagData&lat=" + lat + "&lng=" + lng + "&username=" + username + "&id=" + tagID + "";

        Ion.with(this)
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
                            Toast.makeText(getApplicationContext(), "Tag Location Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update Tag Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        nfcUserMsg = (TextView) findViewById(R.id.nfcUserMsg);
        String userMsg = getIntent().getStringExtra("nfcData");
        nfcUserMsg.setText(userMsg);


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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Write Message");
                builder.setMessage("Enter your message below");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Scan NFC Tag");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        // TODO: Write message to record index 1 (second record) of scanned NFC Tag, then display success message to user
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


    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}
