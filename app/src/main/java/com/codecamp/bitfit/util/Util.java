package com.codecamp.bitfit.util;

import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.PushUps_Table;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.Squat_Table;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.Run_Table;
import com.codecamp.bitfit.database.User;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.raizlabs.android.dbflow.sql.language.Method.max;

/**
 * Created by MaxBreida on 14.02.18.
 */

/**
 * This class hold static methods for basic operations (databse queries and String conversions etc.)
 * to minimize code repetition
 */
public class Util {

    /**
     * convert milliseconds to human readable string
     *
     * @param millis
     * @return String in minute:seconds format
     */
    public static String getMillisAsTimeString(long millis) {
        return String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    /**
     * queries the database for the pushup with the highest repeat count
     *
     * @return highscore pushup
     */
    public static PushUps findHighscorePushup() {
        // find highscore from pushups by selecting max value from repeats
        PushUps query = SQLite.select(Method.ALL_PROPERTY, max(PushUps_Table.repeats))
                .from(PushUps.class)
                .querySingle();
        return query;
    }

    /**
     *  queries the database for the squats with the highest repeat count
     *
     * @return highscore squat
     */
    public static Squat findHighscoreSquat() {
        // find highscore from pushups by selecting max value from repeats
        Squat query = SQLite.select(Method.ALL_PROPERTY, max(Squat_Table.repeats))
                .from(Squat.class)
                .querySingle();

        return query;
    }

    /**
     *  queries the database for the squats with the highest distance value
     *
     * @return highscore run
     */
    public static Run findHighScoreRun(){
        // find highscore from runs by selecting max value from distance
        Run query = SQLite.select(Method.ALL_PROPERTY, max(Run_Table.distance))
                .from(Run.class)
                .querySingle();
        return query;
    }

    /**
     * calculates height in m from height in cm
     *
     * @param heightInCM
     * @return height in m
     */
    public static double getHeightInMeters(int heightInCM) {
        return (double) heightInCM / 100;
    }

    /*
    *  queries the database for the User
    *  *
    * @return current user
    */
    public static User findUser() {
        User query = SQLite.select()
                .from(User.class)
                .querySingle();
        return query;
    }


    /*
    *  queries the database for the Squats
    *  *
    * @return all Squats
    */
    public static List<Squat> findAllSquats() {
        List<Squat> query = SQLite.select()
                .from(Squat.class)
                .queryList();
        return query;

/*      //give you all squats
        List<Squat> allSquat = new ArrayList<Squat>();
        for (Squat squat : allSquat = Util.findAllSquats()) {
            squat.getId();
        }
        */
    }
}
