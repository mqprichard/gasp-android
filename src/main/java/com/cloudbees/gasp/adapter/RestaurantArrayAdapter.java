package com.cloudbees.gasp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Restaurant;

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
 * ArrayAdapter subclass for use with ListActivity (RestaurantListActivity)
 * See gasp_restaurant_list.xml for layout views
 */
public class RestaurantArrayAdapter extends ArrayAdapter<Restaurant> {
    private final static String TAG = RestaurantArrayAdapter.class.getName();

    private List<Restaurant> mRestaurants;
    private int mResource;

    /**
     * Default constructor
     *
     * @param context     The Activity context
     * @param resource    The layout resource
     * @param restaurants The List collection
     */
    public RestaurantArrayAdapter(Context context, int resource, List<Restaurant> restaurants) {
        super(context, resource, restaurants);
        this.mRestaurants = restaurants;
        this.mResource = resource;
    }

    /**
     * Called by ListActivity
     *
     * @param position    Position of this entry in the array
     * @param convertView Layout view
     * @param parent      Not used
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
            TextView viewUrl = (TextView) view.findViewById(R.id.restaurant_url);
            TextView viewName = (TextView) view.findViewById(R.id.restaurant_name);
            TextView viewWebsite = (TextView) view.findViewById(R.id.restaurant_website);
            TextView viewPlacesId = (TextView) view.findViewById(R.id.restaurant_placesId);

            Restaurant restaurant = mRestaurants.get(position);

            if (restaurant != null) {
                viewUrl.setText("Url: " + restaurant.getUrl());
                viewName.setText("Name: " + restaurant.getName());
                viewWebsite.setText("Website: " + restaurant.getWebsite());
                viewPlacesId.setText("PlacesId: " + restaurant.getPlacesId());
            } else {
                Log.e(TAG, "Error: view is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
