package com.cloudbees.gasp.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by markprichard on 7/15/13.
 */
public class RestaurantListActivity extends ListActivity {
    private RestaurantAdapter restaurantAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasp_database_list);

        restaurantAdapter = new RestaurantAdapter(this);
        restaurantAdapter.open();

        List<Restaurant> restaurants = restaurantAdapter.getAllRestaurants();
        Collections.reverse(restaurants);

        // Use the SimpleCursorAdapter to show the
        // elements in a ListView
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