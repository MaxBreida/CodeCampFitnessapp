package com.codecamp.bitfit.statistics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.DBQueryHelper;

import java.util.List;

public class RunHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_history);

        List<Run> allRuns = DBQueryHelper.findAllRuns();
        RunAdapter runAdapter = new RunAdapter(this, allRuns);

        ListView listView = findViewById(R.id.listview_run_history);
        listView.setAdapter(runAdapter);
    }
}
