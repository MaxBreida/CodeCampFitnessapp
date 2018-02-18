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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.SquatStatisticsActivity;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.InstructionsDialog;
import com.codecamp.bitfit.util.SharedPrefsHelper;
import com.codecamp.bitfit.util.Util;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquatFragment extends WorkoutFragment {

    private SensorManager sensorManager;

    private int squatCtr;

    private Squat currentSquat;
    private long startTime;
    private long finishTime;
    //Boolean variable to store if the workout was started, if not: no need to evaluate sensors
    private boolean workoutStarted;

    //GUI elements
    private TextView timeTextView;
    private Button sqFinishButton;
    private TextView squatButton;


    //Use an average value for the accelerometer output
    //Variable for the length of the array for the average value
    private int arrayLength = 10;
    //array for storing the sensor value
    private double[] azValues = new double[arrayLength];
    // counter for how full the array is yet
    private int arrayCtr;
    //average value, calculated later
    private double azAvg;


    enum SquatStates {
            SQUAT_DOWN,
            STAND_UP,
            COUNT
    }

    private SquatStates squatState;

    private CountUpTimer squatTimer;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get acceleration sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        //find view elements
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
                System.out.println("Start workout clicked!");
                // show quit button if we started push ups
                if(!workoutStarted) {
                    workoutStarted = true;
                    squatTimer.start();
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

    private void createSquatObj(){
        long duration = finishTime-startTime; //duration in milliseconds

        //Set attributes of the squat object
        currentSquat.setId(UUID.randomUUID());
        currentSquat.setCurrentDate(Util.getCurrentDateAsString());
        currentSquat.setCalories(calcCalories(duration));
        currentSquat.setDuration(duration);
        currentSquat.setSquatPerMin(calcSquatsPerMinute(duration));
        currentSquat.setRepeats(squatCtr);

        // save object to database
        currentSquat.save();

        // set as last workout
        new SharedPrefsHelper(getContext())
                .setLastActivity(Constants.WORKOUT_SQUATS, currentSquat.getId());
    }

    double calcSquatsPerMinute(long duration){
        return ((squatCtr * 60000) / duration);
    }



    private double calcCalories(long duration) {
        //Get user from database to get weight
        User user = DBQueryHelper.findUser();
        double weight = user.getWeight();

        //MET (metabolic equivalent of task) value for calculating calories
        double metSquat = 5.0;
        // calories are calculated by metSquat*weight*duration (in hours)
        //duration[hours]=duration[msec]/3600000

        return metSquat*(duration/3600000)* weight;
    }

    private void setToInitialState() {
        currentSquat = new Squat();
        workoutStarted = false;
        squatButton.setVisibility(View.VISIBLE);
        sqFinishButton.setVisibility(View.INVISIBLE);
        squatButton.setText("Start");
        timeTextView.setText("0:00");
        timeTextView.setVisibility(View.VISIBLE);
        squatState = SquatStates.SQUAT_DOWN;
        squatCtr = 0;

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

            //Use the linear acceleration sensor for for the squats, but only if start was clicked yet
            if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION && workoutStarted){
                // linear acceleration sensor value in direction z (the other directions are not needed)
                double az;
                startTime = System.currentTimeMillis();


                az = event.values[2];

                //Store the acceleration value until the array is full
                if(arrayCtr<arrayLength){
                    azValues[arrayCtr]=az;
                    arrayCtr++;
                } else {
                    //When it's full: reset the counter
                    arrayCtr=0;
                    //and then calculate average acceleration
                    azAvg = getArrAvg(azValues);
                }


                switch(squatState){
                    case SQUAT_DOWN:
                        if(azAvg>0){
                            // Linear acceleration sensor has a slight negative offset
                            System.out.println("Switching to state stand up");
                            squatState = SquatStates.STAND_UP;
                        }
                        break;
                    case STAND_UP:
                        if(azAvg<0){
                            // While standing up: acceleration is positive
                            System.out.println("Switching to state count");
                            squatState = SquatStates.COUNT;
                        }

                        break;
                    case COUNT:
                        squatCtr++;
                        System.out.println("Squat counted! Switching to state squat down");

                        //Display new value for squat counter
                        squatButton.setText(String.valueOf(squatCtr));
                        squatState=SquatStates.SQUAT_DOWN;
                        break;
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

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_squat, container, false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_instructions:
                new InstructionsDialog(getContext(),
                        getString(R.string.squats),
                        getActivity().getDrawable(R.drawable.squat_instruction),
                        getString(R.string.squat_instructions)).show();
                return true;
            case R.id.action_statistics:
                getActivity().startActivity(new Intent(getActivity(), SquatStatisticsActivity.class));
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

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.squats));
    }
}
