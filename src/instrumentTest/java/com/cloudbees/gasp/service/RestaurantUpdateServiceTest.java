package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RestaurantUpdateServiceTest extends ServiceTestCase<RestaurantUpdateService> {
    private static final String TAG = RestaurantUpdateServiceTest.class.getName();

    private RestaurantAdapter restaurantAdapter;
    private final CountDownLatch signal;

    private RestaurantUpdateServiceTest() {
        super(RestaurantUpdateService.class);
        signal = new CountDownLatch(1);
    }

    private void cleanDatabase() {
        RestaurantAdapter restaurantData = new RestaurantAdapter(getContext());
        restaurantData.open();
        try {
            List<Restaurant> restaurantList = restaurantData.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantData.deleteRestaurant(restaurant);
            }
        }
        catch(Exception e){}
        finally {
            restaurantData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
    }

    public void testRestaurantUpdateIntent () throws InterruptedException {
        startService(new Intent(getContext(), RestaurantUpdateService.class)
                .putExtra(SyncIntentParams.PARAM_ID, 1));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        try {
            restaurantAdapter = new RestaurantAdapter(getContext());
            restaurantAdapter.open();

            List<Restaurant> restaurants = restaurantAdapter.getAll();
            assertTrue(restaurants.size() > 0);
        }
        finally {
            restaurantAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
