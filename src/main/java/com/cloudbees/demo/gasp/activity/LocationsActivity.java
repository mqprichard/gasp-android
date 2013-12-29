package com.cloudbees.demo.gasp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
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
import com.cloudbees.demo.gasp.fragment.GaspDatabaseFragment;
import com.cloudbees.demo.gasp.fragment.NearbySearchFragment;
import com.cloudbees.demo.gasp.fragment.PlaceDetailsFragment;
import com.cloudbees.demo.gasp.model.Place;
import com.cloudbees.demo.gasp.model.PlaceDetails;
import com.cloudbees.demo.gasp.model.Places;
import com.cloudbees.demo.gasp.model.Query;
import com.cloudbees.demo.gasp.model.Restaurant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

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
    private PlaceDetailsFragment mDetailsFragment;
    private GaspDatabaseFragment mDatabaseFragment;

    // Current Location
    private Location mLocation;
    private static String token = "";

    // Map GoogleMap Markers to Place Ids
    private HashMap<String, String> mPlacesMap = new HashMap<String, String>();
    // Map Place Ids to Reference strings
    private HashMap<String, String> mReferencesMap = new HashMap<String, String>();

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
            setMarkerClickListener();
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
            @Override
            public void onSuccess(Places places) {
                showLocations(places);
                checkToken(places);
            }

            @Override
            public void onFailure(String status) {
                Log.e(TAG, "Google Places API search failed: status = " + status);
            }
        };
        mDetailsFragment = new PlaceDetailsFragment() {
            @Override
            public void onSuccess(PlaceDetails placeDetails) {
                launchPlacesDetailActivity(placeDetails);
            }

            @Override
            public void onFailure(String status) {
                Log.e(TAG, "Google Places API search failed: status = " + status);
            }
        };
        mDatabaseFragment = new GaspDatabaseFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(mSearchFragment, getString(R.string.fragment_location_search));
        ft.add(mDatabaseFragment, getString(R.string.fragment_gasp_database));
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
        Restaurant restaurant;
        float markerColour;

        for (Place place : places.getResults()) {
            LatLng pos = new LatLng(place.getGeometry().getLocation().getLat().doubleValue(),
                    place.getGeometry().getLocation().getLng().doubleValue());

            restaurant = mDatabaseFragment.getRestaurantByPlacesId(place.getId());
            if (restaurant != null)
                markerColour = BitmapDescriptorFactory.HUE_GREEN;
            else
                markerColour = BitmapDescriptorFactory.HUE_RED;

            Marker marker = mMap.addMarker(new MarkerOptions().position(pos)
                    .title(place.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
            Log.d(TAG, place.getName() + " " + pos.toString());
            mPlacesMap.put(marker.getId(), place.getId());
            mReferencesMap.put(place.getId(), place.getReference());
        }
    }

    private void launchPlacesDetailActivity(PlaceDetails placeDetails) {
        try {
            Intent intent = new Intent();
            intent.setClass(LocationsActivity.this, PlacesDetailActivity.class);
            intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_SERIALIZED, placeDetails.getResult());
            intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_REFERENCE, mReferencesMap.get(placeDetails.getResult().getId()));
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void setMarkerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "Place Id: " + mPlacesMap.get(marker.getId()));
                Log.d(TAG, "Reference: " + mReferencesMap.get(mPlacesMap.get(marker.getId())));
                mDetailsFragment.placeDetails(new Query(mReferencesMap.get(mPlacesMap.get(marker.getId()))));
                return false;
            }
        });
    }

}
