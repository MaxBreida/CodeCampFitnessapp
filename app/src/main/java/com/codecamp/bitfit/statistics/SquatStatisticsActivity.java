package com.codecamp.bitfit.statistics;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.fragments.SquatFragment;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.util.List;

public class SquatStatisticsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat_statistics);

        if(!DBQueryHelper.findAllSquats().isEmpty()){
            List<Squat> allSquats = DBQueryHelper.findAllSquats();

            SquatAdapter squatItemAdapter = new SquatAdapter(this, allSquats);

            setListAdapter(squatItemAdapter);
        } else {
            //            fillTableWithDummies();    //Fill table with dummy squats if the squat database is empty (for testing purpose)
            Toast.makeText(this.getApplicationContext(), "Noch keine Workouts zum Anzeigen vorhanden", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    protected void fillTableWithDummies(){
        createDummySquat(30, 25);
        createDummySquat(10, 8);
        createDummySquat(42, 51);
        createDummySquat(30, 29);
    }

    protected  void createDummySquat(int noOfSquats, long durationAsSeconds){
        Squat dummySquat = new Squat();

        dummySquat.setRandomId();
        dummySquat.setCurrentDate(Util.getCurrentDateAsString());
        dummySquat.setDurationInMillis(1000*durationAsSeconds);
        dummySquat.setCalories(100);
        dummySquat.setRepeats(noOfSquats);
        dummySquat.setSquatPerMin((double) (noOfSquats/(60*durationAsSeconds)));

        dummySquat.save();
    }

}
