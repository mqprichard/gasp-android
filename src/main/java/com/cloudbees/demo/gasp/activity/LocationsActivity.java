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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.location.GaspPlaces;
import com.cloudbees.demo.gasp.location.GaspSearch;
import com.cloudbees.demo.gasp.model.GaspDatabase;
import com.cloudbees.demo.gasp.fragment.LocationFragment;
import com.cloudbees.demo.gasp.fragment.TwitterAuthenticationFragment;
import com.cloudbees.demo.gasp.gcm.GCMIntentService;
import com.cloudbees.demo.gasp.gcm.GaspRegistrationClient;
import com.cloudbees.demo.gasp.model.Place;
import com.cloudbees.demo.gasp.model.PlaceDetails;
import com.cloudbees.demo.gasp.model.Places;
import com.cloudbees.demo.gasp.model.Query;
import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.service.RestaurantSyncService;
import com.cloudbees.demo.gasp.service.ReviewSyncService;
import com.cloudbees.demo.gasp.service.UserSyncService;
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
    private Location mLocation;

    // Next page token for Google Maps API queries
    private static String token = "";

    // Gasp proxy objects
    private GaspSearch search = new GaspSearch() {
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
    private GaspPlaces details = new GaspPlaces() {
        @Override
        public void onSuccess(PlaceDetails placeDetails) {
            launchPlacesDetailActivity(placeDetails);
        }

        @Override
        public void onFailure(String status) {
            Log.e(TAG, "Google Places API search failed: status = " + status);
        }
    };
    private GaspDatabase database = new GaspDatabase(this);

    // Map GoogleMap Markers to Place Ids
    private HashMap<String, String> mPlacesMap = new HashMap<String, String>();
    // Map Place Ids to Reference strings
    private HashMap<String, String> mReferencesMap = new HashMap<String, String>();

    // Base URL of the Gasp! GCM Push Server
    private static String mGaspPushServerUrl;
    public static String getGaspPushServerUrl() {
        return mGaspPushServerUrl;
    }

    // Proxy to handle Gasp GCM registration services
    private GaspRegistrationClient mGaspRegistrationClient = new GaspRegistrationClient();

    public LocationsActivity() {
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Check for location services and get current location
     */
    private void enableLocationChecking() {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

                // Check current location
                Location location = LocationFragment.getLocation(this);
                if (location != null) {
                    Log.d(TAG, "Location: " + String.format("%.6f", location.getLatitude())
                            + ", " + String.format("%.6f", location.getLongitude())
                            + " (via " + location.getProvider() + ")" + '\n');
                }

                // Add LocationFragment to enable location updates
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                LocationFragment locationFragment = new LocationFragment();
                ft.add(locationFragment, "LocationFragment");
                ft.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start Gasp data synchronization services
     * Handle initial REST sync and GCM updates
     */
    private void startDataSyncServices() {
        Intent reviewsIntent = new Intent(this, ReviewSyncService.class);
        reviewsIntent.putExtra(GCMIntentService.PARAM_IN_MSG, "reviews");
        startService(reviewsIntent);

        Intent restaurantsIntent = new Intent(this, RestaurantSyncService.class);
        restaurantsIntent.putExtra(GCMIntentService.PARAM_IN_MSG, "restaurants");
        startService(restaurantsIntent);

        Intent usersIntent = new Intent(this, UserSyncService.class);
        usersIntent.putExtra(GCMIntentService.PARAM_IN_MSG, "users");
        startService(usersIntent);
    }

    /**
     * Load shared preferences from res/xml/preferences.xml (first time only)
     * Subsequent activations will use the saved shared preferences from the device
     */
    private void getGaspSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "Using Gasp Server URI: "
                + gaspSharedPreferences.getString(getString(R.string.gasp_server_uri_base), ""));

        mGaspPushServerUrl = gaspSharedPreferences.getString(getString(R.string.gasp_push_uri_preferences), "");
        Log.i(TAG, "Using Gasp Push Server URI: " + mGaspPushServerUrl);
    }

    /**
     * Request a Twitter API v1.1 OAuth Token
     * Uses TwitterAuthenticationFragment
     */
    private void requestTwitterOAuthToken() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TwitterAuthenticationFragment responder =
                (TwitterAuthenticationFragment) fm.findFragmentByTag("TwitterAuthentication");
        if (responder == null) {
            responder = new TwitterAuthenticationFragment();

            ft.add(responder, "TwitterAuthentication");
        }
        ft.commit();
    }

    private void addThirdPartyLibs() {
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
                //mDetailsFragment.placeDetails(new Query(mReferencesMap.get(mPlacesMap.get(marker.getId()))));
                details.placeDetails(new Query(mReferencesMap.get(mPlacesMap.get(marker.getId()))));
                return false;
            }
        });
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

            //restaurant = mDatabaseFragment.getRestaurantByPlacesId(place.getId());
            restaurant = database.getRestaurantByPlacesId(place.getId());
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
            //mSearchFragment.nearbySearch(query);
            search.nearbySearch(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPlayServices()) {

            addThirdPartyLibs();
            enableLocationChecking();
            getGaspSharedPreferences();
            startDataSyncServices();

            mGaspRegistrationClient.registerGCM(this);
            requestTwitterOAuthToken();

            setContentView(R.layout.gasp_locations_layout);
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            setLocation();
            setCamera();
            addButtonListener();
            setMarkerClickListener();
            getLocations();
        }
        else {
            Log.e(TAG, "Cannot launch LocationsActivity");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // (Re-)register with Gasp GCM  Server
            case R.id.options_register:
                mGaspRegistrationClient.doRegisterGasp(this);
                return true;

            // Unregister with Gasp GCM Server
            case R.id.options_unregister:
                mGaspRegistrationClient.doUnregisterGasp(this);
                return true;

            case R.id.options_exit:
                finish();
                return true;

            case R.id.gasp_settings:
                Intent intent = new Intent();
                intent.setClass(LocationsActivity.this, SetPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_menu_twitter:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, TwitterStreamActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_reviews_data:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, ReviewListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_restaurants_data:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, RestaurantListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_users_data:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, UserListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_menu_places:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, PlacesActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_login_with_amazon:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, AmazonSignInActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_login_with_google:
                intent = new Intent();
                intent.setClass(LocationsActivity.this, GoogleSignInActivity.class);
                startActivityForResult(intent, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        mGaspRegistrationClient.doUnregisterGasp(this);
        super.onDestroy();
    }
}
