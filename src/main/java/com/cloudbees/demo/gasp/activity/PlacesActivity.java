package com.cloudbees.demo.gasp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.location.GaspNearbySearch;
import com.cloudbees.demo.gasp.location.GaspPlaceDetails;
import com.cloudbees.demo.gasp.fragment.LocationFragment;
import com.cloudbees.demo.gasp.model.Place;
import com.cloudbees.demo.gasp.model.PlaceDetails;
import com.cloudbees.demo.gasp.model.Places;
import com.cloudbees.demo.gasp.model.Query;

import java.util.ArrayList;

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

public class PlacesActivity extends Activity {
    private static final String TAG = PlacesActivity.class.getName();

    private ArrayAdapter<String> mAdapter;
    private ListView mListView;
    private final ArrayList<String> mList = new ArrayList<String>();
    private final ArrayList<String> mReferenceList = new ArrayList<String>();
    private String mReference;

    // Gasp proxy objects
    private GaspNearbySearch search = new GaspNearbySearch() {
        public void onSuccess(Places places) {
            showLocations(places);
            checkToken(places);
        }

        public void onFailure(String status) {
            Log.e(TAG, "Google Places API search failed: status = " + status);
        }
    };
    private GaspPlaceDetails details = new GaspPlaceDetails() {
        @Override
        public void onSuccess(PlaceDetails placeDetails) {
            showDetails(placeDetails);
        }

        @Override
        public void onFailure(String status) {
            Log.e(TAG, "Google Places API search failed: status = " + status);
        }
    };

    private static double lat = 37.3750274;
    private static double lng = -122.1142916;
    private static String token = "";

    public PlacesActivity() {
    }

    public static void setLocation(double latitude, double longitude) {
        lat = latitude;
        lng = longitude;
    }

    private void showLocations(Places places) {
        for (Place place : places.getResults()) {
            Log.d(TAG, place.getName() + " " + place.getReference());
            mAdapter.add(place.getName());
            mReferenceList.add(place.getReference());
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
                setButtonText(R.string.places_button_locations_next);
                placesButton.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDetails(PlaceDetails placeDetails) {
        try {
            Intent intent = new Intent();
            intent.setClass(PlacesActivity.this, PlacesDetailActivity.class);
            intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_SERIALIZED, placeDetails.getResult());
            intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_REFERENCE, mReference);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLocations() {
        try {
            SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            int radius = Integer.valueOf(gaspSharedPreferences.getString(getString(R.string.places_search_radius_preferences), ""));
            Query query = new Query(lat, lng, radius, token);
            search.nearbySearch(query);
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

    private void addListViewAdapter() {
        try {
            // Use simple TextView layout for ArrayAdapter constructor and map to ListView
            setContentView(R.layout.gasp_places_layout);
            mListView = (ListView) findViewById(R.id.places_list);
            mAdapter = new ArrayAdapter<String>(this, R.layout.gasp_generic_textview, mList);
            mListView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addItemClickListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked: item #" + id + ", "
                        + mList.get((int) id) + " ["
                        + mReferenceList.get((int) id) + "]");
                mReference = mReferenceList.get((int) id);
                Query query = new Query(mReferenceList.get((int) id));
                details.placeDetails(query);
            }
        });
    }

    private void setButtonText(int resId) {
        Button placesButton = (Button) findViewById(R.id.places_button);
        placesButton.setText(resId);
    }

    private void checkLocation() {
        try {
            Location location = LocationFragment.getLocation(this);
            if (location != null) {
                setLocation(location.getLatitude(), location.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            checkLocation();
            addListViewAdapter();
            addButtonListener();
            addItemClickListener();
            getLocations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        token = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_short, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.gasp_settings:
                Intent intent = new Intent();
                intent.setClass(this, SetPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.options_exit:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
