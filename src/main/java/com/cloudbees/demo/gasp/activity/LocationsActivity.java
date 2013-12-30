package com.cloudbees.demo.gasp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.cloudbees.demo.gasp.fragment.GaspDatabaseFragment;
import com.cloudbees.demo.gasp.fragment.LocationFragment;
import com.cloudbees.demo.gasp.fragment.NearbySearchFragment;
import com.cloudbees.demo.gasp.fragment.PlaceDetailsFragment;
import com.cloudbees.demo.gasp.fragment.TwitterAuthenticationFragment;
import com.cloudbees.demo.gasp.gcm.GCMProjectKey;
import com.cloudbees.demo.gasp.gcm.GCMRegistration;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
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

    // Base URL of the Gasp! GCM Push Server
    private static String mGaspPushServerUrl;
    public static String getGaspPushServerUrl() {
        return mGaspPushServerUrl;
    }

    // Constants used for GCM Registration
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private ResponseReceiver mGaspMessageReceiver;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String regId;

    // BroadcastReceiver for Gasp sync/update/location messages
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String PARAM_IN_MSG = "gaspInMsg";
        public static final String PARAM_OUT_MSG = "gaspOutMsg";
        public static final String PARAM_ID = "id";
        public static final String ACTION_RESP =
                "com.cloudbees.demo.gasp.gcm.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(PARAM_OUT_MSG);
            Log.d(TAG, text);
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

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
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

    private void startDataSyncServices() {
        Intent reviewsIntent = new Intent(this, ReviewSyncService.class);
        reviewsIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "reviews");
        startService(reviewsIntent);

        Intent restaurantsIntent = new Intent(this, RestaurantSyncService.class);
        restaurantsIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "restaurants");
        startService(restaurantsIntent);

        Intent usersIntent = new Intent(this, UserSyncService.class);
        usersIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "users");
        startService(usersIntent);
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mGaspMessageReceiver = new ResponseReceiver();
        registerReceiver(mGaspMessageReceiver, filter);
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
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        } catch (NullPointerException e) {
            throw new RuntimeException("NPE getting package name: " + e);
        }
        return packageInfo.versionCode;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getPreferences(Context context) {
        return getSharedPreferences(LocationsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration Id not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM/Gasp Push Notification Server.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences. Use when first registering with GCM/Gasp Push Server.
     */
    private void registerGCM() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Register device with Google Cloud Messaging
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regId = getRegistrationId(context);

                    if (regId.isEmpty()) {
                        regId = gcm.register(GCMProjectKey.SENDER_ID);
                        Log.d(TAG, "Registered device: " + regId);
                    }
                    else
                        Log.d(TAG, "Device already registered: " + regId + '\n');

                    // Register with Gasp GCM Push Notification Server
                    boolean registered = GCMRegistration.register(context, regId, getGaspPushServerUrl());
                    if (registered)
                        Log.d(TAG, "Registered with server (" + mGaspPushServerUrl + "): " + regId);
                    else
                        Log.e(TAG, "Could not register with server (" + mGaspPushServerUrl + ")");

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    // TODO:
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Register Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    private void doRegisterGasp() {
        try {
            if (!(regId = getRegistrationId(context)).isEmpty()) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            if (GCMRegistration.register(context, regId, getGaspPushServerUrl()))
                                return ("Registered Device Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Registration failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.d(TAG, msg + "\n");
                    }
                }.execute(null, null, null);

            } else {
                Log.e(TAG, "Registration Id not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregister Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    private void doUnregisterGasp() {
        if (!(regId = getRegistrationId(context)).isEmpty()) {
            try {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            GCMRegistration.unregister(context, regId, getGaspPushServerUrl());
                            return ("Unregistered Device Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Unregister failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.d(TAG, msg + "\n");
                    }
                }.execute(null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Device not registered");
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPlayServices()) {
            context = getApplicationContext();

            addThirdPartyLibs();
            enableLocationChecking();
            getGaspSharedPreferences();
            registerBroadcastReceiver();
            startDataSyncServices();
            registerGCM();
            requestTwitterOAuthToken();

            setContentView(R.layout.gasp_locations_layout);
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
                doRegisterGasp();
                return true;

            // Unregister with Gasp GCM Server
            case R.id.options_unregister:
                doUnregisterGasp();
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
        doUnregisterGasp();
        unregisterReceiver(mGaspMessageReceiver);
        super.onDestroy();
    }
}
