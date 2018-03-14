package com.codecamp.bitfit.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.OnDialogInteractionListener;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.RunStatisticsActivity;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.SharedPrefsHelper;
import com.codecamp.bitfit.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;
import static com.codecamp.bitfit.util.Util.decNumToXPrecisionString;

/**
 * A simple {@link Fragment} subclass.
 */

public class RunFragment extends WorkoutFragment implements OnDialogInteractionListener{

    // Map related global variables
    Polyline line; // the line that represents the running track
    List<LatLng> points = new ArrayList<>(); // a list of points of the running track
    GoogleMap mMap; // an instance of a google map client
    LocationManager lm; // location manager that keeps track of current location

    PowerManager.WakeLock wakeLock; // a wakelock to keep the device running for location updates

    boolean workoutActive = false; // true = workout in progress and vice versa

    // Activities and views related global variables
    FragmentActivity activity; // avoid using getActivity() all the time
    View mainView, dataCard; // avoid using getView() each time it's needed and comfy access to data
    View customDialogLayout;
    FloatingActionButton startPauseButton;
    FloatingActionButton stopButton;

    // userdata and database related global variables
    boolean allowDataUpdate = false; // determines whether or not the database can update automatically
    Run database; // Object that allows writing to the database
    User user;
    long runDuration = 0;
    float runningDistance = 0;
    int unitMode = 1; // determines whether the speed should be displayed in current
                // kmh, avg. kmh or m/s 1 = current km/h, 2 = m/s, 3 = average km/h

    // run duration timer and a timer for saving the data of a run to the database
    CountUpTimer runDurationTimer, saveDataTimer;

    public static RunFragment getInstance() {

        return new RunFragment();
    }

    public RunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // taking care of the infamous "may be null" warnings
        if(getActivity() != null)
            activity = getActivity();
        if(getView() != null) {
            mainView = view;
            dataCard = mainView.findViewById(R.id.run_data_cardview);
        }

        // because it is just being created we set it to inactive
        workoutActive = false;

