package com.codecamp.bitfit.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.POWER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RunFragment extends WorkoutFragment implements OnMapReadyCallback, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

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


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private GoogleMap mMap;
    PolylineOptions lineOptions = new PolylineOptions().color(Color.RED).width(3);
    Polyline line;
    List<LatLng> points = new ArrayList<LatLng>();

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
                String provider = lm.getBestProvider(criteria,true);
                Location firstLoc = lm.getLastKnownLocation(provider);
                if(firstLoc == null){
                    /*TODO: notify user that there's no location data and that they should wait for a GPS signal*/
                }
                else{
                    PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"RunTracking");
                    wakeLock.acquire();

                    LatLng firstLocLatLng = new LatLng(firstLoc.getLatitude(), firstLoc.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocLatLng, 15));
                    lm.requestLocationUpdates(1000, 25, criteria, this, null);

                    //lm.removeUpdates(this);
                    //wakeLock.release();
                }
            }
        }
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
                try {
                    FileOutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory() + "testy.png");
                    image.compress(Bitmap.CompressFormat.PNG, 100, output);
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    float runningDistance = 0;
    Location previousPoint = null;

    @Override
    public void onLocationChanged(Location location) {
        if(location != null && location.getAccuracy() <= 25){
            LatLng curPos = new LatLng(location.getLatitude(),location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(curPos));
            points.add(curPos);
            line.setPoints(points);
            if(previousPoint != null){
                runningDistance += location.distanceTo(previousPoint);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    public Bitmap viewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

}
