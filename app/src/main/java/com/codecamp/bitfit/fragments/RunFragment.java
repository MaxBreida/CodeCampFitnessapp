package com.codecamp.bitfit.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.InstructionsDialog;
import com.codecamp.bitfit.util.OnDialogInteractionListener;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.RunStatisticsActivity;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.SharedPrefsHelper;
import com.codecamp.bitfit.util.Util;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    PolylineOptions lineOptions; // options for the line that's being drawn
    Polyline line; // the line that represents the running track
    List<LatLng> points = new ArrayList<>(); // a list of points of the current running track
    GoogleMap mMap; // an instance of a google map client
    FusedLocationProviderClient fusedLocProvider;

    PowerManager.WakeLock wakeLock; // a wakelock to keep the device running for location updates

    boolean workoutActive = false; // true = workout in progress and vice versa

    // Activities and views related global variables
    FragmentActivity activity; // avoid using getActivity() all the time
    View mainView, dataCard; // avoid using getView() each time it's needed and comfy access to data
    View customDialogLayout;
    TextView durationText;
    TextView distanceText;
    TextView speedText;
    TextView calsText;
    FloatingActionButton startPauseButton;
    FloatingActionButton stopButton;

    // userdata and database related global variables
    boolean allowDataUpdate = false; // determines whether or not the database can update automatically
    Run database; // Object that allows writing to the database
    User user;
    long runDuration = 0;
    float runDistance = 0;
    int unitMode = 1; // determines whether the speed should be displayed in current
                // kmh, avg. kmh or m/s 1 = current km/h, 2 = m/s, 3 = average km/h

    // location request criteria:
    LocationRequest locationRequest = new LocationRequest()
            .setInterval(2000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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

        // taking care of the infamous "may be null" warnings & setting views
        if(getActivity() != null)
            activity = getActivity();
        if(getView() != null) {
            mainView = view;
            dataCard = mainView.findViewById(R.id.run_data_cardview);
            distanceText = dataCard.findViewById(R.id.textview_run_distance);
            speedText = dataCard.findViewById(R.id.textview_run_speed);
            calsText =  dataCard.findViewById(R.id.textview_run_calories);
            durationText = dataCard.findViewById(R.id.textview_run_calories);
        }

        // because it is just being created we set it to inactive
        workoutActive = false;

        // setting wakelock element for later acquirement
        PowerManager powerManager = (PowerManager) activity.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RunTracking");

        // set location manager up
        fusedLocProvider = LocationServices.getFusedLocationProviderClient(activity);

        // ask for location permissions if not already given
        if(!checkPermission()) getLocationPermissions();
        else {
            //Show instructions dialog if the user does squats for the first time
            if(DBQueryHelper.findAllRuns().isEmpty()){
                showInstructions();
            }

            // get the current user
            user = DBQueryHelper.findUser();

            initializeDatabaseObject();

            // setting up map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(mapReady);

            setUpSpeedUnitSwitcher();

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
                workoutActive = false;

                // starts a new line after pausing
                points.clear();
                if(mMap != null) line = mMap.addPolyline(lineOptions);
                previousLoc = null;

                updateDatabase();

                // change button design and move it
                setButtonDesign(
                        false,
                        getResources().getColor(R.color.darkerGreen),
                        R.drawable.ic_play_arrow_white_48dp,
                        startPauseButton
                );
                moveButtonLeft(true, startPauseButton);

                // show and animate stop button:
                makeButtonAppear(true, stopButton);

                // stop the timers
                runDurationTimer.stop();
                saveDataTimer.stop();

                // deactivate location updates and release wakelock
                wakeLock.release();
            }
            else{
                // tell mainactivity that a workout is in progress
                callback.workoutInProgress(true);
                workoutActive = true;

                // provide the user with a quick way of turning on GPS if it's off
                checkForGPS();

                // acquire wakelock with a 10 hour timeout, some runs could last that long i guess
                wakeLock.acquire(36000000);

                // start timers:
                if(runDurationTimer == null) {
                    // move button down on first click:
                    setupRunDurationTimer(
                            durationText,
                            speedText
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
                setButtonDesign(
                        true,
                        getResources().getColor(R.color.red),
                        R.drawable.ic_pause_white,
                        startPauseButton
                );
                if(firstClick){
                    moveStartButtonDown(true);
                    firstClick = false;
                }
                else {
                    moveButtonLeft(false, startPauseButton);
                    // hide stop button again
                    makeButtonAppear(false, stopButton);
                }
            }
        }

        private void checkForGPS() {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(activity);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            task.addOnFailureListener(activity, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(activity, 3341);
                        } catch (Exception sendEx) {
                            // Ignore the error.
                        }
                    }
                }
            });
        }
    };

    private void moveStartButtonDown(boolean go){
        startPauseButton.setClickable(false);
        startPauseButton.animate().yBy(toDp((go) ? 19 : -19));
        startPauseButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                startPauseButton.setClickable(true);
            }
        }, 300);
    }

    View.OnClickListener stopButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            callback.workoutInProgress(false);
            showWorkoutCompleteDialog();
        }
    };

    /**
     * sets up a clickable text view that switches the method of displaying the speed on click
     */
    private void setUpSpeedUnitSwitcher() {
        View.OnClickListener switchSpeedUnit = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch(unitMode){
                    case 1: // state 2 -> m/s
                        ((TextView)view).setText("-.--m/s");
                        break;
                    case 2: // state 3 -> average km/h
                        ((TextView)view).setText(getAverageSpeedString());
                        break;
                    default: // state 1 -> km/h
                        ((TextView)view).setText("-.--km/h");
                        unitMode = 0;
                        break;
                }
                unitMode++;
            }
        };
        speedText.setOnClickListener(switchSpeedUnit);
    }

    // should the user be tracked by the map camera?
    boolean allowUserTracking = false; // gets switched on start!
    boolean disableZooming = false;

    // keeps track of the last point that was used
    Location previousLoc = null;

    LocationCallback locationCallback = new LocationCallback() {

        // sets minimal precision for the location updates (in meters):
        final float initialTolerance = 10;
        float precisionTolerance = initialTolerance;

        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                    checkIfSuitedForPointDrawing(location);
            }
        }

        void checkIfSuitedForPointDrawing(Location loc){

            // zoom in on first found location, then just track if it's allowed
            if(allowUserTracking)
                if(disableZooming) setMapCam(loc);
                else {
                    disableZooming = true;
                    setMapCam(loc, 15);
                }

            if(workoutActive) {
                drawNewPointAndAddDistance(loc);

                if (unitMode != 3 && loc.hasSpeed()) { //unitMode != 3: no average speed
                    // set speed if the location manager can provide those readings
                    float curSpeed = loc.getSpeed();
                    if (unitMode == 1) { //unitMode 1: km/h
                        curSpeed *= 3.6f;
                        speedText.setText(decNumToXPrecisionString(curSpeed, 1).concat("km/h"));
                    } else    // unitMode 2: m/s
                        speedText.setText(decNumToXPrecisionString(curSpeed, 1).concat("m/s"));
                }
            }
        }

        private void drawNewPointAndAddDistance(Location loc) {
            float distToPrevLoc = (previousLoc == null) ? 11 : loc.distanceTo(previousLoc);
            // set a point if accuracy is good enough and last point is at least 10m away
            if (distToPrevLoc > 10 && loc.getAccuracy() <= precisionTolerance) {
                LatLng curPos = new LatLng(loc.getLatitude(), loc.getLongitude());
                points.add(curPos);
                if (line != null && !points.isEmpty()) line.setPoints(points);
                if (previousLoc != null) {
                    runDistance += distToPrevLoc;
                    distanceText.setText(decNumToXPrecisionString(runDistance / 1000, 2));
                    if (allowDataUpdate)
                        updateDatabase();
                }
                previousLoc = loc;

                // set Calories
                calsText.setText(decNumToXPrecisionString(getCurrentCalories(), 1));

                // reset tolerance to initial value
                precisionTolerance = initialTolerance;
            }
            else if (distToPrevLoc > 100 && distToPrevLoc > precisionTolerance + 25)
                precisionTolerance += 5;
                // increase tolerance drastically if the previous point is more than 100 meters away
            else if (previousLoc == null)
                precisionTolerance++;
                // increase tolerance quickly if this is the first location (1 meter every update)
            else
                precisionTolerance += 0.17f;
            // increases tolerance by 0.17 meters on every location update:
        }
    };

    private void setMapCam(Location loc, float zoom) {
        if(mMap != null && loc != null){
            LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
        }
    }
    private void setMapCam(Location loc){
        setMapCam(loc, mMap.getCameraPosition().zoom);
    }

    public double getAverageSpeedInKmh() {
        return (runDuration != 0)? (runDistance / 1000.0) / (runDuration / 3600000.0) : 0;
        //return 0 if run duration is 0 to avoid division by zero
    }
    public String getAverageSpeedString() {
        return "Ø " + decNumToXPrecisionString(getAverageSpeedInKmh(), 1) + "km/h";
    }

    private double getCurrentCalories() {
        // since all the big formulas that consider run duration, age, gender and other factors seem
        // to be flawed, we will just use the good old and probably most efficient MET based
        // formula for different speeds:
        // Kcal ~= METS * weight in kg * running duration in hours
        double milesPerHour = (runDuration>0)?(runDistance*0.6213711922373339)/(runDuration/3600000.0):0;
        double mets = 1.5; // base value that's suitable for standing and very slow walking speeds too
        // rough approximation for mets:
        if(milesPerHour >= 1)
            mets *= milesPerHour;
        return mets * user.getWeightInKG() * (runDuration / 3600000.0);

    }

    OnMapReadyCallback mapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            lineOptions = new PolylineOptions().color(getResources().getColor(R.color.red)).width(6);
            line = mMap.addPolyline(lineOptions);

            if(checkPermission()) mMap.setMyLocationEnabled(true);

            // zoom to last known location, if available
            fusedLocProvider.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                setMapCam(location, 10);
                            }
                        }
                    });

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
                            // asks if you want to navigate to this app's settings,
                            // to remove the permanent permission denial
                            getYesNoDialog("Das Lauf-Workout funktioniert nicht ohne Standort " +
                                            "Bestimmung! Es scheint so als hätten Sie die " +
                                            "Berechtigungen dafür permanent deaktiviert.\n" +
                                            "Möchten Sie das Einstellungs Menü dieser App öffnen um " +
                                            "diese Einstellung zu ändern?",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            sendToAppSettings();
                                        }
                                    }, null);
                        }
                        // tell the user that location permissions are required to use the run workout!
                        else getYesNoDialog("Leider funktioniert das Lauf-Workout nicht " +
                                "ohne Berechtigungen für die Standortbestimmung!\n" +
                                "Berechtigungs-Erteilung nochmal aufrufen?",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        getLocationPermissions();
                                    }
                                }, null);
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
                showInstructions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showInstructions(){
        new InstructionsDialog(getContext(),
                getString(R.string.run),
                getActivity().getDrawable(R.drawable.run_instruction),
                getString(R.string.run_instructions)).show();
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

    private void fragmentReset() {
        saveDataTimer.stop();
        saveDataTimer = null;
        runDistance = 0;
        points.clear();
        mMap.clear();
        distanceText.setText("0.00");
        calsText.setText("0.0");
        durationText.setText("0:00");
        makeButtonAppear(false, stopButton);
        moveButtonLeft(false, startPauseButton);
        moveStartButtonDown(false);
        firstClick = true;
        disableZooming = false;
        database = null;
        initializeDatabaseObject();
    }

    public void updateDatabase() {
        allowDataUpdate = false;/* since it's going to be updated now we can block all other update
                    attempts for now, this actually prevents simultaneous write conflicts as well */
        saveDataTimer.reset(); // resetting timer so that the next update can only happen after a minute

        database.setDistanceInMeters(runDistance);
        database.setDurationInMillis(runDuration);
        database.setCalories(getCurrentCalories());
        database.save();

        // set as last workout
        new SharedPrefsHelper(getContext())
                .setLastActivity(Constants.WORKOUT_RUN, database.getId());
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
            if (wakeLock != null && wakeLock.isHeld())
                wakeLock.release();
            fusedLocProvider.removeLocationUpdates(locationCallback);
            if(saveDataTimer != null) saveDataTimer.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) activity)
                .setActionBarTitle(getString(R.string.run));

        // set up user tracker if location permissions are given
        if(checkPermission()) {
            fusedLocProvider.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null /* Looper */);
            if(saveDataTimer != null) saveDataTimer.resume();
        }
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
                        String.format("Ich habe bei meinem letzten Lauftraining %.2fkm zurückgelegt!", runDistance / 1000));

                resetAndNavigateToNewTabIfClicked();
            }
        };

        DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resetAndNavigateToNewTabIfClicked();
            }
        };

        AlertDialog dialog = Util.getWorkoutCompleteDialog(getActivity(), database, customDialogLayout, positive, negative);
        dialog.show();
    }

    /**
     * set to initial state & go to tab if one was clicked before ending the workout
     */
    private void resetAndNavigateToNewTabIfClicked(){
        fragmentReset();
        callback.setNavigationItem();
    }

    /**
     * sends you to the android settings section for this app
     */
    private void sendToAppSettings() {
        startActivity(new Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.getPackageName(), null))
        );
    }

    /**
     * Build and displays a simple yes / no dialog
     * @param msg the message that should be displayed
     * @param yesListener listener with the method that should be called on yes click
     * @param noListener same as yesListener for the no option
     */
    private void getYesNoDialog(String msg,
                                DialogInterface.OnClickListener yesListener,
                                DialogInterface.OnClickListener noListener)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
        alertBuilder.setMessage(msg);
        alertBuilder.setNegativeButton("NEIN", noListener);
        alertBuilder.setPositiveButton("JA", yesListener);
        alertBuilder.create().show();
    }
}
