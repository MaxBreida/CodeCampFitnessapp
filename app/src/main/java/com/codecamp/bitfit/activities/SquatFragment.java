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
import com.codecamp.bitfit.util.CountUpTimer;

import static java.lang.Math.abs;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquatFragment extends Fragment {

    private SensorManager mgr;
    private int squatCtr;
    // int zeroCtr = 0;
    private boolean workoutStarted;
    private TextView timeTextView;
    private Button sqFinishButton;
    private TextView squatButton;

    enum SquatStates {
            SQUAT_DOWN,
            STAND_UP,
            COUNT
    }

    private CountUpTimer countUpTimer;

    private SquatStates squatState;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get acceleration sensor
        mgr = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mgr.registerListener(listener, mgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

        //find view elements
        squatButton = getView().findViewById(R.id.button_squat);
        timeTextView = getView().findViewById(R.id.textview_squat_time);
        sqFinishButton = getView().findViewById(R.id.button_squat_quit);

        //Initialize stopwatch
        countUpTimer = new CountUpTimer(1000, timeTextView);

    }


    private void setToInitialState() {
        workoutStarted = false;
        sqFinishButton.setVisibility(View.INVISIBLE);
        squatButton.setText("Start");
        timeTextView.setText("0:00");
        squatState = SquatStates.SQUAT_DOWN;
        squatCtr = 0;
    }

    public void onDestroy(){
        super.onDestroy();
        mgr.unregisterListener(listener);
    }

    public static SquatFragment getInstance() {
        SquatFragment fragment = new SquatFragment();

        return fragment;
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
                double ax,ay,az;
                //double ax_old, ay_old, az_old;


               // double ay_standing = 9.81;

                boolean log = true;

                long startTime = System.currentTimeMillis();
                long stopTime = 0;

                ax = abs(event.values[0]);
                ay = abs(event.values[1]);
                az = abs(event.values[2]);

                logOutput(ax, ay, az, log);
                //User should have the device in the pocket for the exercise.
                //When that is the case the accelaration from gravity goes mainly
                //in direction y

               // if(ay>7){
                    System.out.println("You're standing, now squat!");
                    switch (squatState) {
                        case SQUAT_DOWN:
                            if(ay<6){
                                squatState = SquatStates.STAND_UP;
                            }
                            /*while(ay>6){
                                //Wait until user has "Squatted down"
                                logOutput(ax, ay, az, log);
                            }*/

                            break;
                        case STAND_UP:
                            if(ay>9){
                                squatState = SquatStates.COUNT;
                            }
//                            while(ay<9){
//                                //Wait until user has "Stood upS"
//                                logOutput(ax, ay, az, log);
//                            }
                            squatState = SquatStates.COUNT;
                            break;
                        case COUNT:
                            squatCtr++;
                            System.err.println("Squatted! Counter is " + squatCtr);
                            logOutput(ax, ay, az, log);
                            squatState = SquatStates.SQUAT_DOWN;
                            break;
                    }

                //}


            }

        }

        public void logOutput(double ax, double ay, double az, boolean log){
            //Log values
            if(log){
                //     Log.d("ay triggered with: ", ay + ", ax = " + ax + "az = " + az);

                if(abs(ay)>5){
                    Log.d("ay triggered with: ", ay + ", ax = " + ax + "az = " + az);
                } else if(abs(ax)>5){
                    Log.d("ax triggered with: ", ax + ", ay = " + ay + "az = " + az);
                } else if (abs(az)>5){
                    Log.d("az triggered with: ", az + ", ax = " + ax + "ay = " + ay);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public SquatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_squat, container, false);
    }

}
