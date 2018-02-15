package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */
@Table(database = AppDatabase.class)
public class PushUps extends Workout {

    @Column
    int repeats;

    @Column
    double pushPerMin;

    //Getter and Setter
    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

    public double getPushPerMin() {
        return pushPerMin;
    }

    public void setPushPerMin(double pushPerMin) {
        this.pushPerMin = pushPerMin;
    }
}
