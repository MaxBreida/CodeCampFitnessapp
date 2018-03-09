package com.codecamp.bitfit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.statistics.RunStatisticsActivity;
import com.codecamp.bitfit.util.CountUpTimer;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;
import static com.codecamp.bitfit.util.Util.decNumToXPrecisionString;

/**
 * A simple {@link Fragment} subclass.
 */

// TODO: test offline functionality, implement if necessary
public class RunFragment extends WorkoutFragment {

    Polyline line; // the line that represents the running track
    List<LatLng> points = new ArrayList<>(); // a list of points of the running track
    GoogleMap mMap; // an instance of a google map client
    LocationManager lm; // location manager that keeps track of current location
    PowerManager powerManager; // required for wakelock creation
    PowerManager.WakeLock wakeLock; // a wakelock to keep the device running for location updates
    FragmentActivity activity; // avoid using getActivity() all the time
    View mainView; // avoid using getView() each time it's needed
    CountUpTimer runDurationTimer;
    long runDuration = 0;
    View dataCard;

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
        // TODO: else unexpected error notifications

        // setting up map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReady);

        // setting wakelock
        powerManager = (PowerManager) activity.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RunTracking");

        // setting up the start / stop button
        mainView.findViewById(R.id.button_start_stop_run).setOnClickListener(startStopButListener);
    }

    View.OnClickListener startStopButListener = new View.OnClickListener() {
        FloatingActionButton startStopButton;

        @Override
        public void onClick(View view) {
            startStopButton = (FloatingActionButton) view;
            if(startStopButton.isActivated()){
                setButton(false, Color.parseColor("#008800"), R.drawable.ic_play_arrow_white_48dp);

                runDurationTimer.stop();

                lm.removeUpdates(locListener);
                wakeLock.release();
            }
            else{
                setButton(true, Color.parseColor("#BB0000"), R.drawable.ic_stop_white_48dp);

                wakeLock.acquire(36000000);

                if(runDurationTimer == null)
                    setupTimer((TextView) dataCard.findViewById(R.id.textview_run_duration));
                else
                    runDurationTimer.reset();

                runDurationTimer.start();

                startRunIfAllIsSet();
            }
        }

        private void startRunIfAllIsSet() {
            if (checkPermission()) {
                lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    // TODO: notify user that GPS should be used for proper precision, network location services aren't suited for this purpose
                }
                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // TODO: notify user that location services are off, pop up a button that allows the user to quickly navigate to the location settings
                }
                else{
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    lm.requestLocationUpdates(1000, 25, criteria, locListener, null);
                }
            }
        }

        private void setButton(boolean active, int color, int resId){
            startStopButton.setActivated(active);
            startStopButton.setBackgroundTintList(ColorStateList.valueOf(color));
            startStopButton.setImageResource(resId);
        }
    };

    LocationListener locListener = new LocationListener() {

        float runningDistance = 0;
        Location previousLoc = null;

        @Override
        public void onLocationChanged(Location location) {
            if(location != null && location.getAccuracy() <= 25){
                // set a point if accuracy is good enough
                LatLng curPos = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
                points.add(curPos);
                line.setPoints(points);
                if(previousLoc != null){
                    runningDistance += location.distanceTo(previousLoc);
                    TextView distanceText = dataCard.findViewById(R.id.textview_run_distance);
                    distanceText.setText(decNumToXPrecisionString(runningDistance/1000, 2));
                }
                else{
                    LatLng firstLocLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocLatLng, 15));
                }
                previousLoc = location;

                if(location.hasSpeed()){
                    // set speed if the location manager can provide those readings
                    TextView speedText = dataCard.findViewById(R.id.textview_run_speed);
                    float curSpeed = location.getSpeed();
                    speedText.setText(decNumToXPrecisionString(curSpeed, 1).concat("km/h"));
                }

                // set Calories
                TextView calsText = dataCard.findViewById(R.id.textview_run_calories);
                calsText.setText(decNumToXPrecisionString(getCurrentCalories(),1));
            }
        }

        private double getCurrentCalories() {
            // TODO: lots of testing & why does it go negative? Dumb formula?
            // M: [(Age * 0.2017) - (Weight * 0.09036) + (Heart Rate * 0.6309) - 55.0969] * Time / 4.184
            // F: [(Age * 0.074 ) - (Weight * 0.05741) + (Heart Rate * 0.4472) - 20.4022] * Time / 4.184
            // HR: bpm = (46 * kmh) / 8.04672 + 80   (Detailed explanations in the documentation)
            User user = DBQueryHelper.findUser();
            boolean m = user.isMale();
            double avgSpeed = (runningDistance / 1000.0) / (runDuration / 3600000.0);
            double heartRate = avgSpeed * (46 / 8.04672) + 80;
            double ageParameter = user.getAge() * ((m) ? 0.2017 : 0.074);
            double weightParameter = user.getWeightInLbs() * ((m) ? 0.09036 : 0.05741);
            double heartRateParameter = heartRate * ((m) ? 0.6309 : 0.4472);
            heartRateParameter -= ((m) ? 55.0969 : 20.4022);
            double cals = ageParameter - weightParameter + heartRateParameter;
            cals *= (runDuration / (4.184 * 60000));
            if(cals < 0) cals = 0;
            return cals;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        @Override
        public void onProviderEnabled(String s) {}
        @Override
        public void onProviderDisabled(String s) {}
    };

    OnMapReadyCallback mapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            PolylineOptions lineOptions = new PolylineOptions().color(Color.RED).width(3);
            line = mMap.addPolyline(lineOptions);

            if(checkPermission()) mMap.setMyLocationEnabled(true);
        }
    };

    private boolean checkPermission() {
        if(permissionGranted())
            return true;
        else{
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return false;
        }
    }

    private boolean permissionGranted() {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_statistics:
                // TODO start statistics activity.
                activity.startActivity(new Intent(getActivity(), RunStatisticsActivity.class));
                return true;
            case R.id.action_share:
                // TODO ask which sharing method the user wants and use the right one:
                if(true) { // picture of view method
                    // TODO lots of testing + persistent cardview values
                    shareFragmentViewOnClick(dataCard);
                }
                else{ // link method TODO if we use this, the link needs to be checked (can't be too long)
                    StringBuilder googleMapsLink = new StringBuilder("https://www.google.com/maps/dir/");
                    for (int i = 0; i < points.size(); i++) {
                        googleMapsLink.append(points.get(i).latitude).append(",").append(points.get(i).longitude).append("/");
                    }

                    ShareLinkContent content1 = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(googleMapsLink.toString() + "data=!3m1!4b1!4m2!4m1!3e2"))
                            .setQuote("")
                            .build();

                    ShareDialog.show(activity, content1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupTimer(final TextView view){
        runDurationTimer = new CountUpTimer(1000, view) {
            @Override
            public void onTick(long elapsedTime) {
                runDuration = elapsedTime;
                view.setText(Util.getMillisAsTimeString(elapsedTime));
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) activity)
                .setActionBarTitle(getString(R.string.run));
    }
}
