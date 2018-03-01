package com.codecamp.bitfit.statistics;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.codecamp.bitfit.fragments.SquatFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SquatStatisticsActivity extends ListActivity {
    SquatFragment helperSquatFragment = new SquatFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(DBQueryHelper.findAllSquats().isEmpty()){
            fillTable();    //Fill table with dummy squats if the squat database is empty (for testing purpose)
        }

        ArrayList<String> squatStringList = new ArrayList<>();
        List<Squat> allSquats = DBQueryHelper.findAllSquats();

        for(Squat squat:allSquats){
            squatStringList.add(getSquatAsString(squat));
        }

        SquatAdapter squatItemAdapter = new SquatAdapter(this, allSquats);



        setListAdapter(squatItemAdapter);

    }

    protected void fillTable(){
        createDummySquat(30, 25);
        createDummySquat(10, 8);
        createDummySquat(42, 51);
        createDummySquat(30, 29);
    }

    protected String getSquatAsString(Squat squat){
        String squatString = "   Squats: " + squat.getRepeats() +  ", Duration: " +
                squat.getDurationInMillis()/1000 + " s, Squats/Min: " + squat.getSquatPerMin();

        return squatString;
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
