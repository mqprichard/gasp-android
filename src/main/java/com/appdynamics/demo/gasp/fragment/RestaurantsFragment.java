package com.appdynamics.demo.gasp.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.appdynamics.demo.gasp.adapter.RestaurantDataAdapter;
import com.appdynamics.demo.gasp.model.Restaurant;

import java.util.List;

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

public class RestaurantsFragment extends ListFragment {
    private static final String TAG = RestaurantsFragment.class.getName();

    private RestaurantDataAdapter mRestaurantAdapter;
    private List<Restaurant> mRestaurants;

    public RestaurantsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRestaurantAdapter = new RestaurantDataAdapter(inflater.getContext());
        mRestaurantAdapter.open();

        // Get all restaurants in descending order
        mRestaurants = mRestaurantAdapter.getAllDesc();
        mRestaurantAdapter.close();

        ArrayAdapter<Restaurant> adapter =
                new ArrayAdapter<Restaurant>(inflater.getContext(),
                                             android.R.layout.simple_list_item_1,
                                             mRestaurants);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(TAG, "Restaurant Id: " + mRestaurants.get(position).getId()
                + " " + mRestaurants.get(position).getName());
        FragmentManager fm = getFragmentManager();
        RestaurantDialogFragment frag =
                new RestaurantDialogFragment().newInstance("Gasp! Restaurant", mRestaurants.get(position));
        frag.show(fm, "Restaurant Dialog Fragment");
    }
}
