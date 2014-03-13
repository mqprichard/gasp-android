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
import android.widget.TextView;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.adapter.GaspDatabase;
import com.cloudbees.demo.gasp.fragment.EventListFragment;
import com.cloudbees.demo.gasp.fragment.ReviewListFragment;
import com.cloudbees.demo.gasp.model.PlaceDetail;
import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.model.Review;
import com.cloudbees.demo.gasp.server.GaspRestaurants;

import java.net.URL;
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

public class PlacesDetailActivity2 extends FragmentActivity {
    private final static String TAG = PlacesDetailActivity2.class.getName();

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

            Button reviewButton = (Button) findViewById(R.id.detail_review_button);
            reviewButton.setEnabled(true);
            Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
            restaurantButton.setEnabled(false);
        }

        @Override
        public void onFailure() {
            Log.e(TAG, "Error adding Gasp! review");
        }
    };

    public PlacesDetailActivity2() {
    }

    private void initialize() {
        Button reviewButton = (Button) findViewById(R.id.detail_review_button);
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
        Restaurant restaurant = mGaspDatabase.getRestaurantByPlacesId(mPlaceDetail.getId());

        if (restaurant != null) {
            Log.d(TAG, "Gasp Restaurant Id: " + restaurant.getId());
            mGaspRestaurantId = restaurant.getId();
            restaurantButton.setEnabled(false);
            reviewButton.setEnabled(true);
        } else {
            Log.d(TAG, "Restaurant not found in Gasp database");
            restaurantButton.setEnabled(true);
            reviewButton.setEnabled(false);
        }
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

    private void showReviews(PlaceDetail place) {
        ReviewListFragment reviewListFragment =
                (ReviewListFragment) getSupportFragmentManager().findFragmentById(R.id.detail_review_list);
        Restaurant restaurant = mGaspDatabase.getRestaurantByPlacesId(place.getId());
        if (restaurant != null) {
            List<Review> reviews = mGaspDatabase.getLastNReviewsByRestaurant(restaurant.getId(), 10);
            reviewListFragment.showReviewDetails(reviews);
        }
    }

    private void showEvents(PlaceDetail place) {
        EventListFragment eventListFragment =
                (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.detail_event_list);
        eventListFragment.showEventDetails(place);
    }

    private void showLocationDetails(PlaceDetail place) {
        TextView mName = (TextView) findViewById(R.id.detail_name);
        TextView mWebsite = (TextView) findViewById(R.id.detail_address);
        TextView mAddress = (TextView) findViewById(R.id.detail_phone);
        TextView mPhone = (TextView) findViewById(R.id.detail_website);

        mName.setText(place.getName());
        mAddress.setText(place.getFormatted_address());
        mPhone.setText(place.getFormatted_phone_number());
        mWebsite.setText(place.getWebsite());
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

            setContentView(R.layout.gasp_place_detail_layout2);

            // Display views and populate fragments
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

        initialize();
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
