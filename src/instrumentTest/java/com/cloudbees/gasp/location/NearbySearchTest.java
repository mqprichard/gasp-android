package com.cloudbees.gasp.location;

import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import com.cloudbees.gasp.model.Place;
import com.cloudbees.gasp.model.Places;
import com.cloudbees.gasp.model.Query;
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

public class NearbySearchTest extends AndroidTestCase {
    private static final String TAG = NearbySearchTest.class.getName();

    private CountDownLatch signal, signal2;

    private final double lat = 37.3750274;
    private final double lng = -122.1142916;
    private final int radius = 500;

    private Query mQuery;
    private String jsonOutput;

    private static String token = "";


    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        signal2 = new CountDownLatch(1);
    }

    public void placesSearch(Query query) {
        final Query searchQuery = query;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String search = GooglePlacesClient.getQueryStringNearbySearch(searchQuery);
                    Log.d(TAG, search);
                    jsonOutput = GooglePlacesClient.doGet(new URL(search));

                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                }
                return jsonOutput;
            }

            @Override
            protected void onPostExecute(String jsonOutput) {
                super.onPostExecute(jsonOutput);
                try {
                    Places places = new Gson().fromJson(jsonOutput, Places.class);

                    if (places.getStatus().equalsIgnoreCase("OK")) {
                        assertNotNull(places);
                        assertTrue(places.getResults().length > 0);

                        for (Place place : places.getResults()) {
                            Log.d(TAG, place.getName() + " " + place.getReference());
                        }

                        Log.d(TAG, "pagetoken = " + places.getNext_page_token());
                        token = places.getNext_page_token();
                    }
                    else {
                        fail();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Do not call countDown() on the latch: allow signal.await() call to timeout
                // Google Places API will not return follow-on pagetoken queries immediately
            }
        }.execute();
    }

    public void repeatSearch(Query query) {
        final Query searchQuery = query;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String search = GooglePlacesClient.getQueryStringNearbySearch(searchQuery);
                    Log.d(TAG, search);
                    jsonOutput = GooglePlacesClient.doGet(new URL(search));

                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                }
                return jsonOutput;
            }

            @Override
            protected void onPostExecute(String jsonOutput) {
                super.onPostExecute(jsonOutput);
                try {
                    Places places = new Gson().fromJson(jsonOutput, Places.class);

                    if (places.getStatus().equalsIgnoreCase("OK")) {
                        assertNotNull(places);

                        for (Place place : places.getResults()) {
                            Log.d(TAG, place.getName() + " " + place.getReference());
                        }

                        assertTrue(places.getResults().length > 0);
                        assertTrue(places.getNext_page_token().isEmpty());
                    }
                    else {
                        fail();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // This time call countdown so that the test completes immediately
                signal2.countDown();
            }
        }.execute();
    }

    //@UiThreadTest
    public void testLocationSearch() {
        try {
            placesSearch(new Query(lat, lng, radius, token));

            // Allow 30 secs for Google Places API call to complete
            // We will wait for the full timeout so that following call with pagetoken works
            signal.await(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }

    //@UiThreadTest
    public void testRepeatSearch() {
        try {
            assertFalse(token.isEmpty());
            repeatSearch(new Query(lat, lng, radius, token));

            // Allow 20 secs for Google Places API call to complete
            // Test will complete on signal2.countDown() or timeout
            signal2.await(20, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }
}
