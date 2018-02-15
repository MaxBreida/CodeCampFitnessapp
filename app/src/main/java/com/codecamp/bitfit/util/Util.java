package com.codecamp.bitfit.util;

import android.content.Context;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.PushUps_Table;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.Squat_Table;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.Run_Table;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.database.Workout;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.raizlabs.android.dbflow.sql.language.Method.max;

/**
 * Created by MaxBreida on 14.02.18.
 */

/**
 * This class hold static methods for basic operations  (String conversions etc.)
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
     * calculates height in m from height in cm
     *
     * @param heightInCM
     * @return height in m
     */
    public static double getHeightInMeters(int heightInCM) {
        return (double) heightInCM / 100;
    }

    /**
     * generates a string of the current date
     * @return date as string in german date format
     */
    public static String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        return dateFormat.format(calendar.getTime());
    }
}
