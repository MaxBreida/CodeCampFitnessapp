package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;

/**
 * Created by maxib on 11.03.2018.
 */

/**
 * This class helps us to drastically reduce code repetition in pushup/squat statistics
 */
public class WorkoutWithRepetitions extends Workout {
    @Column
    int repeats;

    public int getRepeats() {
        return repeats;
    }

    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }

}
