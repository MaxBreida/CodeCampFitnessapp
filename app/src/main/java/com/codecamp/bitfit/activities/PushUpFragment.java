package com.codecamp.bitfit.activities;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PushUpFragment extends Fragment implements SensorEventListener {

    // View stuff
    private TextView timeTextView;
    private Button finishButton;
    private Button pushUpButton;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private boolean workoutStarted;
    private int count;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_push_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // sensor stuff
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        // find view stuff
        timeTextView = getView().findViewById(R.id.textview_pushup_time);
        finishButton = getView().findViewById(R.id.button_pushup_quit);
        pushUpButton = getView().findViewById(R.id.button_pushup);

        // set button to start state
        setToInitialState();

        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show quit button if we started push ups
                if(finishButton.getVisibility() == View.INVISIBLE) {
                    finishButton.setVisibility(View.VISIBLE);
                    workoutStarted = true;
                }
                // increment count and set text
                count++;
                pushUpButton.setText(String.valueOf(count));
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set to initial state
                setToInitialState();
                // TODO add count to database
            }
        });
    }

    private void setToInitialState() {
        workoutStarted = false;
        finishButton.setVisibility(View.INVISIBLE);
        pushUpButton.setText("Start");
        timeTextView.setText("0:00");
        count = 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this,
                proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // TODO
    @Override
    public void onSensorChanged(SensorEvent event) {
        int val = (int) event.values[0];

        Log.d("Proximity: ", String.valueOf(val));

        if(val > 1)
            val = 1;

        // increment count and set text
        count += val;
        pushUpButton.setText(String.valueOf(count));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