        // ask for location permissions if not already given
        if(!checkPermission()) getLocationPermissions();
        else {
            // get the current user
            user = DBQueryHelper.findUser();

            initializeDatabaseObject();

            // setting up map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(mapReady);

            // setting wakelock
            PowerManager powerManager = (PowerManager) activity.getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RunTracking");

            setUpSpeedUnitSwitcher();

            // set location manager up
            lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

            // setting up the start / pause button
            startPauseButton = mainView.findViewById(R.id.button_start_pause_run);
            startPauseButton.setOnClickListener(startPauseButListener);
            startPauseButton.setVisibility(View.VISIBLE);
            // stop button:
            stopButton = mainView.findViewById(R.id.button_stop_run);
            stopButton.setOnClickListener(stopButtonListener);
        }
    }

    // is it the first click on the start button?
    boolean firstClick = true;
    View.OnClickListener startPauseButListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(startPauseButton.isActivated()){
                // tell mainactivity that the workout is stopped
                callback.workoutInProgress(false);
                workoutActive = false;

                // could be intense TODO start a new line after a pause when the distance ist too far

                updateDatabase();

                // change button design and move it
                setStartButtonDesign(
                        false,
                        getResources().getColor(R.color.darkerGreen),
                        R.drawable.ic_play_arrow_white_48dp
                );
                moveStartButtonLeft(true);

                // show and animate stop button:
                makeStopButtonAppear(true);

                // stop the timers
                runDurationTimer.stop();
                saveDataTimer.stop();

                // deactivate location updates and release wakelock
                lm.removeUpdates(workoutLocListener);
                wakeLock.release();
            }
            else{
                // tell mainactivity that a workout is in progress
                callback.workoutInProgress(true);
                workoutActive = true;

                // acquire wakelock with a 10 hour timeout, some runs could last that long i guess
                wakeLock.acquire(36000000);

                // start timers:
                if(runDurationTimer == null) {
                    // move button down on first click:
                    setupRunDurationTimer(
                            (TextView) dataCard.findViewById(R.id.textview_run_duration),
                            (TextView) dataCard.findViewById(R.id.textview_run_speed)
                    );
                    runDurationTimer.start();
                }
                else if(firstClick)
                    runDurationTimer.start();
                else
                    runDurationTimer.resume();

                if(saveDataTimer == null)
                    setupDataSavingInterval();
                else
                    saveDataTimer.start();

                // change button design and move it
                setStartButtonDesign(
                        true,
                        getResources().getColor(R.color.red),
                        R.drawable.ic_pause_white
                );
                if(firstClick){
                    moveStartButtonDown(true);
                    firstClick = false;
                }
                else {
                    moveStartButtonLeft(false);
                    // hide stop button again
                    makeStopButtonAppear(false);
                }

                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    // quick and easy TODO: notify user that GPS should be used for proper precision, network location services aren't suited for this purpose
                }
                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // quick and easy TODO: notify user that location services are off, pop up a button that allows the user to quickly navigate to the location settings
                }
                else{
                    if(checkPermission())
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 10, workoutLocListener);
                }
            }
        }
    };

    private void setStartButtonDesign(boolean active, int color, int resId){
        startPauseButton.setActivated(active);
        startPauseButton.setBackgroundTintList(ColorStateList.valueOf(color));
        startPauseButton.setImageResource(resId);
    }
    private void moveStartButtonLeft(boolean go){
        startPauseButton.animate().rotationBy((go) ? -360 : 360);
        startPauseButton.animate().xBy(toDp((go) ? -50 : 50));
    }
    private void moveStartButtonDown(boolean go){
        startPauseButton.animate().yBy(toDp((go) ? 19 : -19));
    }
    private void makeStopButtonAppear(boolean go) {
        stopButton.animate().rotationBy((go) ? 360 : -360);
        stopButton.animate().alpha((go) ? 1.0f : 0);
        stopButton.setClickable(go);
        stopButton.animate().xBy(toDp((go) ? 50 : -50));
    }

    View.OnClickListener stopButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            showWorkoutCompleteDialog();
        }
    };

    private float toDp(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()
        );
    }

    /**
     * sets up a clickable text view that switches the method of displaying the speed on click
     */
    private void setUpSpeedUnitSwitcher() {
        TextView speedUnit = dataCard.findViewById(R.id.textview_run_speed);
        View.OnClickListener switchSpeedUnit = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch(unitMode){
                    case 1: // state 2 -> m/s
                        ((TextView)view).setText("-.-m/s");
                        break;
                    case 2: // state 3 -> average km/h
                        ((TextView)view).setText(getAverageSpeedString());
                        break;
                    default: // state 1 -> km/h
                        ((TextView)view).setText("-.-km/h");
                        unitMode = 0;
                        break;
                }
                unitMode++;
            }
        };
        speedUnit.setOnClickListener(switchSpeedUnit);
    }

    // let's the user tracker set a new point it true:
    boolean allowLocUpdate = false;

    // sets minimal precision for the location updates (in meters):
    int precisionTolerance = 10;

    // should the user be tracked by the map camera?
    boolean allowUserTracking = false; // gets switched on start!

    LocationListener trackUser = new LocationListener(){

        // last known position, used for initial point once workout starts
        Location previousLoc = null;

        @Override
        public void onLocationChanged(Location loc){
            // zoom in on first found location, then just track
            if(allowUserTracking)
                if(previousLoc != null) setMapCam(loc);
                else setMapCam(loc, 15);

            if(allowLocUpdate && loc.getAccuracy() <= precisionTolerance){
                // set a point if accuracy is good enough:
                LatLng curPos = new LatLng(loc.getLatitude(),loc.getLongitude());
                points.add(curPos);
                line.setPoints(points);
                if(previousLoc != null){
                    runningDistance += loc.distanceTo(previousLoc);
                    TextView distanceText = dataCard.findViewById(R.id.textview_run_distance);
                    distanceText.setText(decNumToXPrecisionString(runningDistance/1000, 2));
                    if(allowDataUpdate)
                        updateDatabase();
                }
                previousLoc = loc;

                // set Calories
                TextView calsText = dataCard.findViewById(R.id.textview_run_calories);
                calsText.setText(decNumToXPrecisionString(getCurrentCalories(),1));
            }

            if(unitMode != 3 && loc.hasSpeed()){
                // set speed if the location manager can provide those readings
                TextView speedText = dataCard.findViewById(R.id.textview_run_speed);
                float curSpeed = loc.getSpeed();
                if(unitMode == 1) {
                    curSpeed *= 3.6f;
                    speedText.setText(decNumToXPrecisionString(curSpeed, 1).concat("km/h"));
                }
                else
                    speedText.setText(decNumToXPrecisionString(curSpeed, 1).concat("m/s"));
            }
            // saves the last known position before a workout is started
            if(!workoutActive) previousLoc = loc;
        }
        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    };

    LocationListener workoutLocListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location loc) {
            // if the precision is already good enough, trigger an drawing update right away
            // else let the user tracker set a drawing point on the next precise location it receives
            if(loc.getAccuracy() <= precisionTolerance)
                trackUser.onLocationChanged(loc);
            else
                allowLocUpdate = true;
        }
        @Override public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override public void onProviderEnabled(String s) {}
        @Override public void onProviderDisabled(String s) {}
    };

    private void setMapCam(Location loc, float zoom) {
        if(mMap != null && loc != null){
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
        }
    }
    private void setMapCam(Location loc){
        setMapCam(loc, mMap.getCameraPosition().zoom);
    }

    public double getAverageSpeedInKmh() {
        return (runDuration != 0)? (runningDistance / 1000.0) / (runDuration / 3600000.0) : 0;
        //return 0 if run duration is 0 to avoid division by zero
    }
    public String getAverageSpeedString() {
        return "Ø " + decNumToXPrecisionString(getAverageSpeedInKmh(), 1) + "km/h";
    }

    private double getCurrentCalories() {
        // fuck this formula TODO: lots of testing & make sure that weight has to be put in lbs and not kg ... flawed formula, negative cals for fat people
        // M: [(Age * 0.2017) - (Weight * 0.09036) + (Heart Rate * 0.6309) - 55.0969] * Time / 4.184
        // F: [(Age * 0.074 ) - (Weight * 0.05741) + (Heart Rate * 0.4472) - 20.4022] * Time / 4.184
        // HR: bpm = (46 * kmh) / 8.04672 + 80   (Detailed explanations in the documentation)
        boolean m = user.isMale();
        // calculate average hear-rate with the average speed of this run:
        double heartRate = getAverageSpeedInKmh() * (46 / 8.04672) + 80;
        double ageParameter = user.getAge() * ((m) ? 0.2017 : 0.074);
        double weightParameter = user.getWeightInLbs() * ((m) ? 0.09036 : 0.05741);
        double heartRateParameter = heartRate * ((m) ? 0.6309 : 0.4472);
        heartRateParameter -= ((m) ? 55.0969 : 20.4022);
        double cals = ageParameter - weightParameter + heartRateParameter;
        cals *= (runDuration / (4.184 * 60000000));
        // this formula might not be suited for very short runs, preventing negative calorie values:
        if(cals < 0) cals = 0;
        return cals;
    }

    OnMapReadyCallback mapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            PolylineOptions lineOptions = new PolylineOptions()
                    .color(getResources().getColor(R.color.red))
                    .width(4);
            line = mMap.addPolyline(lineOptions);

            if(checkPermission()) mMap.setMyLocationEnabled(true);

            // set camera to last known position, testing all location providers from precise to coarse
            String[] locationProviders = {
                    LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER
            };
            for (String locP : locationProviders) {
                Location last = lm.getLastKnownLocation(locP);
                if(last != null) {
                    setMapCam(last, 10);
                    break;
                }
            }

            GoogleMap.OnMyLocationButtonClickListener locButListener = new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    allowUserTracking = !allowUserTracking;
                    ImageView myLocBut = mainView.findViewById(R.id.map).findViewWithTag("GoogleMapMyLocationButton");

                    // color the button
                    if(allowUserTracking)
                        myLocBut.setColorFilter(getResources().getColor(R.color.transLightBlue));
                    else
                        myLocBut.clearColorFilter();

                    // show toast notification that indicates the state of the tracking
                    String text = "Standort Verfolgung " + (allowUserTracking ? "" : "de") + "aktiviert!";
                    Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                    return false; // false = run super method -> navigate to current location
                }
            };
            mMap.setOnMyLocationButtonClickListener(locButListener);
            // if location services were enabled after the permission check, execute the onclick method once:
            if(mMap.isMyLocationEnabled()) locButListener.onMyLocationButtonClick();
        }
    };

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void getLocationPermissions(){
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission was granted
                        // quick way for restarting the run fragment:
                        ((MainActivity) activity).sendToTab(3);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                            // showSettingsDialog(); //TODO: send user to settings if permissions denied permanently
                        }
                        // quick and easy TODO: tell the user that location permissions are required to use the run workout!
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // continue to ask user for permission
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_statistics:
                activity.startActivity(new Intent(activity, RunStatisticsActivity.class));
                return true;
            case R.id.action_instructions:
                // need to discuss TODO show instructions or maybe leave them out and delete button => not really necessary here
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupRunDurationTimer(final TextView time, final TextView speed){
        runDurationTimer = new CountUpTimer(1000, time) { // 1000 millisecs = every second
            @Override
            public void onTick(long elapsedTime) {
                runDuration = elapsedTime;
                time.setText(Util.getMillisAsTimeString(elapsedTime));
                if(unitMode == 3)
                    speed.setText(getAverageSpeedString());
            }
        };
    }

    /** Since writing to the database every second, or on every data change would be too much,
     * it's only being done every minute (on the next data change), that way we go rather easy
     * on the device's storage unit, which has a limited lifespan in terms of write actions. */
    public void setupDataSavingInterval(){
        saveDataTimer = new CountUpTimer(60000) { // 60000 millisecs = every minute
            int previousAmountOfPoints = 0;
            @Override
            public void onTick(long elapsedTime) {
                if (previousAmountOfPoints < points.size()){
                    updateDatabase();
                    previousAmountOfPoints = points.size();
                }
                else {
                    allowDataUpdate = true;
                }
            }
        };
    }

    public void updateDatabase() {
        allowDataUpdate = false;/* since it's going to be updated now we can block all other update
                    attempts for now, this actually prevents simultaneous write conflicts as well */
        saveDataTimer.reset(); // resetting timer so that the next update can only happen after a minute
        // could get intense TODO: save points too
        // TODO: investigate crashes after reinstallation

        // TODO: check for rounding issues
        database.setDistanceInMeters(runningDistance);
        database.setDurationInMillis(runDuration);
        database.setCalories(getCurrentCalories());
        database.save();

        // set as last workout
        new SharedPrefsHelper(getContext())
                .setLastActivity(Constants.WORKOUT_RUN, database.getId());
    }

    private void fullReset() {
        lm.removeUpdates(workoutLocListener);
        runningDistance = 0;
        points.clear();
        makeStopButtonAppear(false);
        moveStartButtonLeft(false);
        moveStartButtonDown(false);
        allowLocUpdate = false;
        firstClick = true;
        initializeDatabaseObject();
    }

    private void initializeDatabaseObject() {
        database = new Run();
        database.setUser(user);
        database.setRandomId();
        database.setCurrentDate();
    }

    @Override
    public void onPause() {
        super.onPause();

        // if there's no workout in progress release wake lock if held and remove listener
        if(!workoutActive) {
            if (wakeLock.isHeld()) wakeLock.release();
            lm.removeUpdates(trackUser);
            lm.removeUpdates(workoutLocListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) activity)
                .setActionBarTitle(getString(R.string.run));

                // set up user tracker if location permissions are given
        if(checkPermission())
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0, trackUser);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000, 0, trackUser);
            /* sets the location manager up to execute onLocationChanged on specific conditions:
             * 1st parameter sets the location provider that should be used to get updates (GPS / Network)
             * 2nd parameter sets the minimal time (in milliseconds) of a location update
             * 3rd parameter sets the minimal distance (in meters) that you have to travel to trigger an update
             * 4th parameter sets the location listener, which determines the code that is executed
             *      on a change of the location
             */

    }

    @Override
    public void stopWorkoutOnFragmentChange() {
        // stop workout here
        updateDatabase();
        showWorkoutCompleteDialog();
    }

    private void showWorkoutCompleteDialog() {
        // set the custom layout
        customDialogLayout = getLayoutInflater().inflate(R.layout.dialog_content_run_workout, null);
        if (mMap != null)
            mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
            /**
             * provides a bitmap image of the map
             * @param bitMap the image
             */
            @Override
            public void onSnapshotReady(Bitmap bitMap) {
                ImageView imageView = customDialogLayout.findViewById(R.id.placeholder_dialog_run_workout_map);
                imageView.setImageBitmap(bitMap);
            }
        });

        DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Util.shareViewOnClick(activity,
                        customDialogLayout.findViewById(R.id.dialog_run_workout_content),
                        String.format("Ich habe bei meinem letzten Lauftraining %.2fkm zurückgelegt!", runningDistance / 1000));

                // set to initial state
                fullReset();

                callback.setNavigationItem();
            }
        };

        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // set to initial state
                fullReset();

                callback.setNavigationItem();
            }
        };

        AlertDialog dialog = Util.getWorkoutCompleteDialog(getActivity(), database, customDialogLayout, positive, negative);
        dialog.show();
    }
}
