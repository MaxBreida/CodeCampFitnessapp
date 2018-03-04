package com.codecamp.bitfit.statistics;

import android.app.ListActivity;
import android.os.Bundle;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.util.List;
import java.util.UUID;

public class PushupStatisticsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Check why setContentView would replace data from first pushup with the one from master layout
        setContentView(R.layout.activity_pushup_statistics);

        if(DBQueryHelper.findAllPushUps().isEmpty()){
            fillTableWithDummies();    //Fill table with dummy squats if the squat database is empty (for testing purpose)
        }

        List<PushUps> allPushUps = DBQueryHelper.findAllPushUps();

        PushupAdapter pushupAdapter = new PushupAdapter(this, allPushUps);


        setListAdapter(pushupAdapter);

    }

    protected void fillTableWithDummies(){
        createDummyPushup(8, 25);
        createDummyPushup(4, 12);
        createDummyPushup(16, 51);
        createDummyPushup(24, 123);
    }

    protected  void createDummyPushup(int noOfPushups, long durationAsSeconds){
        PushUps dummyPushup = new PushUps();

        dummyPushup.setId(UUID.randomUUID());
        dummyPushup.setCurrentDate(Util.getCurrentDateAsString());
        dummyPushup.setDurationInMillis(1000*durationAsSeconds);
        dummyPushup.setCalories(100);
        dummyPushup.setRepeats(noOfPushups);
        dummyPushup.setPushPerMin((double) (noOfPushups/(60*durationAsSeconds)));

        dummyPushup.save();
    }


}
