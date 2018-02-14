package com.codecamp.bitfit.util;

import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.PushUps_Table;
import com.codecamp.bitfit.database.User;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

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
     *  queries the database for the pushup with the highest repeat count
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
         // find adult users
        User query = SQLite.select()
                .from(User.class)
                .querySingle();
        return query;
    }
}
