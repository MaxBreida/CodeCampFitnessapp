package com.codecamp.bitfit.statistics;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.util.DBQueryHelper;

import java.util.Collections;
import java.util.List;

public class PushupHistoryActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pushup_history);

        List<PushUps> allPushUps = DBQueryHelper.findAllPushUps();
        Collections.reverse(allPushUps);
        PushupAdapter pushupAdapter = new PushupAdapter(this, allPushUps);

        ListView listView = findViewById(R.id.listview_pushup_history);
        listView.setAdapter(pushupAdapter);
    }
}
