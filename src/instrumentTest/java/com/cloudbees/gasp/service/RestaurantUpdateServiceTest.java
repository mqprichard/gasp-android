package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.activity.ConsoleActivity;
import com.cloudbees.gasp.adapter.RestaurantDataAdapter;
import com.cloudbees.gasp.model.Restaurant;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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

public class RestaurantUpdateServiceTest extends ServiceTestCase<RestaurantUpdateService> {
    private static final String TAG = RestaurantUpdateServiceTest.class.getName();

    private RestaurantDataAdapter restaurantAdapter;
    private final CountDownLatch signal;

    public RestaurantUpdateServiceTest() {
        super(RestaurantUpdateService.class);
        signal = new CountDownLatch(1);
    }

    private void cleanDatabase() {
        RestaurantDataAdapter restaurantData = new RestaurantDataAdapter(getContext());
        restaurantData.open();
        try {
            List<Restaurant> restaurantList = restaurantData.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantData.delete(restaurant);
            }
        } catch (Exception e) {
        } finally {
            restaurantData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
    }

    public void testRestaurantUpdateIntent() throws InterruptedException {
        startService(new Intent(getContext(), RestaurantUpdateService.class)
                .putExtra(ConsoleActivity.ResponseReceiver.PARAM_ID, 1));

        // Allow 20 secs for the async REST call to complete
        signal.await(20, TimeUnit.SECONDS);

        try {
            restaurantAdapter = new RestaurantDataAdapter(getContext());
            restaurantAdapter.open();

            List<Restaurant> restaurants = restaurantAdapter.getAll();
            assertTrue(restaurants.size() > 0);
        } finally {
            restaurantAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
