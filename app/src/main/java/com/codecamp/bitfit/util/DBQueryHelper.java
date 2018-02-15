package com.codecamp.bitfit.util;

/**
 * Created by MaxBreida on 16.02.18.
 */

import android.content.Context;

import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.PushUps_Table;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.Run_Table;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.Squat_Table;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.database.Workout;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.max;

/**
 * Class which holds all methods for db queries
 */
public class DBQueryHelper {

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
    }

    /**
     * get last workout from database
     * @param context is necessary for shared preferences
     * @return the last workout object from the database
     */
    public static Workout getLastWorkout(Context context) {
        SharedPrefsHelper sharedPrefsHelper = new SharedPrefsHelper(context);
        switch (sharedPrefsHelper.getLastActivityType()) {
            case Constants.WORKOUT_PUSHUPS:
                return SQLite.select()
                        .from(PushUps.class)
                        .where(PushUps_Table.id.eq(sharedPrefsHelper.getLastActivityUuid()))
                        .querySingle();
            case Constants.WORKOUT_RUN:
                return SQLite.select()
                        .from(Run.class)
                        .where(Run_Table.id.eq(sharedPrefsHelper.getLastActivityUuid()))
                        .querySingle();
            case Constants.WORKOUT_SQUATS:
                return SQLite.select()
                        .from(Squat.class)
                        .where(Squat_Table.id.eq(sharedPrefsHelper.getLastActivityUuid()))
                        .querySingle();
            default: return null;
        }
    }
}
