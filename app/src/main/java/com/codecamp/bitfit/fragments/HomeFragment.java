package com.codecamp.bitfit.fragments;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.max;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // highscore pushups views
    private TextView highscorePushupsDuration;
    private TextView highscorePushupsCalories;
    private TextView highscorePushupsPPM;
    private TextView highscorePushupsRepeats;
    private TextView highscorePushupsDate;

    // highscore squats views
    private TextView highscoreSquatsDuration;
    private TextView highscoreSquatsCalories;
    private TextView highscoreSquatsSPM;
    private TextView highscoreSquatsRepeats;
    private TextView highscoreSquatsDate;

    // highscore Runs views
    private TextView highscoreRunDuration;
    private TextView highscoreRunCalories;
    private TextView highscoreRunSpeed;
    private TextView highscoreRunDistance;
    private TextView highscoreRunDate;

    // bmi views
    private TextView bmiHeight;
    private TextView bmiWeight;
    private TextView bmiValue;

    private MaterialIconView shareBMIButton;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private LoginManager loginManager;


    public static HomeFragment getInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // get last activity
        lastActivity();
        // inflates pushup highscore card with values
        highscorePushups();
        shareToFacebook();
        // inflates bmi card with values
        bmi();
    }

    private void lastActivity() {
        // get last activity from shared prefs
        Workout lastActivity = DBQueryHelper.getLastWorkout(getContext());

        // inflate the card TODO
    }

    private void bmi() {
        // initialize bmi views
        bmiHeight = getView().findViewById(R.id.textview_bmi_height);
        bmiWeight = getView().findViewById(R.id.textview_bmi_weight);
        bmiValue = getView().findViewById(R.id.textview_bmi_bmivalue);

        // get user data from database
        User user = DBQueryHelper.findUser();

        if (user != null) {
            // calculate bim
            double bmi = calculateBMI(user);

            // set text in textviews
            bmiHeight.setText(String.format("Größe: %s", String.valueOf(Util.getHeightInMeters(user.getSize()))));
            bmiWeight.setText(String.format("Gewicht: %s", String.valueOf(user.getWeight())));
            bmiValue.setText(String.format("BMI: %.2f", bmi));
        }
    }

    private double calculateBMI(User user) {
        return user.getWeight() / Math.pow(Util.getHeightInMeters(user.getSize()), 2);
    }

    private void highscorePushups() {
        // initialize highscore pushups textviews
        highscorePushupsCalories = getView().findViewById(R.id.textview_highscore_pushup_calories);
        highscorePushupsDate = getView().findViewById(R.id.textview_highscore_pushup_date);
        highscorePushupsDuration = getView().findViewById(R.id.textview_highscore_pushup_duration);
        highscorePushupsPPM = getView().findViewById(R.id.textview_highscore_pushup_ppm);
        highscorePushupsRepeats = getView().findViewById(R.id.textview_highscore_pushup_repeats);

        // find pushup highscore
        PushUps highscorePushups = DBQueryHelper.findHighscorePushup();

        if (highscorePushups != null) {
            // set text in textviews
            highscorePushupsCalories.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highscorePushups.getCalories())));
            highscorePushupsRepeats.setText(
                    String.format("Wiederholungen: %s", String.valueOf(highscorePushups.getRepeats())));
            highscorePushupsPPM.setText(
                    String.format("PushUps/min: %s", String.valueOf(highscorePushups.getPushPerMin())));
            highscorePushupsDate.setText(
                    String.format("Datum: %s", highscorePushups.getCurrentDate()));
            highscorePushupsDuration.setText(
                    String.format("Dauer: %s", Util.getMillisAsTimeString(highscorePushups.getDuration())));
        }
    }

    private void highscoreSquats() {
        // initialize highscore pushups textviews
        highscoreSquatsCalories = getView().findViewById(R.id.textview_highscore_pushup_calories);
        highscoreSquatsDate = getView().findViewById(R.id.textview_highscore_pushup_date);
        highscoreSquatsDuration = getView().findViewById(R.id.textview_highscore_pushup_duration);
        highscoreSquatsSPM = getView().findViewById(R.id.textview_highscore_pushup_ppm);
        highscoreSquatsRepeats = getView().findViewById(R.id.textview_highscore_pushup_repeats);

        // find pushup highscore
        Squat highscoreSquats = DBQueryHelper.findHighscoreSquat();

        if (highscoreSquats != null) {
            // set text in textviews
            highscoreSquatsCalories.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highscoreSquats.getCalories())));
            highscoreSquatsRepeats.setText(
                    String.format("Wiederholungen: %s", String.valueOf(highscoreSquats.getRepeats())));
            highscoreSquatsSPM.setText(
                    String.format("PushUps/min: %s", String.valueOf(highscoreSquats.getSquatPerMin())));
            highscoreSquatsDate.setText(
                    String.format("Datum: %s", highscoreSquats.getCurrentDate()));
            highscoreSquatsDuration.setText(
                    String.format("Dauer: %s", Util.getMillisAsTimeString(highscoreSquats.getDuration())));
        }
    }

    private void highscoreRun(){

        // initialize highscore run
        highscoreRunCalories = getView().findViewById(R.id.textview_highscore_run_calories);
        highscoreRunDate = getView().findViewById(R.id.textview_highscore_run_date);
        highscoreRunDuration = getView().findViewById(R.id.textview_highscore_run_duration);
        highscoreRunSpeed = getView().findViewById(R.id.textview_highscore_run_speed);
        highscoreRunDistance = getView().findViewById(R.id.textview_highscore_run_distance);

        Run highScoreRun = DBQueryHelper.findHighScoreRun();

        if(highScoreRun != null){
            highscoreRunCalories.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highScoreRun.getCalories()))
            );
            highscoreRunDate.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highScoreRun.getCurrentDate()))
            );
            highscoreRunDuration.setText(
                    String.format("Dauer: %s", String.valueOf(highScoreRun.getDuration()))
            );
            highscoreRunDistance.setText(
                    String.format("Dauer: %s", String.valueOf(highScoreRun.getDistance()))
            );
            highscoreRunSpeed.setText(
                    String.format("Dauer: %s", String.valueOf(highScoreRun.getSpeed()))
            );
        }
    }

    // Share stuff to Facebook
    private void shareToFacebook() {

        shareBMIButton = getView().findViewById(R.id.button_share_bmi);
  //      shareDialog = new ShareDialog(this);

        shareBMIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
