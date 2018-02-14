package com.codecamp.bitfit.fragments;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.statistics.PushupStatisticsActivity;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class PushUpFragment extends WorkoutFragment implements SensorEventListener {

    // View stuff
    private TextView timeTextView;
    private Button finishButton;
    private TextView pushUpButton;

    // fragment stuff
    private boolean workoutStarted;
    private int count;
    private CountUpTimer countUpTimer;
    private PushUps pushUp;
    private long startTime;

    // sensor stuff
    private SensorManager mSensorManager;
    private Sensor mLight;
    private float maxLightRange;
    private double minLightRange;
    private double averageLightRange;
    private boolean lockPushUpsCount;


    public static PushUpFragment getInstance() {
        PushUpFragment fragment = new PushUpFragment();

        return fragment;
    }

    public PushUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        timeTextView = getView().findViewById(R.id.textview_pushup_time);
        finishButton = getView().findViewById(R.id.button_pushup_quit);
        pushUpButton = getView().findViewById(R.id.button_pushup);

        countUpTimer = new CountUpTimer(1000, timeTextView);

        // set button to start state
        setToInitialState();

        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // show quit button if we started push ups
                if (!workoutStarted) {
                    finishButton.setVisibility(View.VISIBLE);
                    workoutStarted = true;
                    countUpTimer.start();
                    startTime = System.currentTimeMillis();
                } else {
                    // increment count and set text
                    count++;
                }
                // Screen keep on Flag set
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                pushUpButton.setText(String.valueOf(count));
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                persistPushupObject();

                // Screen keep on Flag set
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // set to initial state
                setToInitialState();
                countUpTimer.stop();

            }
        });
    }

    private void persistPushupObject() {
        long duration = System.currentTimeMillis() - startTime;

        // set pushup object
        pushUp.setId(UUID.randomUUID());
        pushUp.setDuration(duration);
        pushUp.setPushPerMin(calcPushupsPerMinute(duration));
        pushUp.setRepeats(count);
        pushUp.setCalories(calcCalories());
        pushUp.setCurrentDate(getCurrentDateAsString());

        // save workout to database
        pushUp.save();
    }

    private double calcPushupsPerMinute(long duration) {
        return (double) ((count * 60000) / duration);
    }

    //TODO
    private double calcCalories() {
        // dummy value
        return 1.0;
    }

    private String getCurrentDateAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format));
        return dateFormat.format(calendar.getTime());
    }

    private void setToInitialState() {
        pushUp = new PushUps();
        startTime = 0;
        workoutStarted = false;
        finishButton.setVisibility(View.INVISIBLE);
        pushUpButton.setText(R.string.start);
        timeTextView.setText(R.string.default_timer_value);
        count = 0;
        lockPushUpsCount = false;
        maxLightRange = 0;
        minLightRange = 0;
        averageLightRange = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    // TODO
    @Override
    public void onSensorChanged(SensorEvent event) {
        // Search for light sensor, only start at workoutstart
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values != null && workoutStarted) {
                // calculation light range
                if (maxLightRange < event.values[0]) {
                    maxLightRange = (event.values[0]);
                    minLightRange = (double) event.values[0] / 3;
                    averageLightRange = (double) event.values[0] / 2;
                }

                // Count++ if is unlock
                if (minLightRange > event.values[0] && lockPushUpsCount == false) {
                    lockPushUpsCount = true;
                    count++;
                    pushUpButton.setText(String.valueOf(count));
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
            case R.id.action_statistics:
                getActivity().startActivity(new Intent(getActivity(), PushupStatisticsActivity.class));
                return true;
            case R.id.action_share:
                // TODO start share intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
