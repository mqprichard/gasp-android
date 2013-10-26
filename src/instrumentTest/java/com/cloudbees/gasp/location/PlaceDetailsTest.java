package com.cloudbees.gasp.location;

import android.os.AsyncTask;
import android.test.AndroidTestCase;
import android.util.Log;

import com.cloudbees.gasp.model.PlaceDetails;
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

public class PlaceDetailsTest extends AndroidTestCase {
    private static final String TAG = PlaceDetailsTest.class.getName();

    private CountDownLatch signal, signal2;

    private final double lat = 37.3750274;
    private final double lng = -122.1142916;
    private final int radius = 500;
    private final String token = "";

    private String jsonOutput;
    private static String reference = "";

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
                        reference = places.getResults()[0].getReference();
                        assertFalse(places.getResults()[0].getReference().isEmpty());
                    }
                    else {
                        fail();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Call countDown() on the latch so that the test completes immediately
                signal.countDown();
            }
        }.execute();
    }

    public void placeDetails() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String search = GooglePlacesClient.getQueryStringPlaceDetails(reference);
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
                    PlaceDetails details = new Gson().fromJson(jsonOutput, PlaceDetails.class);

                    if (details.getStatus().equalsIgnoreCase("OK")) {
                        assertNotNull(details);
                        assertFalse(details.getResult().getId().isEmpty());
                        assertFalse(details.getResult().getName().isEmpty());
                        assertFalse(details.getResult().getFormatted_address().isEmpty());
                        assertFalse(details.getResult().getFormatted_phone_number().isEmpty());
                        assertFalse(details.getResult().getWebsite().isEmpty());
                    }
                    else {
                        fail();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Call countDown() on the latch so that the test completes immediately
                signal2.countDown();
            }
        }.execute();
    }

    public void testPlaceDetails() {
        try {
            placesSearch(new Query(lat, lng, radius, token));

            // Allow 30 secs for Google Places API call to complete
            signal.await(30, TimeUnit.SECONDS);

            placeDetails();

            // Allow 30 secs for Google Places API call to complete
            signal2.await(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            fail();
        }
    }
}
