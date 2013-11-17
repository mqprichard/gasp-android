package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

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

public class RestaurantSyncServiceTest extends ServiceTestCase<RestaurantSyncService> {
    private static final String TAG = RestaurantSyncServiceTest.class.getName();

    private RestaurantDataAdapter restaurantAdapter;
    private CountDownLatch signal;

    public RestaurantSyncServiceTest() {
        super(RestaurantSyncService.class);
    }

    private void cleanDatabase() {
        RestaurantDataAdapter restaurantAdapter = new RestaurantDataAdapter(getContext());
        restaurantAdapter.open();
        try {
            List<Restaurant> restaurantList = restaurantAdapter.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantAdapter.delete(restaurant);
            }
        } catch (Exception e) {
        } finally {
            restaurantAdapter.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
        signal = new CountDownLatch(1);
    }

    public void testRestaurantSyncIntent() throws InterruptedException {
        startService(new Intent(getContext(), RestaurantSyncService.class));

        // Allow 20 secs for the async REST call to complete
        signal.await(20, TimeUnit.SECONDS);

        try {
            restaurantAdapter = new RestaurantDataAdapter(getContext());
            restaurantAdapter.open();

            List<Restaurant> restaurantList = restaurantAdapter.getAll();
            assertTrue(restaurantList.size() > 0);
        } finally {
            restaurantAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
