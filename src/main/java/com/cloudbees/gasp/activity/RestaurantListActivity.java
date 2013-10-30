/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;

import java.util.Collections;
import java.util.List;

public class RestaurantListActivity extends ListActivity {
    private RestaurantAdapter restaurantAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasp_database_list);

        restaurantAdapter = new RestaurantAdapter(this);
        restaurantAdapter.open();

        List<Restaurant> restaurants = restaurantAdapter.getAll();
        Collections.reverse(restaurants);

        ArrayAdapter<Restaurant> adapter =
                new ArrayAdapter<Restaurant>(this, android.R.layout.simple_list_item_1, restaurants);
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        restaurantAdapter.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        restaurantAdapter.close();
        super.onPause();
    }
}