package com.codecamp.bitfit.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    int sizeInCM;

    @Column
    double weightInKG;

    @Column
    Date birthday;

    @Column
    String gender;

    List<PushUps> pushUps;

    List<Squat> squats;

    List<Run> runs;

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

    public int getSizeInCM() {
        return sizeInCM;
    }

    public void setSizeInCM(int sizeInCM) {
        this.sizeInCM = sizeInCM;
    }

    public double getWeightInKG() { return weightInKG; }

    public double getWeightInLbs() { return weightInKG * 2.2046226218487755; }

    public void setWeightInKG(double weightInKG) {
        this.weightInKG = weightInKG;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isMale() { return !isFemale(); }

    public boolean isFemale(){ return gender.startsWith("w"); }

    public int getAge() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.US);
        // setting locale to US to ensure that we get ASCII digits instead of (e.g.) arabic digits
        Date curDate = Calendar.getInstance().getTime();
        int curD = Integer.parseInt(df.format(curDate));
        int bDay = Integer.parseInt(df.format(birthday));
        int age = ((curD - bDay) / 10000);
        return age + 1900;
    }

    // One to Many relataion, if you have more than one User
    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "pushUps")
    public List<PushUps> getMyPushUps() {
        if (pushUps == null || pushUps.isEmpty()) {
            pushUps = SQLite.select()
                    .from(PushUps.class)
                    .where(PushUps_Table.user_id.eq(id))
                    .queryList();
        }
        return pushUps;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "squats")
    public List<Squat> getMySquats() {
        if (squats == null || squats.isEmpty()) {
            squats = SQLite.select()
                    .from(Squat.class)
                    .where(Squat_Table.user_id.eq(id))
                    .queryList();
        }
        return squats;
    }

    @OneToMany(methods = {OneToMany.Method.ALL}, variableName = "runs")
    public List<Run> getMyRuns() {
        if (runs == null || runs.isEmpty()) {
            runs = SQLite.select()
                    .from(Run.class)
                    .where(Run_Table.user_id.eq(id))
                    .queryList();
        }
        return runs;
    }
}
