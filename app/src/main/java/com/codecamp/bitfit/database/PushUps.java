package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */
@Table(database = AppDatabase.class)
public class PushUps extends WorkoutWithRepetitions {

    @Column
    double pushPerMin;

    //Getter and Setter
    public double getPushPerMin() {
        return pushPerMin;
    }

    public void setPushPerMin(double pushPerMin) {
        this.pushPerMin = pushPerMin;
    }
}
