package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */

@Table(database = AppDatabase.class)

public class User extends BaseModel {
    //Declaration variables DBFlow
    @PrimaryKey // at least one primary key required
            UUID id;

    @Column
    String name;

    @Column
    int size;

    @Column
    double weight;

    @Column
    Date birthday;

    @Column
    String gender;

    //Getter and Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date curDate = Calendar.getInstance().getTime();
        int curD = Integer.parseInt(df.format(curDate));
        int bday = Integer.parseInt(df.format(birthday));
        int age = ((curD - bday) / 10000);
        return age;
    }

}
