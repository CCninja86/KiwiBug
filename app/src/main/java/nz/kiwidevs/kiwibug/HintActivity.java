package nz.kiwidevs.kiwibug;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class HintActivity extends AppCompatActivity {

    private ListView listViewHints;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        setTitle("Hints");

        listViewHints = (ListView) findViewById(R.id.listViewHints);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Ion.with(this)
                .load("http://netweb.bplaced.net/kiwibug/api.php?action=getHints")
                .as(new TypeToken<Hint[]>(){})
                .setCallback(new FutureCallback<Hint[]>() {
                    @Override
                    public void onCompleted(Exception e, Hint[] hintsArray) {
                        if(progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                        ArrayList<Hint> hintArrayList = new ArrayList<>();

                        for(Hint hint : hintsArray){
                            hintArrayList.add(hint);
                        }

                        setAdapter(hintArrayList);
                    }
                });
    }

    public void setAdapter(ArrayList<Hint> hints){
        ArrayAdapter adapter = new HintListViewAdapter(this, R.layout.hint_row, hints);
        listViewHints.setAdapter(adapter);
    }

}
