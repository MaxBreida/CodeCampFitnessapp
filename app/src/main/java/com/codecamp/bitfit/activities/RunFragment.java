package com.codecamp.bitfit.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codecamp.bitfit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends Fragment implements OnMapReadyCallback {

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private GoogleMap mMap;
    PolylineOptions lineOptions = new PolylineOptions().color(Color.RED).width(3);
    Polyline line;
    List<LatLng> points = new ArrayList<LatLng>();
    //Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng kassel = new LatLng(51.3127, 9.4797);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kassel));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kassel, 15));
        mMap.setMaxZoomPreference(20);
        mMap.setMinZoomPreference(1);
        line = mMap.addPolyline(lineOptions);
        drawLines();
    }

    double a=51.3127, b=9.4797;

    private void drawLines() {
        final Handler han = new Handler();
        han.postDelayed(new Runnable() {
            @Override
            public void run() {
                points.add(new LatLng(a, b));
                a += 0.01;
                b -= 0.01;
                points.add(new LatLng(a, b));
                line.setPoints(points);
                drawLines();
            }
        }, 1000);
    }
}
