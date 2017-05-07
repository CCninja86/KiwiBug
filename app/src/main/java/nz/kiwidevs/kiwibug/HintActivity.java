package nz.kiwidevs.kiwibug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

public class HintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        ListView listViewHints = (ListView) findViewById(R.id.listViewHints);

        // TODO: Implement custom ListAdapter for HintList class
    }

}
