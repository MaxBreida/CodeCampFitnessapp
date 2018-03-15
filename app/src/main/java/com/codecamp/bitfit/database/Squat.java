package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

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
