package com.codecamp.bitfit.fragments;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.OnDialogInteractionListener;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.PushupStatisticsActivity;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.InstructionsDialog;
import com.codecamp.bitfit.util.SharedPrefsHelper;
import com.codecamp.bitfit.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class PushUpFragment extends WorkoutFragment implements SensorEventListener, OnDialogInteractionListener {

    // current User
    User user = DBQueryHelper.findUser();

    // View stuff
    private View container;
    private TextView timeTextView;
    private FloatingActionButton finishButton;
    private TextView pushUpButton;
    private TextView caloriesTextView;
    private TextView avgPushupsTextView;

    // fragment stuff
    private boolean workoutStarted;
    private int count;
    private CountUpTimer countUpTimer;
    private PushUps pushUp;
    private long startTime;
    private long elapsedTime;

    // sensor stuff
    private SensorManager mSensorManager;
    private Sensor mLight;
    private float maxLightRange;
    private double minLightRange;
    private double averageLightRange;
    private boolean lockPushUpsCount;

    //Values for calorie calculation
    double weightPushed;
    double heightPushed;


    public static PushUpFragment getInstance() {
        PushUpFragment fragment = new PushUpFragment();

        return fragment;
    }

    public PushUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_push_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // light sensor initialization
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // find view stuff
        TextView avgTitleTextView = getView().findViewById(R.id.textview_cardview_avg_title);
        avgTitleTextView.setText(getString(R.string.pushups_per_min));

        timeTextView = getView().findViewById(R.id.textview_cardview_time);
        finishButton = getView().findViewById(R.id.button_pushup_quit);
        pushUpButton = getView().findViewById(R.id.button_pushup);
        avgPushupsTextView = getView().findViewById(R.id.textview_cardview_avg);
        caloriesTextView = getView().findViewById(R.id.textview_cardview_calories);
        container = getView().findViewById(R.id.container_pushup_counter);

        countUpTimer = new CountUpTimer(1000, timeTextView) {
            @Override
            public void onTick(long elapsedTime) {
                setElapsedTime(elapsedTime);
                avgPushupsTextView.setText(String.valueOf(calcPushupsPerMinute(elapsedTime)));
                timeTextView.setText(Util.getMillisAsTimeString(elapsedTime));
            }
        };

        // set button to start state
        setToInitialState();

        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show quit button and cardview if we started push ups
                if (!workoutStarted) {
                    container.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.VISIBLE);
                    workoutStarted = true;
                    countUpTimer.start();
                    startTime = System.currentTimeMillis();
                    callback.workoutInProgress(true);
                } else {
                    // increment count and set text
                    count++;
                    avgPushupsTextView.setText(String.valueOf(calcPushupsPerMinute(elapsedTime)));
                    caloriesTextView.setText(String.valueOf(calcCalories()));
                }
                // Screen keep on Flag set
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                pushUpButton.setText(String.valueOf(count));
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopWorkout();
            }
        });
    }

    private void setToInitialState() {
        pushUp = new PushUps();
        startTime = 0;
        workoutStarted = false;
        container.setVisibility(View.INVISIBLE);
        finishButton.setVisibility(View.INVISIBLE);
        pushUpButton.setText(R.string.start);
        timeTextView.setText(R.string.default_timer_value);
        caloriesTextView.setText(R.string.default_double_value);
        avgPushupsTextView.setText(R.string.default_double_value);
        count = 0;
        lockPushUpsCount = false;
        maxLightRange = 0;
        minLightRange = 0;
        averageLightRange = 0;
        //values for calorie calculation
        // TODO check values and approximation for body proportions
        // calculation from http://www.science-at-home.de/wiki/index.php/Kalorienverbrauch_bei_einzelnen_Sport%C3%BCbungen_pro_Wiederholung
        weightPushed = user.getWeightInKG()*0.5;
        //Factor for the height: approximated using graphic from https://de.wikipedia.org/wiki/K%C3%B6rperproportion,
        //upper arm length is about 1 1/2 fields => 3/8*size
        heightPushed =  ((double) user.getSizeInCM() * 3) / (100 * 8);
        //Divide by 100 to get from cm to meter, multiply with 3/8 for body proportion adjustments
    }

    private void stopWorkout() {
        callback.workoutInProgress(false);

        persistPushupObject();

        // Screen keep on Flag clear
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // set to initial state
        setToInitialState();
        countUpTimer.stop();
    }

    private void persistPushupObject() {
        // TODO: use the countUpTimer for this:
        // System.currentTimeMillis isn't suited for timers and gotta use our optimized countUpTimer for this anyways
        // for more information: https://developer.android.com/reference/android/os/SystemClock.html
        // I would suggest just using the global elapsedTime variable (which gets updated every second) for this
        long duration = System.currentTimeMillis() - startTime;

        // set pushup object
        pushUp.setUser(user);
        pushUp.setRandomId();
        pushUp.setDurationInMillis(duration);
        pushUp.setPushPerMin(calcPushupsPerMinute(duration));
        pushUp.setRepeats(count);
        pushUp.setCalories(calcCalories());
        pushUp.setCurrentDate(Util.getCurrentDateAsString());

        // save workout to database and make Toast to confirm saving
        pushUp.save();
        Toast.makeText(getActivity().getApplicationContext(), "Workout gespeichert!", Toast.LENGTH_SHORT).show();


        // set as last activity
        new SharedPrefsHelper(getContext())
                .setLastActivity(Constants.WORKOUT_PUSHUPS, pushUp.getId());

    }

    private double calcPushupsPerMinute(long duration) {
        return Util.roundTwoDecimals(((double) count * 60000.0) / (double) duration);
    }

    private double calcCalories() {
        // calculation from http://www.science-at-home.de/wiki/index.php/Kalorienverbrauch_bei_einzelnen_Sport%C3%BCbungen_pro_Wiederholung
        //weightPushed and heightPushed are initialized in setToInitialState() method
        // wayUp + wayDown = one push up
        double wayUp = ((weightPushed*heightPushed*9.81) / 4.1868) / 1000;
        double wayDown = wayUp / 2.0;

        return Util.roundTwoDecimals((wayDown + wayUp) * (double) count);
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.pushups));
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Search for light sensor, only start at workoutstart
        // TODO calibration
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values != null && workoutStarted) {
                // calculation light range
                if (maxLightRange < event.values[0]) {

                    if(maxLightRange < 400) {
                        maxLightRange = (event.values[0]);
                        minLightRange = (double) event.values[0] / 2;
                        averageLightRange = (double) event.values[0] / 4;
                        averageLightRange = averageLightRange * 3;
                        //Toast.makeText(getActivity().getApplicationContext(), "unter", Toast.LENGTH_SHORT).show();

                    } else {
                        maxLightRange = (event.values[0]);
                        minLightRange = (double) event.values[0] / 3;
                        averageLightRange = (double) event.values[0] / 2;
                        //Toast.makeText(getActivity().getApplicationContext(), "drüber", Toast.LENGTH_SHORT).show();
                    }

                }

                // Count++ if is unlock
                if (minLightRange > event.values[0] && lockPushUpsCount == false) {
                    lockPushUpsCount = true;
                    count++;
                    pushUpButton.setText(String.valueOf(count));
                    avgPushupsTextView.setText(String.valueOf(calcPushupsPerMinute(elapsedTime)));
                    caloriesTextView.setText(String.valueOf(calcCalories()));
                }

                // Lock
                if (averageLightRange < event.values[0] && lockPushUpsCount == true) {
                    lockPushUpsCount = false;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // empty method, not needed but necessary by implementation of interface
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_instructions:
                new InstructionsDialog(getContext(),
                        getString(R.string.pushups),
                        getActivity().getDrawable(R.drawable.push_up_instruction),
                        getString(R.string.pushup_instructions)).show();
                return true;
            case R.id.action_statistics:
                getActivity().startActivity(new Intent(getActivity(), PushupStatisticsActivity.class));
                return true;
            case R.id.action_share:
                // TODO lots of testing + persistent cardview values
                shareFragmentViewOnClick(getView().findViewById(R.id.container_pushup_counter));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @Override
    public void stopWorkoutOnFragmentChange() {
        stopWorkout();
    }
}
