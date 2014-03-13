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
import android.widget.ListView;
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

public class PlacesDetailActivity2 extends FragmentActivity {
    private final static String TAG = PlacesDetailActivity2.class.getName();

    public static final String PLACES_DETAIL_SERIALIZED = "PlacesDetail";
    public static final String PLACES_DETAIL_REFERENCE = "Reference";

    // Layout views
    private TextView mName;
    private TextView mWebsite;
    private TextView mAddress;
    private TextView mPhone;

    // Google Places API events for this location
    private ListView mEventsView;
    private final ArrayList<String> mEventList = new ArrayList<String>();

    // Gasp reviews for this location
    private ListView mReviewsView;
    private final ArrayList<String> mReviewList = new ArrayList<String>();

    private int mGaspRestaurantId;      // The Gasp restaurant id
    private String mPlacesReference;    // The Google Places reference

    // Google Places API details
    private PlaceDetail mPlaceDetail;

    // Is restaurant in Gasp server database?
    private boolean mGaspRestaurant = false;

    // Gasp proxy objects
    private GaspDatabase mGaspDatabase = new GaspDatabase(this);
    private GaspRestaurants mGaspRestaurants = new GaspRestaurants() {
        @Override
        public void onSuccess(String location) {
            Log.d(TAG, "Gasp! restaurant added: " + location);
            mGaspRestaurantId = Integer.valueOf(location.substring(location.lastIndexOf("/") + 1));
            mGaspRestaurant = true;
            setButtons();
        }

        @Override
        public void onFailure() {
            Log.e(TAG, "Error adding Gasp! review");
        }
    };

    public PlacesDetailActivity2() {
    }

    private void setButtons() {
        Button reviewButton = (Button) findViewById(R.id.detail_review_button);
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);

        if (mGaspRestaurant) {
            restaurantButton.setEnabled(false);
            reviewButton.setEnabled(true);
        } else {
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

    private void getGaspData() {
        Restaurant restaurant = mGaspDatabase.getRestaurantByPlacesId(mPlaceDetail.getId());
        if (restaurant != null) {
            Log.d(TAG, "Gasp Restaurant Id: " + restaurant.getId());
            mGaspRestaurant = true;
            mGaspRestaurantId = restaurant.getId();
        } else {
            Log.d(TAG, "Restaurant not found in Gasp database");
        }
    }

    private void setViews() {
        setContentView(R.layout.gasp_place_detail_layout2);
        mName = (TextView) findViewById(R.id.detail_name);
        mAddress = (TextView) findViewById(R.id.detail_address);
        mPhone = (TextView) findViewById(R.id.detail_phone);
        mWebsite = (TextView) findViewById(R.id.detail_website);
    }

    private void showReviews() {
        ReviewListFragment reviewListFragment =
                (ReviewListFragment) getSupportFragmentManager().findFragmentById(R.id.detail_review_list);
        Restaurant restaurant = mGaspDatabase.getRestaurantByPlacesId(mPlaceDetail.getId());
        if (restaurant != null) {
            List<Review> reviews = mGaspDatabase.getLastNReviewsByRestaurant(restaurant.getId(), 10);
            reviewListFragment.showReviewDetails(reviews);
        }
    }

    private void showEvents() {
        EventListFragment eventListFragment =
                (EventListFragment) getSupportFragmentManager().findFragmentById(R.id.detail_event_list);
        eventListFragment.showEventDetails(mPlaceDetail);
    }

    private void showLocationDetails(PlaceDetail place) {
        mName.setText(place.getName());
        mAddress.setText(place.getFormatted_address());
        mPhone.setText(place.getFormatted_phone_number());
        mWebsite.setText(place.getWebsite());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);

            mPlaceDetail = (PlaceDetail) getIntent().getSerializableExtra(PLACES_DETAIL_SERIALIZED);
            mPlacesReference = getIntent().getStringExtra(PLACES_DETAIL_REFERENCE);

            setViews();

            showLocationDetails(mPlaceDetail);
            showReviews();
            showEvents();

            addReviewButtonListener();
            addRestaurantButtonListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getGaspData();
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
