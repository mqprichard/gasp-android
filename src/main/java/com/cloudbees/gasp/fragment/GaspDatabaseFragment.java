package com.cloudbees.gasp.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;

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

public class GaspDatabaseFragment extends Fragment {
    private static final String TAG = GaspDatabaseFragment.class.getName();

    private RestaurantAdapter mRestaurantAdapter;
    private ReviewAdapter mReviewAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRestaurantAdapter = new RestaurantAdapter(getActivity());
        mReviewAdapter = new ReviewAdapter(getActivity());
    }

    public Restaurant getRestaurantByPlacesId(String placesId) {
        Restaurant restaurant = null;
        try {
            mRestaurantAdapter.open();
            restaurant = mRestaurantAdapter.findRestaurantByPlacesId(placesId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRestaurantAdapter.close();
        }
        return restaurant;
    }

    public List<Review> getReviewsByRestaurant(int id) {
        List<Review> reviews = null;
        try {
            mReviewAdapter.open();
            reviews = mReviewAdapter.getAllByRestaurant(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mReviewAdapter.close();
        }
        return reviews;
    }
}
