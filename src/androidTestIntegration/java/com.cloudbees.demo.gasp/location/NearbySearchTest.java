package com.appdynamics.demo.gasp.location;

import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import com.appdynamics.demo.gasp.model.Place;
import com.appdynamics.demo.gasp.model.Places;
import com.appdynamics.demo.gasp.model.Query;
import com.google.gson.Gson;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2013 Mark Prichard
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

    // Latches used to signal completion of async Google Places API calls
    private CountDownLatch signal, signal2;

    // Constants for initial Places API NearbySearch
    private final double lat = 37.3750274;
    private final double lng = -122.1142916;
    private final int radius = 500;
    private final String token = "";

    private String jsonOutput;

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
                    Log.d(TAG, "Places API Search: " + search);
                    jsonOutput = GooglePlacesClient.doGet(new URL(search));

                } catch (Exception e) {
                    fail();
                }
                return jsonOutput;
            }

            @Override
            protected void onPostExecute(String jsonOutput) {
                super.onPostExecute(jsonOutput);

                String token = new String();
                try {
                    Places places = new Gson().fromJson(jsonOutput, Places.class);

                    if (places.getStatus().equalsIgnoreCase("OK")) {
                        assertNotNull(places);
                        assertTrue(places.getResults().length > 0);

                        for (Place place : places.getResults()) {
                            Log.d(TAG, "Places API Search result: " + place.getName());
                        }
                        token = places.getNext_page_token();
                    }
                    else {
                        fail();
                    }
                    assertFalse(token.isEmpty());

                    // Wait for 30 seconds before repeating NearbySearch call
                    // Places API will not return a valid result immediately
                    signal.await(30, TimeUnit.SECONDS);
                    repeatSearch(new Query(lat, lng, radius, token));

                } catch (Exception e) {
                    fail();
                }
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
                    Log.d(TAG, "Places API Search: " + search);
                    jsonOutput = GooglePlacesClient.doGet(new URL(search));

                } catch (Exception e) {
                    fail();
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
                            Log.d(TAG, "Places API Search result: " + place.getName());
                        }
                    }
                    else {
                        fail();
                    }

                } catch (Exception e) {
                    fail();
                }

                // This time call countdown so that the test completes immediately
                signal2.countDown();
            }
        }.execute();
    }

    public void testLocationSearch() {
        try {
            placesSearch(new Query(lat, lng, radius, token));
        }
        catch (Exception e) {
            fail();
        }
    }
}
