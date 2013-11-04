package com.cloudbees.gasp.server;

import android.webkit.URLUtil;

import com.cloudbees.gasp.model.Restaurant;
import com.google.gson.Gson;

import java.net.URL;
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

public class GaspRestaurantTest extends GaspEntityTest {
    //Store Gasp entity URL across add/delete test cases
    private static String mLocation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void onEntityAdded(String location) {
        assert(URLUtil.isValidUrl(location));
        assert(location.startsWith(mGaspRestaurantsUrl));
        mLocation = location;
    }

    public void testAddRestaurant() {
        try {
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Test Restaurant");
            restaurant.setAddress("Test Address");
            restaurant.setWebsite("www.testrestaurant.com");

            final String jsonInput = new Gson().toJson(restaurant, Restaurant.class);
            final URL gaspUrl = new URL(mGaspRestaurantsUrl);

            addGaspEntity(jsonInput, gaspUrl);
            signal.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }

    public void testDeleteRestaurant() {
        try {
            deleteGaspEntity(new URL(mLocation));
            signal2.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }
}
