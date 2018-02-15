package com.codecamp.bitfit.fragments;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.statistics.SquatStatisticsActivity;
import com.codecamp.bitfit.util.Util;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.abs;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquatFragment extends WorkoutFragment {

    private SensorManager sensorManager;

//    private Sensor proximitySensor;
//
//    private static final int PROXIMITY_SENSITIVITY = 4;

    private int squatCtr;

    private Squat currentSquat;
    private long startTime;
    private long finishTime;
    private boolean workoutStarted;
    private boolean exercisesStarted;
    private TextView timeTextView;
    private Button instructionButton;
    private TextView instruction;
    private Button sqFinishButton;
    private TextView squatButton;


    //Use an average value for the accelerometer output
    //Counter and length for the average
    int arrayCtr;
    int arrayLength = 10;

    //arrays for storing the sensor values
    private double[] axValues = new double[arrayLength];
    private double[] ayValues = new double[arrayLength];
    private double[] azValues = new double[arrayLength];

    //average values, calculated later
    private double axAvg;
    private double ayAvg;
    private double azAvg;


    enum SquatStates {
            SQUAT_DOWN,
            STAND_UP,
            COUNT
    }

    private CountUpTimer squatTimer;

    private SquatStates squatState;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get acceleration sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

//        sensorManager.registerListener(listener, proximitySensor, sensorManager.SENSOR_DELAY_NORMAL);


        //find view elements
        instructionButton = getView().findViewById(R.id.button_squat_instruction);
        instruction = getView().findViewById(R.id.textview_instruction);
        squatButton = getView().findViewById(R.id.button_squat);
        timeTextView = getView().findViewById(R.id.textview_squat_time);
        sqFinishButton = getView().findViewById(R.id.button_squat_quit);

        //Initialize stopwatch
        squatTimer = new CountUpTimer(1000, timeTextView);

        //Call initialization method
        setToInitialState();

        //Create listener for squat workout start
        squatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show quit button if we started push ups
                if(!workoutStarted) {
                    workoutStarted = true;
                    squatTimer.start();
                    instruction.setVisibility(View.INVISIBLE);
                    instructionButton.setVisibility(View.INVISIBLE);
                    timeTextView.setVisibility(View.VISIBLE);
                    sqFinishButton.setVisibility(View.VISIBLE);
                    squatButton.setVisibility(View.VISIBLE);
                }

                // Screen keep on Flag set
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // increment count and set text
                squatButton.setText(String.valueOf(squatCtr));
            }
        });



        sqFinishButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                squatTimer.stop();
                finishTime = System.currentTimeMillis();

                // Screen keep on Flag set
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                // TODO: view acticity details and send them to database
                createSquatObj();
                setToInitialState();
            }
        });

    }

