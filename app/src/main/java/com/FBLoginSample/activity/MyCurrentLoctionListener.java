package com.FBLoginSample.activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyCurrentLoctionListener implements LocationListener {


    public static LatLng me;

    @Override
    public void onLocationChanged(Location location) {
        location.getLatitude();
        location.getLongitude();

        String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();

        me = new LatLng(location.getLatitude(),location.getLongitude());

        //I make a log to see the results
        Log.e("MY CURRENT LOCATION", myLocation);
        com.FBLoginSample.activity.NearbyFragment.latlangtxt.setText(myLocation);
        com.FBLoginSample.activity.NearbyFragment.map.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 15));

        // Zoom in, animating the camera.
        //com.FBLoginSample.activity.NearbyFragment.map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        Marker ciu = NearbyFragment.map.addMarker(new MarkerOptions().position(me).title("I am here"));
        /* to zoom on marker -- delete it if you want --> halima  */

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}