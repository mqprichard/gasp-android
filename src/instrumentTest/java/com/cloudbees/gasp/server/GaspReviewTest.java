package com.cloudbees.gasp.server;

import android.webkit.URLUtil;

import com.cloudbees.gasp.model.Review;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

public class GaspReviewTest extends GaspEntityTest {
    private static String mLocation;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void onEntityAdded(String location) {
        assert(URLUtil.isValidUrl(location));
        assert(location.startsWith(mGaspReviewsUrl));
        mLocation = location;
    }

    public void testAddReview() {
        try {
            Review review = new Review();
            review.setStar(5);
            review.setComment("Test Comment");
            review.setRestaurant(mGaspRestaurantsUrl + "/1");
            review.setUser(mGaspUsersUrl + "/1");

            final URL gaspUrl = new URL(mGaspReviewsUrl);
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            String jsonInput = gson.toJson(review, Review.class);

            addGaspEntity(jsonInput, gaspUrl);
            signal.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }

    public void testDeleteReview() {
        try {
            deleteGaspEntity(new URL(mLocation));
            signal2.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }
}
