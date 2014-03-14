package com.cloudbees.demo.gasp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.adapter.GaspDatabase;
import com.cloudbees.demo.gasp.fragment.EventDetailsFragment;
import com.cloudbees.demo.gasp.fragment.LocationDetailsFragment;
import com.cloudbees.demo.gasp.fragment.ReviewDetailsFragment;
import com.cloudbees.demo.gasp.model.PlaceDetail;
import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.model.Review;
import com.cloudbees.demo.gasp.server.GaspRestaurants;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

public class PlacesDetailActivity extends FragmentActivity {
    private final static String TAG = PlacesDetailActivity.class.getName();

    // Intent Bundle keys
    public static final String PLACES_DETAIL_SERIALIZED = "PlacesDetail";
    public static final String PLACES_DETAIL_REFERENCE = "Reference";

    private int mGaspRestaurantId;      // The Gasp restaurant id
    private String mPlacesReference;    // The Google Places reference
    private PlaceDetail mPlaceDetail;   // Google Places API details

    // Gasp proxy objects
    private GaspDatabase mGaspDatabase = new GaspDatabase(this);
    private GaspRestaurants mGaspRestaurants = new GaspRestaurants() {
        @Override
        public void onSuccess(String location) {
            Log.d(TAG, "Gasp! restaurant added: " + location);
            mGaspRestaurantId = Integer.valueOf(location.substring(location.lastIndexOf("/") + 1));
            setButtons();
        }

        @Override
        public void onFailure() {
            Log.e(TAG, "Error adding Gasp! restaurant");
        }
    };

    public PlacesDetailActivity() {
    }

    private Restaurant getGaspRestaurant(PlaceDetail place) {
        Restaurant restaurant = mGaspDatabase.getRestaurantByPlacesId(mPlaceDetail.getId());
        if (restaurant != null) {
            mGaspRestaurantId = restaurant.getId();
            return restaurant;
        }
        else return null;
    }

    private void addGaspReview() {
        try {
            Intent intent = new Intent();
            intent.setClass(this, ReviewActivity.class);
            intent.putExtra(ReviewActivity.REVIEW_RESTAURANT_NAME, mPlaceDetail.getName());
            intent.putExtra(ReviewActivity.REVIEW_RESTAURANT_ID, mGaspRestaurantId);
            intent.putExtra(ReviewActivity.REVIEW_REFERENCE, mPlacesReference);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addGaspRestaurant() {
        try {
            SharedPreferences gaspSharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            URL gaspUrl =
                    new URL(gaspSharedPreferences.getString(getString(R.string.gasp_server_uri_preferences), "")
                            + getString(R.string.gasp_restaurants_location));

            Restaurant restaurant = new Restaurant();
            restaurant.setName(mPlaceDetail.getName());
            restaurant.setPlacesId(mPlaceDetail.getId());
            restaurant.setWebsite(mPlaceDetail.getWebsite());

            mGaspRestaurants.addRestaurant(restaurant, gaspUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setButtons() {
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
        Button reviewButton = (Button) findViewById(R.id.detail_review_button);

        Restaurant restaurant = getGaspRestaurant(mPlaceDetail);
        if ( restaurant != null) {
            restaurantButton.setEnabled(false);
            reviewButton.setEnabled(true);
        } else {
            restaurantButton.setEnabled(true);
            reviewButton.setEnabled(false);
        }
    }

    private void addReviewButtonListener() {
        Button reviewButton = (Button) findViewById(R.id.detail_review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGaspReview();
                finish();
            }
        });
    }

    private void addRestaurantButtonListener() {
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addGaspRestaurant();
                finish();
            }
        });
    }

    private void showLocationDetails(PlaceDetail place) {
        LocationDetailsFragment locationDetailsFragment =
                (LocationDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detail_location_fragment);

        Restaurant restaurant = getGaspRestaurant(mPlaceDetail);
        if ( restaurant != null) {
            locationDetailsFragment.showLocationDetails(place);
        } else {
            locationDetailsFragment.showLocationDetails(place);
        }
    }

    private void showReviews(PlaceDetail place) {
        ReviewDetailsFragment reviewDetailsFragment =
                (ReviewDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detail_review_fragment);

        Restaurant restaurant = getGaspRestaurant(mPlaceDetail);
        if (restaurant != null) {
            List<Review> reviews = mGaspDatabase.getLastNReviewsByRestaurant(restaurant.getId(), 10);
            reviewDetailsFragment.showReviewDetails(reviews);
        }
        else {
            reviewDetailsFragment.showReviewDetails(new ArrayList<Review>());
        }
    }

    private void showEvents(PlaceDetail place) {
        EventDetailsFragment eventDetailsFragment =
                (EventDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.detail_event_fragment);
        eventDetailsFragment.showEventDetails(place);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // Enable Home as Up navigation
            getActionBar().setDisplayHomeAsUpEnabled(true);

            // Get PlaceDetail from calling Activity
            mPlaceDetail = (PlaceDetail) getIntent().getSerializableExtra(PLACES_DETAIL_SERIALIZED);
            mPlacesReference = getIntent().getStringExtra(PLACES_DETAIL_REFERENCE);

            setContentView(R.layout.gasp_place_detail_layout);

            // Populate Fragments
            showLocationDetails(mPlaceDetail);
            showReviews(mPlaceDetail);
            showEvents(mPlaceDetail);

            // Hook uo button listeners
            addReviewButtonListener();
            addRestaurantButtonListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setButtons();
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

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
