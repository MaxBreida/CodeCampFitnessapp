package com.codecamp.bitfit.statistics;

import android.app.ListActivity;
import android.os.Bundle;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.util.List;
import java.util.UUID;

public class SquatStatisticsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Check why he doesn't find the listView by id when using setContentView (is sCV required?)
        setContentView(R.layout.activity_squat_statistics);

        if(DBQueryHelper.findAllSquats().isEmpty()){
            fillTableWithDummies();    //Fill table with dummy squats if the squat database is empty (for testing purpose)
        }

        List<Squat> allSquats = DBQueryHelper.findAllSquats();

        SquatAdapter squatItemAdapter = new SquatAdapter(this, allSquats);


        setListAdapter(squatItemAdapter);

    }

    protected void fillTableWithDummies(){
        createDummySquat(30, 25);
        createDummySquat(10, 8);
        createDummySquat(42, 51);
        createDummySquat(30, 29);
    }

    protected  void createDummySquat(int noOfSquats, long durationAsSeconds){
        Squat dummySquat = new Squat();

        dummySquat.setId(UUID.randomUUID());
        dummySquat.setCurrentDate(Util.getCurrentDateAsString());
        dummySquat.setDurationInMillis(1000*durationAsSeconds);
        dummySquat.setCalories(100);
        dummySquat.setRepeats(noOfSquats);
        dummySquat.setSquatPerMin((double) (noOfSquats/(60*durationAsSeconds)));

        dummySquat.save();
    }

}
