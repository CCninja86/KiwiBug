package nz.kiwidevs.kiwibug;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class TagActivity extends AppCompatActivity {

    TextView nfcUserMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        nfcUserMsg = (TextView) findViewById(R.id.nfcUserMsg);


        String userMsg = getIntent().getStringExtra("nfcData");

        nfcUserMsg.setText(userMsg);

    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);


    }

}
