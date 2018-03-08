package com.codecamp.bitfit.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMANY);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * generates a string of any given date object
     * @return date as string in german date format
     */
    public static String getDateAsString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMANY);
        return dateFormat.format(date);
    }

    /**
     * convers a string to a date object
     * @param string
     * @return date
     */
    public static Date getStringAsDate(String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMANY);
        Date date;
        try {
            date = dateFormat.parse(string);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Does what the method title says
     * @param d
     * @return rounded double value
     */
    public static double roundTwoDecimals(double d) {
        d = Math.round(d * 100);
        d = d/100;
        return d;
    }

    // TODO: forbid the usage of anything other than float or double (I forgot how to do it elegantly, gotta google)
    public static <T> String decNumToXPrecisionString(T num, int precision){
        // US locale to ensure that we get a "." (dot) notation instead of a "," (comma) notation
        String format = "%.".concat(String.valueOf(precision)).concat("f");
        return String.format(java.util.Locale.US, format, num);
    }

    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
