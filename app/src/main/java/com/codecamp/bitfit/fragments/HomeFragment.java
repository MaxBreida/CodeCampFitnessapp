package com.codecamp.bitfit.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.database.Workout;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

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

    // highsore share to Facebook
    private MaterialIconView shareHighScorePushUpsButton;
    private CardView cardview_highscore_pushups;
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
        shareBMIToFacebook();
        shareHighScorePushUpsToFacebook();
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
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);

                    }
                }

                Bitmap bitmap = viewToBitmap(cardview_highscore_pushups);
                try {
                    FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "/111.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}
