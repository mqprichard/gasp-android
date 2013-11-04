package com.cloudbees.gasp.server;

import android.content.Context;
import android.os.AsyncTask;
import android.test.InstrumentationTestCase;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Restaurant;
import com.google.gson.Gson;

import java.net.URL;
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

public class AddRestaurantTest extends InstrumentationTestCase {
    private CountDownLatch signal, signal2;
    private String gaspRestaurants;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        signal2 = new CountDownLatch(1);

        Context gaspContext = getInstrumentation().getTargetContext();
        gaspRestaurants = gaspContext.getString(R.string.gasp_restaurants_url);
    }

    public void addRestaurant(final Restaurant restaurant, final URL url) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String location = "";
                try {
                    String jsonInput = new Gson().toJson(restaurant, Restaurant.class);
                    location = GaspServerAPI.newGaspEntity(jsonInput, url);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return location;
            }

            @Override
            protected void onPostExecute(String location) {
                super.onPostExecute(location);
                try {
                    if ((location != null) && (! location.isEmpty())) {
                        assertNotNull(location);
                        assertFalse(location.isEmpty());
                        assert(location.startsWith(gaspRestaurants));
                    }
                    else {
                        fail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                signal.countDown();
            }
        }.execute();
    }

    public void testAddRestaurant() {
        try {
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Test Restaurant");
            restaurant.setAddress("Test Address");
            restaurant.setWebsite("www.testrestaurant.com");


            final URL gaspUrl = new URL(gaspRestaurants);
            addRestaurant(restaurant, gaspUrl);

            signal.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void testBadRestaurant() {
        try {
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Test Restaurant");
            restaurant.setAddress("Test Address");
            restaurant.setWebsite("www.testrestaurant.com");

            final URL gaspUrl = new URL("http://badgasp.partnerdemo.cloudbees.net/restaurants");
            addRestaurant(restaurant, gaspUrl);

            signal.await(20, TimeUnit.SECONDS);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