//    //Register proximity sensor listener
//    @Override
//    public void onResume(){
//        super.onResume();
//        sensorManager.registerListener(listener, proximitySensor, sensorManager.SENSOR_DELAY_NORMAL);
//    }
    private void createSquatObj(){
        long duration = finishTime-startTime;

        //Set attributes of the squat object
        currentSquat.setId(UUID.randomUUID());
        currentSquat.setCurrentDate(getCurrentDateAsString());
        currentSquat.setCalories(calcCalories());
        currentSquat.setDuration(duration);
        currentSquat.setSquatPerMin(calcSquatsPerMinute(duration));
        currentSquat.setRepeats(squatCtr);
        currentSquat.save();
    }

    double calcSquatsPerMinute(long duration){
        return ((squatCtr * 60000) / duration);
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
        currentSquat = new Squat();
        workoutStarted = false;
        exercisesStarted = false;
        squatButton.setVisibility(View.VISIBLE);
        sqFinishButton.setVisibility(View.INVISIBLE);
        instructionButton.setVisibility(View.VISIBLE);
        instruction.setVisibility(View.INVISIBLE);
        instruction.setText("Lay your snartphone on your hand horizontally, click on the start " +
                "button and start squatting!");
        squatButton.setText("Start");
        timeTextView.setText("0:00");
        squatState = SquatStates.SQUAT_DOWN;
        squatCtr = 0;

/*        instruction.setVisibility(View.INVISIBLE);
        instructionButton.setVisibility(View.INVISIBLE);
        timeTextView.setVisibility(View.VISIBLE);
        sqFinishButton.setVisibility(View.VISIBLE);
        squatButton.setVisibility(View.INVISIBLE);*/
    }

    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }

    public static SquatFragment getInstance() {
        SquatFragment fragment = new SquatFragment();

        return fragment;
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

           /*
            //Use proximity sensor to turn off display when the smartphone is in the pocket
            if(event.sensor.getType()==Sensor.TYPE_PROXIMITY){
                if(event.values[0] >= -PROXIMITY_SENSITIVITY && event.values[0]<= PROXIMITY_SENSITIVITY){
                    //Sensor is near: Smartphone is in the pocket
                    System.out.println("Proximity triggered");
                }
            }*/

            //Use the accelerometer for the squats
            if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION && workoutStarted){
                double ax,ay,az;

                //boolean variable log (for development): If on, the values of the accelerometer
                //are logged and printed
                boolean log = false;

                startTime = System.currentTimeMillis();

                ax = event.values[0];
                ay = event.values[1];
                az = event.values[2];

                //Store the acceleration values until the array is full
                if(arrayCtr<arrayLength){
                    axValues[arrayCtr]=ax;
                    ayValues[arrayCtr]=ay;
                    azValues[arrayCtr]=az;
                    arrayCtr++;
                } else {
                    //When it's full: reset the counter
                    arrayCtr=0;
                    //and then calculate average acceleration for each sensor
                    axAvg = getArrAvg(axValues);
                    ayAvg = getArrAvg(ayValues);
                    azAvg = getArrAvg(azValues);
                    if(log){
                        System.out.println("ax avg: " + axAvg + " ay avg: " + ayAvg + " az avg: " + azAvg);
                        Log.d("ax avg: ", axAvg + ", ay avg:  " + ayAvg + ", az avg: " + azAvg);
                    }
                }

                switch(squatState){
                    case SQUAT_DOWN:
                        if(azAvg>0){
                            // Linear acceleration sensor has a slight negative offset
//                            System.out.println("Switching to state stand up");
                            squatState = SquatStates.STAND_UP;
                        }
                        break;
                    case STAND_UP:
                        if(azAvg<0){
                            // While standing up: acceleration is positive
//                            System.out.println("Switching to state count");
                            squatState = SquatStates.COUNT;
                        }

                        break;
                    case COUNT:
                        squatCtr++;
//                        System.out.println("Squat counted! Switching to state squat down");

                        //Display new value for squat counter
                        squatButton.setText(String.valueOf(squatCtr));
                        squatState=SquatStates.SQUAT_DOWN;
                        break;
                    }

                }

        }
/*
        public void logOutput(double ax, double ay, double az, boolean log){
            //Log values
            if(log){
                //     Log.d("ay triggered with: ", ay + ", ax = " + ax + "az = " + az);

                if(abs(ay)>5 && ay>ax && ay>az){
                    Log.d("ay triggered!: ", ay + ", ax = " + ax + ", az = " + az);
                } else if(abs(ax)>5  && ax>ay && ax>az){
                    Log.d("ax triggered with: ", ax + ", ay = " + ay + ", az = " + az);
                } else if (abs(az)>5  && az>ax && az>ay){
                    Log.d("az triggered with: ", az + ", ax = " + ax + ", ay = " + ay);
                } else {
                    Log.d("Values: ay = ", ay + ", ax = " + ax + ", az = " + az);
                }
            }
        }*/

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

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_squat, container, false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_statistics:
                // TODO start statistics activity
                return true;
            case R.id.action_share:
                // TODO start share intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Help method for calculating an arrays average,
    //needed to calculate the average acceleration values over some time
    private double getArrAvg(double[] array){
        double average = 0;
        double sum = 0;
        for(int i=0; i<array.length; i++){
            sum+=array[i];
        }

        average = sum/array.length;

        return average;
    }
}
