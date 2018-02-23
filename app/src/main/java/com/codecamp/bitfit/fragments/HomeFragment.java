package com.codecamp.bitfit.fragments;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.database.Workout;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    // initialized user
    private User user = DBQueryHelper.findUser();
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
    private double bmi;

    // bmi share to Facebook
    private MaterialIconView shareBMIButton;
    private int genderUrlFormat = 1;

    // highsore pushups share to Facebook
    private MaterialIconView shareHighScorePushUpsButton;
    private CardView cardview_highscore_pushups;

    // highscore Squat share to Facebook
    private MaterialIconView shareHighScoreSquatsButton;
    private CardView cardview_highscore_squats;

    // highscore Runnin share to Facebook
    private MaterialIconView shareHighScoreRunButton;
    private CardView cardview_highscore_Run;

    // share last activity to Facebook
    private MaterialIconView shareLastActivityButton;
    private CardView cardview_last_activity;

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

        // inflates bmi card with values
        bmi();

        // initialized Facebook share buttons
        shareBMIToFacebook();
        shareHighScorePushUpsToFacebook();
        shareHighScoreSquatToFacebook();
        shareHighScoreRunToFacebook();
        shareLastActivityToFacebook();


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


        if (user != null) {
            // calculate bim
            bmi = calculateBMI(user);

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
                    String.format("Dauer: %s", Util.getMillisAsTimeString(highscorePushups.getDurationInMillis())));
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
                    String.format("Dauer: %s", Util.getMillisAsTimeString(highscoreSquats.getDurationInMillis())));
        }
    }

    private void highscoreRun() {

        // initialize highscore run
        highscoreRunCalories = getView().findViewById(R.id.textview_highscore_run_calories);
        highscoreRunDate = getView().findViewById(R.id.textview_highscore_run_date);
        highscoreRunDuration = getView().findViewById(R.id.textview_highscore_run_duration);
        highscoreRunSpeed = getView().findViewById(R.id.textview_highscore_run_speed);
        highscoreRunDistance = getView().findViewById(R.id.textview_highscore_run_distance);

        Run highScoreRun = DBQueryHelper.findHighScoreRun();

        if (highScoreRun != null) {
            highscoreRunCalories.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highScoreRun.getCalories()))
            );
            highscoreRunDate.setText(
                    String.format("Verbrauchte Kalorien: %s", String.valueOf(highScoreRun.getCurrentDate()))
            );
            highscoreRunDuration.setText(
                    String.format("Dauer: %s", String.valueOf(highScoreRun.getDurationInMillis()))
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
    private void shareBMIToFacebook() {

        shareBMIButton = getView().findViewById(R.id.button_share_bmi);
        shareBMIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (user.getGender() != "männlich") {
                    genderUrlFormat = 0;
                }

                String urlToBmiCalculator = "https://de.smartbmicalculator.com/ergebnis.html?unit=0&hc=" + user.getSize() + "&wk=" + user.getWeight() + "&us=" + genderUrlFormat + "&ua=" + user.getAge() + "&gk=";
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(urlToBmiCalculator))
                        .setQuote(user.getName() + " BMI: " + Double.toString(Math.rint(bmi * 100) / 100))
                        .build();

                ShareDialog.show(getActivity(), content);
                shareBMIButton.setColor(Color.parseColor("#DDDDDD"));
            }
        });

    }

    private void shareHighScorePushUpsToFacebook() {
        shareHighScorePushUpsButton = getView().findViewById(R.id.button_share_highscore_pushups);
        cardview_highscore_pushups = getView().findViewById(R.id.cardview_highscore_pushups);

        shareHighScorePushUpsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = viewToBitmap(cardview_highscore_pushups);

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog.show(getActivity(), content);
                shareHighScorePushUpsButton.setColor(Color.parseColor("#DDDDDD"));
            }
        });
    }

    private void shareHighScoreSquatToFacebook() {
        shareHighScoreSquatsButton = getView().findViewById(R.id.button_share_highscore_squats);
        cardview_highscore_squats = getView().findViewById(R.id.cardview_highscore_squats);

        shareHighScoreSquatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = viewToBitmap(cardview_highscore_squats);

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog.show(getActivity(), content);
                shareHighScoreSquatsButton.setColor(Color.parseColor("#DDDDDD"));
            }
        });
    }

    private void shareHighScoreRunToFacebook() {
        shareHighScoreRunButton = getView().findViewById(R.id.button_share_highscore_run);
        cardview_highscore_Run = getView().findViewById(R.id.cardview_highscore_run);

        shareHighScoreRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = viewToBitmap(cardview_highscore_Run);

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog.show(getActivity(), content);
                shareHighScoreRunButton.setColor(Color.parseColor("#DDDDDD"));
            }
        });
    }

    private void shareLastActivityToFacebook() {

        shareLastActivityButton = getView().findViewById(R.id.button_share_last_activity);
        cardview_last_activity = getView().findViewById(R.id.cardview_last_activity);

        shareLastActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = viewToBitmap(cardview_last_activity);

                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(bitmap)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();

                ShareDialog.show(getActivity(), content);
                shareLastActivityButton.setColor(Color.parseColor("#DDDDDD"));
            }
        });
    }

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.home));
    }
}
