package com.cloudbees.demo.gasp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.adapter.GaspDatabase;
import com.cloudbees.demo.gasp.gcm.GCMIntentService;
import com.cloudbees.demo.gasp.gcm.GaspRegistrationClient;
import com.cloudbees.demo.gasp.location.GaspPlaces;
import com.cloudbees.demo.gasp.location.GaspSearch;
import com.cloudbees.demo.gasp.model.Place;
import com.cloudbees.demo.gasp.model.PlaceDetails;
import com.cloudbees.demo.gasp.model.Places;
import com.cloudbees.demo.gasp.model.Query;
import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.model.SearchResult;
import com.cloudbees.demo.gasp.service.RestaurantSyncService;
import com.cloudbees.demo.gasp.service.ReviewSyncService;
import com.cloudbees.demo.gasp.service.UserSyncService;
import com.cloudbees.demo.gasp.twitter.TwitterAuthentication;
import com.cloudbees.demo.gasp.utils.LocationServices;
import com.cloudbees.demo.gasp.utils.Network;
import com.cloudbees.demo.gasp.utils.PlayServices;
import com.cloudbees.demo.gasp.utils.Preferences;
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
    private float mZoom = 16;

    // Incremental search results from Google Places API
    private SearchResult mSearchResult = new SearchResult();
    // Next page token for Google Places API queries
    private String mPageToken = "";

    // Bundle serialization keys
    private static final String PAGE_TOKEN = "PAGE_TOKEN";
    private static final String SEARCH_RESULT = "SEARCH_RESULT";
    private static final String ZOOM_LEVEL = "ZOOM_LEVEL";

    // Gasp proxy objects
    private GaspSearch mGaspSearch = new GaspSearch() {
        @Override
        public void onSuccess(Places places) {
            mSearchResult.add(places);
            showLocations(mSearchResult);
            checkToken(places);
        }

        @Override
        public void onFailure(String status) {
            Log.e(TAG, "Google Places API search failed: status = " + status);
        }
    };
    private GaspPlaces mGaspPlaces = new GaspPlaces() {
        @Override
        public void onSuccess(PlaceDetails placeDetails) {
            launchPlacesDetailActivity(placeDetails);
        }

        @Override
        public void onFailure(String status) {
            Log.e(TAG, "Google Places API search failed: status = " + status);
        }
    };
    private GaspDatabase mGaspDatabase = new GaspDatabase(this);

    // Map GoogleMap Markers to Place Ids
    private HashMap<String, String> mPlacesMap = new HashMap<String, String>();
    // Map Place Ids to Reference strings
    private HashMap<String, String> mReferencesMap = new HashMap<String, String>();

    // On initial load, we need to wait for Gasp data sync before drawing location markers
    private static boolean waitForSync = true;
    public static final String SYNC_COMPLETED = "gasp-sync";

    // Proxy to handle Gasp GCM registration services
    private GaspRegistrationClient mGaspRegistrationClient = new GaspRegistrationClient();


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
     * Button Listener for "More Locations" button
     */
    private void addButtonListener() {
        Button placesButton = (Button) findViewById(R.id.places_button);
        placesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocations();
            }
        });
    }

    /**
     * Click listener for Google Maps markers
     */
    private void setMarkerClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "Place Id: " + mPlacesMap.get(marker.getId()));
                Log.d(TAG, "Reference: " + mReferencesMap.get(mPlacesMap.get(marker.getId())));
                mGaspPlaces.placeDetails(new Query(mReferencesMap.get(mPlacesMap.get(marker.getId()))));
                return false;
            }
        });
    }

    /**
     * Set Camera for Google Maps API
     */
    private void setCamera() {
        try {
            LatLng myLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)
                    .zoom(mZoom)
                    .bearing(0)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Do Google Places API search (centred on current location, radius from shared preferences)
     */
    private void getLocations() {
        try {
            Preferences preferences = new Preferences(this);
            Query query = new Query(mLocation.getLatitude(),
                    mLocation.getLongitude(),
                    preferences.getGaspSearchRadius(),
                    mPageToken);
            mGaspSearch.nearbySearch(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Display location via Google Maps API
     * @param searchResult Gasp! Locations to display
     */
    private void showLocations(SearchResult searchResult) {
        Restaurant restaurant;
        float markerColour;
        try {
            for (Place place : searchResult.getPlaces()) {
                LatLng pos = new LatLng(place.getGeometry().getLocation().getLat().doubleValue(),
                        place.getGeometry().getLocation().getLng().doubleValue());

                restaurant = mGaspDatabase.getRestaurantByPlacesId(place.getId());
                if (restaurant != null)
                    markerColour = BitmapDescriptorFactory.HUE_GREEN;
                else
                    markerColour = BitmapDescriptorFactory.HUE_RED;

                Marker marker =
                        mMap.addMarker(new MarkerOptions().position(pos)
                                .title(place.getName())
                                .icon(BitmapDescriptorFactory.defaultMarker(markerColour)));
                Log.d(TAG, place.getName() + " " + pos.toString());
                mPlacesMap.put(marker.getId(), place.getId());
                mReferencesMap.put(place.getId(), place.getReference());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for Google Places API search result for next_page_token
     * See @link{https://developers.google.com/places/documentation/search} for details
     * @param places Google Places API return (converted from JSON)
     */
    private void checkToken(Places places) {
        try {
            if (places.getNext_page_token() == null) {
                mPageToken = "";
                setPlacesButton(false);
            } else {
                mPageToken = places.getNext_page_token();
                Log.d(TAG, "Next page token: " + mPageToken);
                setPlacesButton(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch child activity to display details for selected Gasp! location
     * @param placeDetails
     */
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

    /**
     * Enable/disable "More Locations" button
     * @param enableButton enable or disable button
     */
    private void setPlacesButton(boolean enableButton) {
        Button placesButton = (Button) findViewById(R.id.places_button);
        placesButton.setText("More Locations");
        if (enableButton) {
            placesButton.setEnabled(true);
        } else {
            placesButton.setEnabled(false);
        }
    }

    /**
     * BroadcastReceiver for notification that Gasp! data sync completed
     */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Restaurant sync completed");
            if (waitForSync) {
                getLocations();
                waitForSync = false;
            }
        }
    };

    /**
     * Load Google Maps view/fragment, set location and button/click listeners
     */
    private void prepareMapView() {
        setContentView(R.layout.gasp_locations_layout);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        setCamera();
        addButtonListener();
        setMarkerClickListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGaspRegistrationClient.registerGCM(this);
        TwitterAuthentication.requestTwitterOAuthToken(this);
        LocationServices.enableLocationChecking(this);
        mLocation = LocationServices.getLocation(this);

        if (savedInstanceState != null) {
            // Restore incremental search results, page_token and zoom level
            mSearchResult = (SearchResult) savedInstanceState.getSerializable(SEARCH_RESULT);
            mPageToken = savedInstanceState.getString(PAGE_TOKEN);
            mZoom = savedInstanceState.getFloat(ZOOM_LEVEL);

            // Display locations and enable/disable "More Locations" button
            prepareMapView();
            showLocations(mSearchResult);
            if (! mPageToken.isEmpty()) {
                setPlacesButton(true);
            }
        }

        // First time only: Sync Gasp data before Location search
        else {
            // Gasp requires Play Services and network connectivity
            if (PlayServices.checkPlayServices(this) && Network.checkNetworking(this)) {
                // Register listener for notification that restaurant data has been synced
                LocalBroadcastManager.getInstance(this)
                        .registerReceiver(mMessageReceiver, new IntentFilter(SYNC_COMPLETED));
                startDataSyncServices();

                prepareMapView();

                // Only show location markers once Gasp restaurant data loaded
                if (!waitForSync) {
                    getLocations();
                }
            }
            else {

                Log.e(TAG, "Cannot launch LocationsActivity");
                finish();
            }
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save incremental search results, page_token and zoom level
        outState.putSerializable(SEARCH_RESULT, mSearchResult);
        outState.putString(PAGE_TOKEN, mPageToken);
        outState.putFloat(ZOOM_LEVEL, mMap.getCameraPosition().zoom);
    }
}
