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
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.SquatStatisticsActivity;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
//import com.codecamp.bitfit.util.FinishDialog;
import com.codecamp.bitfit.util.InstructionsDialog;
import com.codecamp.bitfit.util.SharedPrefsHelper;
import com.codecamp.bitfit.util.Util;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class SquatFragment extends WorkoutFragment {

    User user = DBQueryHelper.findUser();
    private SensorManager sensorManager;

    private int squatCtr;
    private Squat currentSquat;
    //Time variables
    private long startTime;
    private long finishTime;
    private long elapsedTime;
    private long pauseTime;
    private long resumeTime;

    //Boolean variable to store if the workout was started, if not: no need to evaluate sensors
    private boolean workoutStarted;

    //Values for calorie calculation
    double weightPushed;
    double heightPushed;

    //GUI elements
    private TextView timeTextView;
    private FloatingActionButton sqFinishButton;
    private TextView squatButton;
    private TextView avgSquatsTextView;
    private TextView caloriesTextView;
    private FloatingActionButton sqResumeButton;
    private TextView resumeTextView;
    private TextView quitTextView;
    private View container;

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

    enum QuitButtonStates {
        STOP_CLICK,
        SAVE_CLICK
    }

    private SquatStates squatState;

    private QuitButtonStates quitState;

    private CountUpTimer squatTimer;

    public SquatFragment() {
        // Required empty public constructor
    }

    public static SquatFragment getInstance() {
        SquatFragment fragment = new SquatFragment();

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_squat, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get acceleration sensor
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_NORMAL);

        //find view elements
        TextView avgTitleTextView = getView().findViewById(R.id.textview_cardview_avg_title);
        avgTitleTextView.setText(getString(R.string.squats_per_min));

        squatButton = getView().findViewById(R.id.button_squat);
        sqFinishButton = getView().findViewById(R.id.button_squat_quit);
        sqResumeButton = getView().findViewById(R.id.button_squat_resume);
        resumeTextView = getView().findViewById(R.id.textView_squat_resume);
        quitTextView = getView().findViewById(R.id.textView_squat_quit);

        timeTextView = getView().findViewById(R.id.textview_cardview_time);
        avgSquatsTextView = getView().findViewById(R.id.textview_cardview_avg);
        caloriesTextView = getView().findViewById(R.id.textview_cardview_calories);
        container = getView().findViewById(R.id.container_squat_counter);

        //Initialize stopwatch
        squatTimer = new CountUpTimer(1000, timeTextView) {
            @Override
            public void onTick(long elapsedTime) {
                setElapsedTime(elapsedTime);
                avgSquatsTextView.setText(String.valueOf(calcSquatsPerMinute(elapsedTime)));
                timeTextView.setText(Util.getMillisAsTimeString(elapsedTime));
            }
        };

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
                    startTime = System.currentTimeMillis();
                    container.setVisibility(View.VISIBLE);
                    sqFinishButton.setVisibility(View.VISIBLE);
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
                if(quitState.equals(QuitButtonStates.STOP_CLICK)){
                    squatTimer.stop();
                    pauseTime = System.currentTimeMillis();
                    workoutStarted = false;

                    sqResumeButton.setVisibility(View.VISIBLE);
                    resumeTextView.setVisibility(View.VISIBLE);
                    quitTextView.setVisibility(View.VISIBLE);

                    quitState = QuitButtonStates.SAVE_CLICK;
                    // Screen keep on Flag set
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else if(quitState.equals(QuitButtonStates.SAVE_CLICK)){
                    finishTime = System.currentTimeMillis();
                    currentSquat = createSquatObj();
                    // save workout to database and make Toast to confirm saving
                    currentSquat.save();
                    Toast.makeText(getActivity().getApplicationContext(), "Workout gespeichert!", Toast.LENGTH_SHORT).show();
                    // set as last workout
                    new SharedPrefsHelper(getContext())
                            .setLastActivity(Constants.WORKOUT_SQUATS, currentSquat.getId());

                    setToInitialState();
                }
            }
        });

        // TODO make CountUpTimer not be resetted
        sqResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitState = QuitButtonStates.STOP_CLICK;
                workoutStarted = true;

                sqResumeButton.setVisibility(View.INVISIBLE);
                resumeTextView.setVisibility(View.INVISIBLE);
                quitTextView.setVisibility(View.INVISIBLE);

                squatTimer.start();
                resumeTime = System.currentTimeMillis();
            }
        });

    }

    private void setToInitialState() {
        currentSquat = new Squat();
        workoutStarted = false;
        container.setVisibility(View.INVISIBLE);
        sqFinishButton.setVisibility(View.INVISIBLE);
        squatButton.setText(getString(R.string.start));
        timeTextView.setText(getString(R.string.default_timer_value));
        caloriesTextView.setText(R.string.default_double_value);
        avgSquatsTextView.setText(R.string.default_double_value);

        sqResumeButton.setVisibility(View.INVISIBLE);
        quitTextView.setVisibility(View.INVISIBLE);
        resumeTextView.setVisibility(View.INVISIBLE);

        squatState = SquatStates.SQUAT_DOWN;
        quitState = QuitButtonStates.STOP_CLICK;
        squatCtr = 0;
        //values for calorie calculation
        // TODO check values and approximation for body proportions
        // calculation from http://www.science-at-home.de/wiki/index.php/Kalorienverbrauch_bei_einzelnen_Sport%C3%BCbungen_pro_Wiederholung
        //Using factor 0.5 instead of 0.7 at PushUp because there you push more of your weight than when you're doing a squat
        weightPushed = user.getWeightInKG()*0.5;
        //Factor for the height pushed = size/4, approximated using graphic from https://de.wikipedia.org/wiki/K%C3%B6rperproportion
        heightPushed = (double) user.getSizeInCM() / (100 * 4);
        //Divide by 100 to get from cm to meter and divide by 4 to adjust the value to body proportions
    }

    private Squat createSquatObj(){
        // TODO: I need to check if this is really giving right values
        // I had one workout where a time about 25300000:44 was saved (it wasn't that long ;D)
        long duration = finishTime-startTime-(resumeTime-pauseTime); //duration in milliseconds

        //Set attributes of the squat object
        User currentUser = DBQueryHelper.findUser();

        currentSquat.setUser(currentUser);
        currentSquat.setId(UUID.randomUUID());
        currentSquat.setCurrentDate(Util.getCurrentDateAsString());
        currentSquat.setCalories(calcCalories());
        currentSquat.setDurationInMillis(duration);
        currentSquat.setSquatPerMin(calcSquatsPerMinute(duration));
        currentSquat.setRepeats(squatCtr);

        return currentSquat;
    }

    double calcSquatsPerMinute(long duration){
        return Util.roundTwoDecimals(((double) squatCtr * 60000.0) / (double) duration);
    }

    double calcCalories() {
        // calculation from http://www.science-at-home.de/wiki/index.php/Kalorienverbrauch_bei_einzelnen_Sport%C3%BCbungen_pro_Wiederholung
        //weightPushed and heightPushed are initialized in setToInitialState() method
        // wayUp + wayDown = one squat
        double wayUp = ((heightPushed*weightPushed*9.81) / 4.1868) / 1000;
        double wayDown = wayUp / 2.0;

        double calorie = Util.roundTwoDecimals((wayDown + wayUp) * (double) squatCtr);
        return Util.roundTwoDecimals((wayDown + wayUp) * (double) squatCtr);
    }

    public void onDestroy(){
        super.onDestroy();
        sensorManager.unregisterListener(listener);
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            //Use the linear acceleration sensor for for the squats, but only if start was clicked yet
            if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION && workoutStarted){
                // linear acceleration sensor value in direction z (the other directions are not needed)
                double az;


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
                            squatState = SquatStates.STAND_UP;
                        }
                        break;
                    case STAND_UP:
                        if(azAvg<0){
                            // While standing up: acceleration is positive
                            squatState = SquatStates.COUNT;
                        }

                        break;
                    case COUNT:
                        squatCtr++;
                        //Display new value for squat counter
                        squatButton.setText(String.valueOf(squatCtr));
                        avgSquatsTextView.setText(String.valueOf(calcSquatsPerMinute(elapsedTime)));
                        caloriesTextView.setText(String.valueOf(calcCalories()));
                        squatState=SquatStates.SQUAT_DOWN;
                        break;
                    }

                }

        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // Nothing here
        }
    };

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
                // TODO lots of testing + persistent cardview values, rethink where sharing would best be placed
                // My opinion: Sharing should be possible from the "Finish workout?" screen
                //(which is not yet created), from here you'd share a not yet finished workout
                shareFragmentViewOnClick(getView().findViewById(R.id.container_squat_counter));
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

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

}
