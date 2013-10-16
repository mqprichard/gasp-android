package com.cloudbees.gasp.location;

import android.util.Log;

import com.cloudbees.gasp.model.EventResponse;
import com.cloudbees.gasp.model.PlaceDetails;
import com.cloudbees.gasp.model.Places;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

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

public class GooglePlaces {
    private static final String TAG = GooglePlaces.class.getName();

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_SEARCH = "/search";
    private static final String TYPE_DETAILS = "/details";
    private static final String TYPE_NEARBY = "/nearbysearch";
    private static final String TYPE_EVENT_ADD = "/event/add";
    private static final String TYPE_EVENT_DELETE = "/event/delete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "";

    private static Places search(String keyword, double lat, double lng, int radius, String pageToken) {
        Places places = null;
        try {
            String search = PLACES_API_BASE + TYPE_SEARCH + OUT_JSON
                    + "?sensor=false"
                    + "&key=" + API_KEY
                    + "&keyword=" + URLEncoder.encode(keyword, "utf8")
                    + "&location=" + String.valueOf(lat) + "," + String.valueOf(lng)
                    + "&radius=" + String.valueOf(radius);
            if (!pageToken.isEmpty()) {
                search += "&pagetoken=" + URLEncoder.encode(pageToken, "utf8");
            }

            URL url = new URL(search);

            String jsonOutput = doGet(url);
            places = new Gson().fromJson(jsonOutput, Places.class);

            Log.d(TAG, "Status: " + places.getStatus());
            Log.d(TAG, "next_page_token: " + places.getNext_page_token());

        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }

        return places;
    }

    private static PlaceDetails detail(String reference) {
        PlaceDetails placeDetails = null;
        try {
            String search = PLACES_API_BASE + TYPE_DETAILS + OUT_JSON
                    + "?sensor=false"
                    + "&key=" + API_KEY
                    + "&reference=" + URLEncoder.encode(reference, "utf8");

            URL url = new URL(search);
            String jsonOutput = doGet(url);
            placeDetails = new Gson().fromJson(jsonOutput, PlaceDetails.class);

            Log.d(TAG, "Status: " + placeDetails.getStatus());

        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }

        return placeDetails;
    }

    private static Places nearby(double lat, double lng, int radius, String name) {
        Places places= null;
        try {
            String search = PLACES_API_BASE + TYPE_NEARBY + OUT_JSON
                    + "?sensor=false"
                    + "&key=" + API_KEY
                    + "&location=" + lat + "," + lng
                    + "&radius=" + radius
                    + "&types=Restaurant|food|cafe"
                    + "&name=" + URLEncoder.encode(name, "utf8");

            URL url = new URL(search);
            String jsonOutput = doGet(url);
            places = new Gson().fromJson(jsonOutput, Places.class);

            Log.d(TAG, "Status: " + places.getStatus());

        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }

        return places;
    }

    private static EventResponse doAddEvent(String jsonInput) {
        EventResponse eventResponse = null;
        try {
            String search = PLACES_API_BASE + TYPE_EVENT_ADD + OUT_JSON
                    + "?sensor=false"
                    + "&key=" + API_KEY;

            URL url = new URL(search);
            String jsonOutput = doPost(jsonInput, url);
            eventResponse = new Gson().fromJson(jsonOutput, EventResponse.class);

            Log.d(TAG, "Status: " + eventResponse.getStatus());

        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }

        return eventResponse;
    }

    private static EventResponse doDeleteEvent(String jsonInput) {
        EventResponse eventResponse = null;
        try {
            String search = PLACES_API_BASE + TYPE_EVENT_DELETE + OUT_JSON
                    + "?sensor=false"
                    + "&key=" + API_KEY;

            URL url = new URL(search);
            String jsonOutput = doPost(jsonInput, url);
            eventResponse = new Gson().fromJson(jsonOutput, EventResponse.class);

            Log.d(TAG, "Status: " + eventResponse.getStatus());

        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        }

        return eventResponse;
    }

    private static String doGet(URL url) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            Log.d(TAG, "Request URL: " + url.toString());

            conn = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.d(TAG, "Response: " + jsonResults.toString());
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();
    }

    private static String doPost(String input, URL url) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();

        try {
            Log.d(TAG, "Request URL: " + url.toString());
            Log.d(TAG, "Request Body: " + input);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", "" +
                    Integer.toString(input.getBytes().length));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
            wr.writeBytes (input);
            wr.flush ();
            wr.close ();

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }

            Log.d(TAG, "Response: " + jsonResults.toString());
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResults.toString();
    }
}