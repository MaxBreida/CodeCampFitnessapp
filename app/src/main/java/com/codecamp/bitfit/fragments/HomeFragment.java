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
import com.codecamp.bitfit.database.User;
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

        // inflates pushup highscore card with values
        highscorePushups();
        shareToFacebook();
        // inflates bmi card with values
        bmi();
    }

    private void bmi() {
        // initialize bmi views
        bmiHeight = getView().findViewById(R.id.textview_bmi_height);
        bmiWeight = getView().findViewById(R.id.textview_bmi_weight);
        bmiValue = getView().findViewById(R.id.textview_bmi_bmivalue);

        // get user data from database
        User user = Util.findUser();

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
        PushUps highscorePushups = Util.findHighscorePushup();

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
