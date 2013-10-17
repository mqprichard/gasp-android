package com.cloudbees.gasp.location;

import android.test.AndroidTestCase;

import com.cloudbees.gasp.model.Places;
import com.cloudbees.gasp.model.Query;

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

public class PlacesSearchTest extends AndroidTestCase {

    private CountDownLatch signal;
    private String mToken = "";
    private static final double lat = 37.3750274;
    private static final double lng = -122.1142916;
    private static final int radius = 500;

    protected void setUp() {
        signal = new CountDownLatch(1);
    }

    public void testPlacesSearch() {
        try {
            Query query = new Query(lat, lng, radius, "");

            PlacesSearch search = new PlacesSearch(query);
            search.execute();

            // Allow 20 secs for the async REST call to complete
            signal.await(20, TimeUnit.SECONDS);

            Places places = search.getPlaces();
            assertNotNull(places);
            assertEquals(20, places.getResults().length);

            mToken = places.getNext_page_token();
            assertNotNull(mToken);

            query = new Query(lat, lng, radius, mToken);
            search = new PlacesSearch(query);
            search.execute();

            // Allow 20 secs for the async REST call to complete
            signal.await(20, TimeUnit.SECONDS);

            places = search.getPlaces();
            assertNotNull(places);
            assertTrue(places.getResults().length > 0);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void tearDown() {
        signal.countDown();
    }
}
