package com.codecamp.bitfit.statistics;

import android.app.ListActivity;
import android.os.Bundle;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.util.List;
import java.util.UUID;

public class RunStatisticsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Check why setContentView would replace data from first run with the one from master layout
        setContentView(R.layout.activity_run_statistics);

        if(DBQueryHelper.findAllRuns().isEmpty()){
            fillTableWithDummies();    //Fill table with dummy runs if the run database is empty (for testing purpose)
        }

        List<Run> allRuns = DBQueryHelper.findAllRuns();

        RunAdapter runAdapter = new RunAdapter(this, allRuns);


        setListAdapter(runAdapter);

    }

    protected void fillTableWithDummies(){
        createDummyRun(2, 15*60);
        createDummyRun(4, 30*60);
        createDummyRun(11, 100*60);
        createDummyRun(0.5, 30*60);
    }

    protected  void createDummyRun(double distance, long durationAsSeconds){
        Run dummyRun = new Run();

        dummyRun.setId(UUID.randomUUID());
        dummyRun.setCurrentDate(Util.getCurrentDateAsString());
        dummyRun.setDurationInMillis(1000*durationAsSeconds);
        dummyRun.setCalories(100);
        dummyRun.setDistanceInKm(distance);

        dummyRun.save();
    }

}
