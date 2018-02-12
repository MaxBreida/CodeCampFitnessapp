package com.codecamp.bitfit.activities;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Use alternative for the R. notation, master of layouts, I might need your assistance :D
        /*setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    PolylineOptions line = new PolylineOptions();
    List<LatLng> points;

    /*TODO: draw one smooth line instead of drawing several short thick lines .... use the points list!*/

    float a = 8, b = 54;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Kassel and move the camera
        LatLng kassel = new LatLng(51.3127, 9.4797);
        points.add(kassel);
        points.add(kassel);
        mMap.addMarker(new MarkerOptions().position(kassel).title("Marker in Kassel"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kassel));
        PolylineOptions kasline = new PolylineOptions();
        kasline.add(kassel);
        Polyline zeLine = mMap.addPolyline(kasline);
        drawLines();
    }

    private void drawLines() {
        final Handler han = new Handler();
        han.postDelayed(new Runnable() {
            @Override
            public void run() {
                line.add(new LatLng(b, a));
                a += 0.1;
                b -= 0.1;
                line.add(new LatLng(b, a));
                mMap.addPolyline(line);
                drawLines();
            }
        }, 1000);
    }
}
