package com.codecamp.bitfit.database;

import com.codecamp.bitfit.util.Util;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */
@Table(database = AppDatabase.class)

public class Run extends Workout {
    @Column
    float distance;

    // Getter and Setter
    public float getDistanceInKm() {
        return distance / 1000;
    }

    public String getDistanceInKmString() {
        return Util.decNumToXPrecisionString(getDistanceInKm(), 2) + " km";
    }

    public void setDistanceInMeters(float distance) {
        this.distance = Util.roundTwoDecimals(distance);
    }

    public double getAverageKmh() {
        return getDistanceInKm() * 3600000 / durationInMillis ;
        // optimized calculation for dividing durationInMillis by 1000 to get seconds,
        // by 60 to get minutes and another 60 to get hours (=) 3600000 and finally get km / h
    }

    public String getAverageKmhString(){
        return Util.decNumToXPrecisionString(getAverageKmh(),2) + " km/h";
    }
}
