package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */

@Table(database = AppDatabase.class)
public class Squat extends WorkoutWithRepetitions {

    @Column
    double squatPerMin;

    //Getter and Setter
    public Double getSquatPerMin() {
        return squatPerMin;
    }

    public void setSquatPerMin(Double squatPerMin) {
        this.squatPerMin = squatPerMin;
    }
}
