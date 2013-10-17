package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.cloudbees.gasp.location.PlacesSearchFragment;
import com.cloudbees.gasp.model.Place;
import com.cloudbees.gasp.model.Places;
import com.cloudbees.gasp.model.Query;

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

public class PlacesActivity extends Activity {
    private static final String TAG = PlacesActivity.class.getName();

    private static final double lat = 37.3750274;
    private static final double lng = -122.1142916;
    private static final int radius = 500;
    private String token = "";

    public void putOnMap(Places places) {
        for (Place place : places.getResults()) {
            Log.d(TAG, place.getName());
        }
        Log.d(TAG, "Next page Token: " + places.getNext_page_token());
        if (! places.getNext_page_token().isEmpty()) {
            token = places.getNext_page_token();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlacesSearchFragment searchFragment = new PlacesSearchFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(searchFragment, "PlacesSearchFragment");
        ft.commit();

        Query query = new Query (lat, lng, radius, "");
        searchFragment.placesSearch(query);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
