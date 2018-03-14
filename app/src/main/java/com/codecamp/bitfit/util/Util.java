package com.codecamp.bitfit.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * @param millis milliseconds
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
     * @param heightInCM the height in CM that shall be converted to meters
     * @return height in meters
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
     * @param date the date as a Date object
     * @return date as string in german date format
     */
    public static String getDateAsString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.GERMANY);
        return dateFormat.format(date);
    }

    /**
     * converts a string to a date object
     * @param string the date as a string
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
     * @param d the decimal value that shall be rounded
     * @return rounded value
     */
    public static double roundTwoDecimals(double d) {
        return  Math.round(d * 100) / 100.0;
    }
    public static float roundTwoDecimals(float d) {
        return  Math.round(d * 100) / 100.0f;
    }

    /** TODO: if it's not needed in any other method it can be put into the run fragment
     * Returns a given number as a string and that with a set amount of decimal places.
     * @param num the initial number
     * @param precision determines how many decimal places the returned string number should have
     * @return string of the number with the preferred amount of decimal places
     */
    public static String decNumToXPrecisionString(Number num, int precision){
        String format = "%.".concat(String.valueOf(precision)).concat("f");
        return String.format(java.util.Locale.US, format, num);
        // US locale to ensure that we get a "." (dot) notation instead of a "," (comma) notation
    }

    /**
     * Converts a view to a bitmap image.
     * @param view the view that shall be converted
     * @return the bitmap image
     */
    public static Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /**
     * creates a bitmap from the view that was passed to it and then passes it to the sharing method
     * @param activity context
     * @param shareView view to get shared
     * @param shareMessage the message passed with the view
     */
    public static void shareViewOnClick(Activity activity, View shareView, String shareMessage) {
        Bitmap bitmap = Util.viewToBitmap(shareView);

        shareBitmap(activity, bitmap, shareMessage);
    }

    /**
     * starts a sharing intent and lets the user share a bitmap picture with a predefined message
     * @param activity context
     * @param bitmap bitmap that shall be shared
     * @param shareMessage the predefined message that will be shared with the picture
     */
    public static void shareBitmap(Activity activity, Bitmap bitmap, String shareMessage) {
        // get cache file dir
        File cachePath = new File(activity.getCacheDir(), "images");
        cachePath.mkdirs();

        // save image in cache folder
        try {
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // share image
        File imagePath = new File(activity.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(activity, "com.codecamp.bitfit.fileprovider", newFile);
        if(contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, activity.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            activity.startActivity(Intent.createChooser(shareIntent, "Teile deinen Status via..."));
        }
    }

    /**
     * Creates an AlertDialog to be shown after a workout.
     *
     * @param activity the activity for context
     * @param workout the workout to be shared
     * @param customDialogLayout the contentview
     * @param positiveListener listener for positive button
     * @param negativeListener listener for negative button
     * @return WorkoutCompleteDialog
     */
    public static AlertDialog getWorkoutCompleteDialog(Activity activity,
                                                Workout workout,
                                                View customDialogLayout,
                                                DialogInterface.OnClickListener positiveListener,
                                                DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // set the textview contents
        if(workout instanceof Run) {
            setTextViews((Run) workout, customDialogLayout);
        } else if (workout instanceof PushUps){
            setTextViews((PushUps) workout, customDialogLayout);
        } else {
            setTextViews((Squat) workout, customDialogLayout);
        }

        builder.setView(customDialogLayout);
        builder.setPositiveButton("Workout Teilen", positiveListener);
        builder.setNegativeButton("Schließen", negativeListener);
        builder.setCancelable(false);
        return builder.create();
    }

    // helper methods for getWorkoutCompleteDialog method
    private static void setTextViews(Squat squat, View customDialog) {
        TextView caloriesText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_calories);
        TextView durationText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_duration);
        TextView perminText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_permin);
        TextView countText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_repeats);

        caloriesText.setText(String.format("%.2f kcal", squat.getCalories()));
        durationText.setText(String.format("%s min", Util.getMillisAsTimeString(squat.getDurationInMillis())));
        perminText.setText(String.format("%.2f S/min", squat.getSquatPerMin()));
        countText.setText(String.format("%d Squat(s)", squat.getRepeats()));
    }

    private static void setTextViews(PushUps pushUp, View customDialog) {
        TextView caloriesText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_calories);
        TextView durationText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_duration);
        TextView perminText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_permin);
        TextView countText = customDialog.findViewById(R.id.textview_dialog_repetition_workout_repeats);

        caloriesText.setText(String.format("%.2f kcal", pushUp.getCalories()));
        durationText.setText(String.format("%s min", Util.getMillisAsTimeString(pushUp.getDurationInMillis())));
        perminText.setText(String.format("%.2f P/min", pushUp.getPushPerMin()));
        countText.setText(String.format("%d Push-Up(s)", pushUp.getRepeats()));

    }

    private static void setTextViews(Run run, View customDialog) {
        TextView caloriesText = customDialog.findViewById(R.id.textview_dialog_run_workout_calories);
        TextView durationText = customDialog.findViewById(R.id.textview_dialog_run_workout_duration);
        TextView speedText = customDialog.findViewById(R.id.textview_dialog_run_workout_speed);
        TextView distanceText = customDialog.findViewById(R.id.textview_dialog_run_workout_distance);

        caloriesText.setText(String.format("%.2f kcal", run.getCalories()));
        durationText.setText(String.format("%s min", Util.getMillisAsTimeString(run.getDurationInMillis())));
        speedText.setText(String.format("%.2f km/h", run.getAverageKmh()));
        distanceText.setText(String.format("%.2f km", run.getDistanceInKm()));

    }

}
