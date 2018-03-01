package com.codecamp.bitfit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends WorkoutFragment {

    public static RunFragment getInstance() {
        RunFragment fragment = new RunFragment();

        return fragment;
    }

    public RunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReady);

        final FloatingActionButton startStopBut = getView().findViewById(R.id.button_start_stop_run);
        startStopBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startStopBut.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BB0000")));
                startStopBut.setImageResource(R.drawable.ic_stop_white_48dp);
            }
        });
    }

    private GoogleMap mMap;
    PolylineOptions lineOptions = new PolylineOptions().color(Color.RED).width(3);
    Polyline line;
    List<LatLng> points = new ArrayList<>();

    OnMapReadyCallback mapReady = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            User user = DBQueryHelper.findUser();
            user.getAge();

            line = mMap.addPolyline(lineOptions);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);

                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    // TODO:notify user that GPS should be used for proper precision, network location services aren't suited for this purpose
                }
                if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    // TODO:notify user that location services are off, pop up a button that allows the user to quickly navigate to the location settings
                }
                else{


                    //lm.removeUpdates(this);
                    //wakeLock.release();

                    PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RunTracking");
                    wakeLock.acquire();

                    LocationListener locListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if(location != null && location.getAccuracy() <= 25){
                                LatLng curPos = new LatLng(location.getLatitude(),location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
                                points.add(curPos);
                                line.setPoints(points);
                                if(previousLoc != null){
                                    runningDistance += location.distanceTo(previousLoc);
                                }
                                else{
                                    LatLng firstLocLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocLatLng, 15));
                                }
                                previousLoc = location;
                            }
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {}
                        @Override
                        public void onProviderEnabled(String s) {}
                        @Override
                        public void onProviderDisabled(String s) {}
                    };

                    lm.requestLocationUpdates(1000, 25, criteria, locListener, null);
                }
            }
            else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                //TODO start the map after getting the permission
            }
        }
    };

    float runningDistance = 0;
    Location previousLoc = null;

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_statistics:
                // TODO start statistics activity
                View but = getView().findViewById(R.id.button_share_highscore_squats);
                Bitmap image = viewToBitmap(but);
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                ShareDialog.show(getActivity(), content);
                return true;
            case R.id.action_share:
                // TODO start share intent

                String googleMapsLink = "https://www.google.com/maps/dir/";
                for (int i = 0; i < points.size(); i++) {
                    googleMapsLink = googleMapsLink + points.get(i).latitude + "," + points.get(i).longitude + "/";
                }

                ShareLinkContent content1 = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(googleMapsLink + "data=!3m1!4b1!4m2!4m1!3e2"))
                        .setQuote("")
                        .build();

                ShareDialog.show(getActivity(), content1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.run));
    }
}
