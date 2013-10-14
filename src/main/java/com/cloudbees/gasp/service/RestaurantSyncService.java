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

package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

public class RestaurantSyncService extends IntentService implements IRESTListener {
    private static final String TAG = RestaurantSyncService.class.getName();

    private Uri mGaspRestaurantsUri;

    private void getGaspRestaurantsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_restaurants_uri", "");

        this.mGaspRestaurantsUri = Uri.parse(gaspReviewsUri);
    }

    private Uri getGaspRestaurantsUri() {
        return mGaspRestaurantsUri;
    }

    public RestaurantSyncService() {
        super(RestaurantSyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getGaspRestaurantsUriSharedPreferences();
        Log.i(TAG, "Using Gasp Server Restaurants URI: " + getGaspRestaurantsUri());

        AsyncRESTClient asyncRestCall = new AsyncRESTClient(getGaspRestaurantsUri(), this);
        asyncRestCall.getAll();
    }

    @Override
    public void onCompleted(String results){
        Log.i(TAG, "Response from " + mGaspRestaurantsUri.toString() + " :" + results + '\n');

        if (results!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Restaurant>>() {}.getType();
                List<Restaurant> restaurants = gson.fromJson(results, type);

                RestaurantAdapter restaurantsDB = new RestaurantAdapter(getApplicationContext());
                restaurantsDB.open();
                ListIterator<Restaurant> iterator = restaurants.listIterator();
                int index = 0;
                while (iterator.hasNext()) {
                    try {
                        Restaurant restaurant = iterator.next();
                        restaurantsDB.insertRestaurant(restaurant);
                        index = restaurant.getId();
                    } catch (SQLiteConstraintException e) {
                        // Attempting to overwrite existing records will throw an exception
                        // Ignore these as we want to re-sync on startup
                    }
                }
                restaurantsDB.close();

                String resultTxt = "Loaded " + index + " restaurants from " + mGaspRestaurantsUri;
                Log.i(TAG, resultTxt + '\n');

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(MainActivity.ResponseReceiver.PARAM_OUT_MSG, resultTxt);
                sendBroadcast(broadcastIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
