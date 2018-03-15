package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaxBreida on 15.02.18.
 */

public class LastPoints extends BaseModel {

    @PrimaryKey
    int id = 1445;

    @Column
    List<Double> points;

    public List<Double> getPoints(){
        return points;
    };

    public void setPoints(List<Double> p){
        points = new ArrayList<>();
        points.addAll(p);
    };
}
