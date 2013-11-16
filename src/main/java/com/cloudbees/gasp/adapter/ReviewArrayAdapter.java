package com.cloudbees.gasp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Review;

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

/**
 * ArrayAdapter subclass for use with ListActivity (ReviewListActivity)
 * See gasp_review_list.xml for layout views
 */
public class ReviewArrayAdapter extends ArrayAdapter<Review> {
    private final static String TAG = ReviewArrayAdapter.class.getName();

    private List<Review> mReviews;
    private int mResource;

    /**
     * Default constructor
     *
     * @param context  The Activity context
     * @param resource The layout resource
     * @param reviews  The List collection
     */
    public ReviewArrayAdapter(Context context, int resource, List<Review> reviews) {
        super(context, resource, reviews);
        this.mReviews = reviews;
        this.mResource = resource;
    }

    /**
     * Called by ListActivity
     * @param position      Position of this entry in the array
     * @param convertView   Layout view
     * @param parent        Not used
     * @return View object for this entry
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        try {
            if (view == null) {
                LayoutInflater inflater
                        = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(mResource, null);
            }
            TextView viewUrl = (TextView) view.findViewById(R.id.review_url);
            TextView viewRestaurant = (TextView) view.findViewById(R.id.review_restaurant);
            TextView viewUser = (TextView) view.findViewById(R.id.review_user);
            TextView viewStar = (TextView) view.findViewById(R.id.review_star);
            TextView viewComment = (TextView) view.findViewById(R.id.review_comment);

            Review review = mReviews.get(position);

            if (review != null) {
                viewUrl.setText("Url: " + review.getUrl());
                viewRestaurant.setText("Restaurant: " + review.getRestaurant());
                viewUser.setText("User: " + review.getUser());
                viewStar.setText("Star: " + review.getStar());
                viewComment.setText("Comment: " + review.getComment());
            } else {
                Log.e(TAG, "Error: view is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
