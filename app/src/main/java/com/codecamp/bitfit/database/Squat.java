package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */

public class Squat {
    //Declaration variables DBFlow
    @PrimaryKey // at least one primary key required
            UUID id;

    @Column
    Date currentDate;

    @Column
    Double calorie;

    @Column
    Double duration;

    @Column
    Integer squatPerMin;

    //Getter and Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getSquatPerMin() {
        return squatPerMin;
    }

    public void setSquatPerMin(Integer squatPerMin) {
        this.squatPerMin = squatPerMin;
    }
}
