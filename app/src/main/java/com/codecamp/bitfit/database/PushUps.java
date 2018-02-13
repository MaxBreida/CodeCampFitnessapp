package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */
@Table(database = AppDatabase.class)
public class PushUps extends BaseModel {
    //Declaration variables DBFlow
    // at least one primary key required
    @PrimaryKey
    UUID id;

    @Column
    String currentDate;

    @Column
    long duration;

    @Column
    int repeats;

    @Column
    double pushPerMin;

    @Column
    double calories;

    //Getter and Setter


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

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

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calorie) {
        this.calories = calorie;
    }
}
