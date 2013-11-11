package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.fragment.GaspDatabaseFragment;
import com.cloudbees.gasp.model.PlaceDetail;
import com.cloudbees.gasp.model.PlaceEvent;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.Review;

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
    private List<Review> mReviews;
    private boolean mGaspRestaurant = false;
    private GaspDatabaseFragment mGaspDatabaseFragment;

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
                Log.d(TAG, "Add Review");
            }
        });
    }

    private void addRestaurantButtonListener() {
        Button restaurantButton = (Button) findViewById(R.id.detail_restaurant_button);
        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add Restaurant");
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
        ft.add(mGaspDatabaseFragment, "Gasp Database Fragment");
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
            PlaceDetail place = (PlaceDetail) getIntent().getSerializableExtra("PlaceDetail");
            mPlacesId = place.getId();

            Log.d(TAG, "Name: " + place.getName());
            Log.d(TAG, "Website " + place.getWebsite());
            Log.d(TAG, "Address: " + place.getFormatted_address());
            Log.d(TAG, "Id: " + place.getId());
            Log.d(TAG, "Lat: " + place.getGeometry().getLocation().getLat());
            Log.d(TAG, "Lng: " + place.getGeometry().getLocation().getLng());

            setViews();
            addFragments();
            showLocationDetails(place);
            showEventDetails(place);
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
