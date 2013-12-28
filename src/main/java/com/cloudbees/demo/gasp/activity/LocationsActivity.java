package com.cloudbees.demo.gasp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.fragment.NearbySearchFragment;
import com.cloudbees.demo.gasp.model.Place;
import com.cloudbees.demo.gasp.model.Places;
import com.cloudbees.demo.gasp.model.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LocationsActivity extends FragmentActivity {
    private static final String TAG = LocationsActivity.class.getName();

    private GoogleMap mMap;
    private NearbySearchFragment mSearchFragment;
    private Location mLocation;
    private static String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPlayServices()) {
            setContentView(R.layout.activity_locations);
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            setLocation();
            setCamera();
            addFragments();
            addButtonListener();
            getLocations();
        }
        else {
            Log.e(TAG, "Cannot launch LocationsActivity");
        }
    }

    private boolean checkPlayServices() {
        int playServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (playServicesAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else {
            Log.e(TAG, "Google Play Services not available: error code [" + playServicesAvailable + "]");
            return false;
        }
    }

    private void addFragments() {
        mSearchFragment = new NearbySearchFragment() {
            public void onSuccess(Places places) {
                showLocations(places);
                checkToken(places);
            }

            public void onFailure(String status) {
                Log.e(TAG, "Google Places API search failed: status = " + status);
            }
        };
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mSearchFragment, getString(R.string.fragment_location_search));
        ft.commit();

    }

    private void setCamera() {
        LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(myLocation)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void setLocation() {
        String svcName = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        mLocation = locationManager.getLastKnownLocation(provider);
        Log.i(TAG, "Current Latitude = " + mLocation.getLatitude());
        Log.i(TAG, "Current Longitude = " + mLocation.getLongitude());

        mMap.setMyLocationEnabled(true);
    }

    private void showLocations(Places places) {
        for (Place place: places.getResults()) {
            LatLng pos = new LatLng(place.getGeometry().getLocation().getLat().doubleValue(),
                                    place.getGeometry().getLocation().getLng().doubleValue());
            mMap.addMarker(new MarkerOptions().position(pos).title(place.getName()));
            Log.d(TAG, place.getName() + " " + pos.toString());
        }
    }

    private void checkToken(Places places) {
        try {
            Button placesButton = (Button) findViewById(R.id.places_button);
            if (places.getNext_page_token() == null) {
                token = "";
                Log.d(TAG, "No page token returned from Places API");
                placesButton.setEnabled(false);
            } else {
                token = places.getNext_page_token();
                Log.d(TAG, "Next page token: " + token);
                placesButton.setText(R.string.places_button_locations_next);
                placesButton.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocations() {
        try {
            SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int radius = Integer.valueOf(gaspSharedPreferences.getString(getString(R.string.places_search_radius_preferences), ""));
            Query query = new Query(mLocation.getLatitude(), mLocation.getLongitude(), radius, token);
            mSearchFragment.nearbySearch(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addButtonListener() {
        Button placesButton = (Button) findViewById(R.id.places_button);
        placesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocations();
            }
        });
    }
}
