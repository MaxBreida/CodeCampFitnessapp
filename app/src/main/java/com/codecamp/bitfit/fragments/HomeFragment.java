package com.codecamp.bitfit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.database.Workout;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import net.steamcrafted.materialiconlib.MaterialIconView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    // initialized user
    private User user = DBQueryHelper.findUser();

    private double bmi;

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get last activity
        lastActivity();

        // inflates highscore cards
        highscorePushups();
        highscoreSquats();
        highscoreRun();

        // inflates bmi card with values
        bmi();

        // initialize share buttons
        initializeShareButton(R.id.button_share_bmi, "Whoa, was für ein BMI!");
        initializeShareButton(R.id.button_share_highscore_pushups, "Oha, hab starke Ärmchen!");
        initializeShareButton(R.id.button_share_highscore_squats, "Niemals den Bein-Tag überspringen!");
        // english obviously sounds better, never skip leg day #memes
        initializeShareButton(R.id.button_share_highscore_run, "Ha, schneller als Sonic!");
        initializeShareButton(R.id.button_share_last_activity, "War wiedermal fleißig!");
    }


    private void lastActivity() {
        // get last activity from shared prefs
        Workout lastActivity = DBQueryHelper.getLastWorkout(getContext());
        // initialize lastActivity view
        TextView lastActivityDuration = getView().findViewById(R.id.textview_lastActivity_duration);
        TextView lastActivityCalories = getView().findViewById(R.id.textview_lastActivity_calories);
        TextView lastActivityPerMinOrSpeed = getView().findViewById(R.id.textview_lastActivity_perMin_or_speed);
        ImageView perMinOrSpeedImageView = getView().findViewById(R.id.image_last_activity_counter);
        TextView lastActivityRepeatsOrDistance = getView().findViewById(R.id.textview_lastActivity_repeats_or_distance);
        ImageView repeatsOrDistanceImageView = getView().findViewById(R.id.image_last_activity_per_min);
        TextView lastActivityDate = getView().findViewById(R.id.textview_lastActivity_date);
        TextView noActivityText = getView().findViewById(R.id.text_last_activity_no_activity);
        View content = getView().findViewById(R.id.cardview_last_activity_constraintlayout);
        ImageView lastActivityIcon = getView().findViewById(R.id.imageview_lastActivity_icon);
        ImageView calendarImage = getView().findViewById(R.id.image_lastActivity_date);

        // set card content
        if(lastActivity == null) {
            content.setVisibility(View.INVISIBLE);
            lastActivityDate.setVisibility(View.INVISIBLE);
            noActivityText.setVisibility(View.VISIBLE);
            calendarImage.setVisibility(View.INVISIBLE);
        }
        else if(lastActivity instanceof PushUps) {
            PushUps pushUps = (PushUps) lastActivity;
            lastActivityCalories.setText(
                    String.format("%s kcal", String.valueOf(pushUps.getCalories())));
            lastActivityDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(pushUps.getDurationInMillis())));
            lastActivityPerMinOrSpeed.setText(
                    String.format("%s P/min", String.valueOf(pushUps.getPushPerMin())));
            lastActivityRepeatsOrDistance.setText(
                    String.format("%s Push-Up(s)", String.valueOf(pushUps.getRepeats())));
            lastActivityDate.setText(String.format(" %s", pushUps.getCurrentDate()));
            lastActivityIcon.setVisibility(View.VISIBLE);
            lastActivityIcon.setImageResource(R.drawable.icon_pushup_color);
        }
        else  if(lastActivity instanceof  Squat) {
            Squat squat = (Squat) lastActivity;
            lastActivityCalories.setText(
                    String.format("%s kcal", String.valueOf(squat.getCalories())));
            lastActivityDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(squat.getDurationInMillis())));
            lastActivityPerMinOrSpeed.setText(
                    String.format("%s S/min", String.valueOf(squat.getSquatPerMin())));
            lastActivityRepeatsOrDistance.setText(
                    String.format("%s Squat(s)", String.valueOf(squat.getRepeats())));
            lastActivityDate.setText(
                    String.format("%s", squat.getCurrentDate()));
            lastActivityIcon.setVisibility(View.VISIBLE);
            lastActivityIcon.setImageResource(R.drawable.icon_squat_color);
        }
        else if (lastActivity instanceof Run) {
            Run run = (Run) lastActivity;
            lastActivityCalories.setText(
                    String.format("%s kcal", String.valueOf(run.getCalories())));
            lastActivityDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(run.getDurationInMillis())));
            lastActivityPerMinOrSpeed.setText(run.getAverageKmhString());
            perMinOrSpeedImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_speed));
            lastActivityRepeatsOrDistance.setText(run.getDistanceInKmString());
            repeatsOrDistanceImageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_distance));
            lastActivityDate.setText(
                    String.format("%s", run.getCurrentDate()));
            lastActivityIcon.setVisibility(View.VISIBLE);
            lastActivityIcon.setImageResource(R.drawable.icon_run_color);
        }
    }

    private void bmi() {
        // initialize bmi views
        TextView bmiHeight = getView().findViewById(R.id.textview_bmi_height);
        TextView bmiWeight = getView().findViewById(R.id.textview_bmi_weight);
        TextView bmiValue = getView().findViewById(R.id.textview_bmi_bmivalue);

        // get user data from database
        if (user != null) {
            // calculate bim
            bmi = calculateBMI(user);

            // set text in textviews
            bmiHeight.setText(String.format("%s m", String.valueOf(Util.getHeightInMeters(user.getSizeInCM()))));
            bmiWeight.setText(String.format("%s kg", String.valueOf(user.getWeightInKG())));
            bmiValue.setText(String.format("BMI: %.2f", bmi));
        }
    }

    private double calculateBMI(User user) {
        return user.getWeightInKG() / Math.pow(Util.getHeightInMeters(user.getSizeInCM()), 2);
    }

    private void highscorePushups() {
        // initialize highscore pushups textviews
        TextView highscorePushupsCalories = getView().findViewById(R.id.textview_highscore_pushup_calories);
        TextView highscorePushupsDate = getView().findViewById(R.id.textview_highscore_pushup_date);
        TextView highscorePushupsDuration = getView().findViewById(R.id.textview_highscore_pushup_duration);
        TextView highscorePushupsPPM = getView().findViewById(R.id.textview_highscore_pushup_ppm);
        TextView highscorePushupsRepeats = getView().findViewById(R.id.textview_highscore_pushup_repeats);
        TextView noHighscoreText = getView().findViewById(R.id.text_highscore_pushups_no_highscore);
        View content = getView().findViewById(R.id.cardview_highscore_pushups_content);
        ImageView calendarImage = getView().findViewById(R.id.image_highscore_pushup_date);

        // find pushup highscore
        PushUps highscorePushups = DBQueryHelper.findHighscorePushup();

        if (highscorePushups.getCurrentDate() != null) {
            // set text in textviews
            highscorePushupsCalories.setText(
                    String.format("%s kcal", String.valueOf(highscorePushups.getCalories())));
            highscorePushupsRepeats.setText(
                    String.format("%s Push-Up(s)", String.valueOf(highscorePushups.getRepeats())));
            highscorePushupsPPM.setText(
                    String.format("%s P/min", String.valueOf(highscorePushups.getPushPerMin())));
            highscorePushupsDate.setText(
                    String.format("%s", highscorePushups.getCurrentDate()));
            highscorePushupsDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(highscorePushups.getDurationInMillis())));
        } else {
            noHighscoreText.setVisibility(View.VISIBLE);
            content.setVisibility(View.INVISIBLE);
            highscorePushupsDate.setVisibility(View.INVISIBLE);
            calendarImage.setVisibility(View.INVISIBLE);
        }
    }

    private void highscoreSquats() {
        // initialize highscore pushups textviews
        TextView highscoreSquatsCalories = getView().findViewById(R.id.textview_highscore_squats_calories);
        TextView highscoreSquatsDate = getView().findViewById(R.id.textview_highscore_squats_date);
        TextView highscoreSquatsDuration = getView().findViewById(R.id.textview_highscore_squats_duration);
        TextView highscoreSquatsSPM = getView().findViewById(R.id.textview_highscore_squats_spm);
        TextView highscoreSquatsRepeats = getView().findViewById(R.id.textview_highscore_squats_repeats);
        TextView noHighscoreText = getView().findViewById(R.id.text_highscore_squats_no_highscore);
        View content = getView().findViewById(R.id.cardview_highscore_squats_content);
        ImageView calendarImage = getView().findViewById(R.id.image_highscore_squats_date);

        // find pushup highscore
        Squat highscoreSquats = DBQueryHelper.findHighscoreSquat();

        if (highscoreSquats.getCurrentDate() != null) {
            // set text in textviews
            highscoreSquatsCalories.setText(
                    String.format("%s kcal", String.valueOf(highscoreSquats.getCalories())));
            highscoreSquatsRepeats.setText(
                    String.format("%s Squat(s)", String.valueOf(highscoreSquats.getRepeats())));
            highscoreSquatsSPM.setText(
                    String.format("%s S/min", String.valueOf(highscoreSquats.getSquatPerMin())));
            highscoreSquatsDate.setText(
                    String.format("%s", highscoreSquats.getCurrentDate()));
            highscoreSquatsDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(highscoreSquats.getDurationInMillis())));
        } else {
            noHighscoreText.setVisibility(View.VISIBLE);
            content.setVisibility(View.INVISIBLE);
            highscoreSquatsDate.setVisibility(View.INVISIBLE);
            calendarImage.setVisibility(View.INVISIBLE);
        }
    }

    private void highscoreRun() {

        // initialize highscore run
        TextView highscoreRunCalories = getView().findViewById(R.id.textview_highscore_run_calories);
        TextView highscoreRunDate = getView().findViewById(R.id.textview_highscore_run_date);
        TextView highscoreRunDuration = getView().findViewById(R.id.textview_highscore_run_duration);
        TextView highscoreRunSpeed = getView().findViewById(R.id.textview_highscore_run_speed);
        TextView highscoreRunDistance = getView().findViewById(R.id.textview_highscore_run_distance);
        TextView noHighscoreText = getView().findViewById(R.id.text_highscore_run_no_highscore);
        View content = getView().findViewById(R.id.cardview_highscore_run_content);
        ImageView calendarImage = getView().findViewById(R.id.image_highscore_run_date);

        Run highScoreRun = DBQueryHelper.findHighScoreRun();

        if (highScoreRun.getCurrentDate() != null) {
            highscoreRunCalories.setText(
                    String.format("%.2f kcal", highScoreRun.getCalories())
            );
            highscoreRunDate.setText(
                    highScoreRun.getCurrentDate()
            );
            highscoreRunDuration.setText(
                    String.format("%s min", Util.getMillisAsTimeString(highScoreRun.getDurationInMillis()))
            );
            highscoreRunDistance.setText(highScoreRun.getDistanceInKmString());
            highscoreRunSpeed.setText(highScoreRun.getAverageKmhString());
        } else {
            noHighscoreText.setVisibility(View.VISIBLE);
            content.setVisibility(View.INVISIBLE);
            highscoreRunDate.setVisibility(View.INVISIBLE);
            calendarImage.setVisibility(View.INVISIBLE);
        }
    }

    View.OnClickListener shareParentCardViewOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View shareButton) {
            View targetView = shareButton;
            // gets parent view till it reaches the desired CardView object
            while(!targetView.getClass().equals(CardView.class)){
                targetView = (View) targetView.getParent();
            }

            Util.shareViewOnClick(getActivity(), targetView, shareButton.getContentDescription().toString());
        }
    };

    // using the content description to set the text that should be shared by clicking the button
    private void initializeShareButton(int butRes, String msg) {
        String spam = "\nHol dir jetzt die BitFit app auf PirateBay und zeige auch du deinen Freunden wie cool du bist!";
        MaterialIconView shareButton = getView().findViewById(butRes);
        shareButton.setContentDescription(msg + spam);
        shareButton.setOnClickListener(shareParentCardViewOnClick);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.home));
    }
}
