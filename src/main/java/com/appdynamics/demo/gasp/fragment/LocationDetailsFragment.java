package com.appdynamics.demo.gasp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appdynamics.demo.gasp.R;
import com.appdynamics.demo.gasp.model.PlaceDetail;

/**
 * Copyright (c) 2013 Mark Prichard
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

public class LocationDetailsFragment extends Fragment {
    private static final String TAG = LocationDetailsFragment.class.getName();

    private TextView mName;
    private TextView mWebsite;
    private TextView mAddress;
    private TextView mPhone;

    private Button mReviewButton;
    private Button mRestaurantButton;

    public LocationDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gasp_location_details_layout, container);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mName = (TextView) getView().findViewById(R.id.detail_name);
        mWebsite = (TextView) getView().findViewById(R.id.detail_website);
        mAddress = (TextView) getView().findViewById(R.id.detail_address);
        mPhone = (TextView) getView().findViewById(R.id.detail_phone);

        mReviewButton = (Button) getView().findViewById(R.id.detail_review_button);
        mRestaurantButton = (Button) getView().findViewById(R.id.detail_restaurant_button);

        super.onViewCreated(view, savedInstanceState);
    }

    public void showLocationDetails(PlaceDetail place) {
        mName.setText(place.getName());
        mAddress.setText(place.getFormatted_address());
        mPhone.setText(place.getFormatted_phone_number());
        mWebsite.setText(place.getWebsite());
    }
}
