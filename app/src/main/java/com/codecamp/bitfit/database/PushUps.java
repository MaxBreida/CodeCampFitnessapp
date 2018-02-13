package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;

import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */

public class PushUps {
    //Declaration variables DBFlow
    @PrimaryKey // at least one primary key required
            UUID id;

    @Column
    String currentDate;

    @Column
    Double duration;

    @Column
    Integer repeats;

    @Column
    Integer pushPerMin;

    @Column
    Double calorie;

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

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Integer getRepeats() {
        return repeats;
    }

    public void setRepeats(Integer repeats) {
        this.repeats = repeats;
    }

    public Integer getPushPerMin() {
        return pushPerMin;
    }

    public void setPushPerMin(Integer pushPerMin) {
        this.pushPerMin = pushPerMin;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }
}
