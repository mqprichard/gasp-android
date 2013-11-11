package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.fragment.GaspDatabaseFragment;
import com.cloudbees.gasp.fragment.GaspRestaurantFragment;
import com.cloudbees.gasp.fragment.GaspReviewFragment;
import com.cloudbees.gasp.model.PlaceDetail;
import com.cloudbees.gasp.model.PlaceEvent;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.Review;

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

public class PlacesDetailActivity extends Activity {
    private final static String TAG = PlacesDetailActivity.class.getName();

    private TextView mName;
    private TextView mWebsite;
    private TextView mAddress;
    private TextView mPhone;
    private TextView mId;
    private TextView mLatitude;
    private TextView mLongitude;
    private ListView mEventsView;

    private String mPlacesId;
    private PlaceDetail mPlaceDetail;
    private List<Review> mReviews;
    private boolean mGaspRestaurant = false;
    private GaspDatabaseFragment mGaspDatabaseFragment;
    private GaspRestaurantFragment mGaspRestaurantFragment;
    private GaspReviewFragment mGaspReviewFragment;
    private static String SERVER_URL;
    private Restaurant mRestaurant;

    private ArrayAdapter<String> mEventAdapter;
    private final ArrayList<String> mEventList = new ArrayList<String>();

    private void addItemClickListener() {
        mEventsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Selected: Event #" + id);
            }
        });
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

    private void addReviewButtonListener() {
        Button reviewButton = (Button) findViewById(R.id.detail_review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences gaspSharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    URL gaspUrl = new URL(gaspSharedPreferences.getString("gasp_reviews_uri", ""));

                    Review review = new Review();
                    review.setStar(5);
                    review.setComment("Very good");
                    review.setRestaurant_id(mRestaurant.getId());
                    review.setUser_id(1);

                    mGaspReviewFragment.addReview(review, gaspUrl);

                    mGaspRestaurant = true;
                    setButtons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addRestaurantButtonListener() {
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences gaspSharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    URL gaspUrl = new URL(gaspSharedPreferences.getString("gasp_restaurants_uri", ""));

                    Restaurant restaurant = new Restaurant();
                    restaurant.setName(mPlaceDetail.getName());
                    restaurant.setPlacesId(mPlaceDetail.getId());
                    restaurant.setWebsite(mPlaceDetail.getWebsite());

                    mGaspRestaurantFragment.addRestaurant(restaurant, gaspUrl);

                    mGaspRestaurant = true;
                    setButtons();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setViews() {
        setContentView(R.layout.gasp_place_detail_layout);
        mName = (TextView) findViewById(R.id.detail_name);
        mAddress = (TextView) findViewById(R.id.detail_address);
        mPhone = (TextView) findViewById(R.id.detail_phone);
        mWebsite = (TextView) findViewById(R.id.detail_website);
        mId = (TextView) findViewById(R.id.detail_id);
        mLatitude = (TextView) findViewById(R.id.detail_latitude);
        mLongitude = (TextView) findViewById(R.id.detail_longitude);
        mEventsView = (ListView) findViewById(R.id.detail_events);
    }

    private void addFragments() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mGaspDatabaseFragment = new GaspDatabaseFragment();
        mGaspRestaurantFragment = new GaspRestaurantFragment() {
            @Override
            public void onSuccess(String location) {
                Log.d(TAG, "Gasp! restaurant added");
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Error adding Gasp! review");
            }
        };
        mGaspReviewFragment = new GaspReviewFragment() {
            @Override
            public void onSuccess(String location) {
                Log.d(TAG, "Gasp! review added");
            }

            @Override
            public void onFailure() {
                Log.e(TAG, "Eror adding Gasp! review");
            }
        };
        ft.add(mGaspDatabaseFragment, "Gasp Database Fragment");
        ft.add(mGaspRestaurantFragment, "Gasp Restaurant Fragment");
        ft.add(mGaspReviewFragment, "Gasp Review Fragment");
        ft.commit();
    }

    private void showLocationDetails(PlaceDetail place) {
        mName.setText(place.getName());
        mAddress.setText(place.getFormatted_address());
        mPhone.setText(place.getFormatted_phone_number());
        mWebsite.setText(place.getWebsite());
        mId.setText(getString(R.string.places_google_id) + place.getId());
        mLatitude.setText(getString(R.string.places_latitude)
                + place.getGeometry().getLocation().getLat().toString());
        mLongitude.setText(getString(R.string.places_longitude)
                + place.getGeometry().getLocation().getLng().toString());
    }

    private void showEventDetails (PlaceDetail place) {
        mEventAdapter = new ArrayAdapter<String>(this, R.layout.gasp_list_layout, mEventList);
        mEventsView.setAdapter(mEventAdapter);

        if (place.getEvents() != null) {
            for (PlaceEvent event : place.getEvents()) {
                Log.d(TAG, "Event Id: " + event.getEvent_id());
                Log.d(TAG, "Event Summary: " + event.getSummary());
                mEventAdapter.add(event.getEvent_id() + ": " + event.getSummary());
            }
        }
    }

    private void getGaspData() {
        Restaurant restaurant = mGaspDatabaseFragment.getRestaurantByPlacesId(mPlacesId);
        if (restaurant != null) {
            Log.d(TAG, "Gasp Restaurant Id: " + restaurant.getId());
            mGaspRestaurant = true;
            mRestaurant = restaurant;
            mReviews = mGaspDatabaseFragment.getReviewsByRestaurant(restaurant.getId());
            for (Review review : mReviews) {
                Log.d(TAG, review.toString());
            }
        } else {
            Log.d(TAG, "Restaurant not found in Gasp database");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mPlaceDetail = (PlaceDetail) getIntent().getSerializableExtra("PlaceDetail");
            mPlacesId = mPlaceDetail.getId();

            Log.d(TAG, "Name: " + mPlaceDetail.getName());
            Log.d(TAG, "Website " + mPlaceDetail.getWebsite());
            Log.d(TAG, "Address: " + mPlaceDetail.getFormatted_address());
            Log.d(TAG, "Id: " + mPlaceDetail.getId());
            Log.d(TAG, "Lat: " + mPlaceDetail.getGeometry().getLocation().getLat());
            Log.d(TAG, "Lng: " + mPlaceDetail.getGeometry().getLocation().getLng());

            setViews();
            addFragments();
            showLocationDetails(mPlaceDetail);
            showEventDetails(mPlaceDetail);
            addItemClickListener();
            addReviewButtonListener();
            addRestaurantButtonListener();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getGaspData();
        setButtons();
    }
}
