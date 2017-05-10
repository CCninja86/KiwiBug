package nz.kiwidevs.kiwibug;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Calendar;

public class TagActivity extends AppCompatActivity {

    TextView nfcUserMsg;
    private ProgressDialog progressDialog;
    private Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

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
        String username = sharedPreferences.getString("username", "anonymous");

        String tagID = "test";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
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
                            Toast.makeText(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        nfcUserMsg = (TextView) findViewById(R.id.nfcUserMsg);
        String userMsg = getIntent().getStringExtra("nfcData");
        nfcUserMsg.setText(userMsg);

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);


    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
