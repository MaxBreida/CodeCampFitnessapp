package com.codecamp.bitfit.statistics;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.DBQueryHelper;


import java.util.Collections;
import java.util.List;

public class SquatHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_history);

        List<Squat> allSquats = DBQueryHelper.findAllSquats();
        Collections.reverse(allSquats);

        SquatAdapter squatAdapter = new SquatAdapter(this, allSquats);

        ListView listView = findViewById(R.id.listview_squat_history);
        listView.setAdapter(squatAdapter);

    }
}
