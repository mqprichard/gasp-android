package com.cloudbees.gasp.location;

import android.util.Log;

import com.cloudbees.gasp.model.Query;

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

public class GooglePlacesClient {
    private static final String TAG = GooglePlacesClient.class.getName();

    public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    public static final String TYPE_SEARCH = "/search";
    public static final String TYPE_DETAILS = "/details";
    public static final String TYPE_NEARBY = "/nearbysearch";
    public static final String TYPE_EVENT_ADD = "/event/add";
    public static final String TYPE_EVENT_DELETE = "/event/delete";
    public static final String OUT_JSON = "/json";
    public static final String API_KEY = "AIzaSyD8RPFcX_YY3-M21yGGaww2_NBPLHsjU5o";

    private static final String keyword = "Restaurant";
    private static final String keywords = "Restaurant|food|cafe";
    private static final String encoding = "utf8";

    public static String getQueryStringLocationSearch(Query query) {
        String search = "";
        try {
            search = GooglePlacesClient.PLACES_API_BASE
                    + GooglePlacesClient.TYPE_SEARCH
                    + GooglePlacesClient.OUT_JSON
                    + "?sensor=false"
                    + "&key=" + GooglePlacesClient.API_KEY
                    + "&keyword=" + URLEncoder.encode(keyword, encoding)
                    + "&location=" + String.valueOf(query.getLat()) + "," + String.valueOf(query.getLng())
                    + "&radius=" + String.valueOf(query.getRadius());
            if (!query.getNext_page_token().isEmpty()) {
                search += "&pagetoken=" + URLEncoder.encode(query.getNext_page_token(), encoding);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    public static String getQueryStringNearbySearch(Query query) {
        String search = "";
        try {
            search = GooglePlacesClient.PLACES_API_BASE
                    + GooglePlacesClient.TYPE_NEARBY
                    + GooglePlacesClient.OUT_JSON
                    + "?sensor=false"
                    + "&key=" + GooglePlacesClient.API_KEY
                    + "&location=" + String.valueOf(query.getLat()) + "," + String.valueOf(query.getLng())
                    + "&radius=" + String.valueOf(query.getRadius())
                    + "&types=" + keywords;
            if (!query.getNext_page_token().isEmpty()) {
                search += "&pagetoken=" + URLEncoder.encode(query.getNext_page_token(), encoding);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    public static String getQueryStringPlaceDetails(Query query) {
        String search = "";
        try {
            search = GooglePlacesClient.PLACES_API_BASE
                    + GooglePlacesClient.TYPE_DETAILS
                    + GooglePlacesClient.OUT_JSON
                    + "?sensor=false"
                    + "&key=" + GooglePlacesClient.API_KEY
                    + "&reference=" + URLEncoder.encode(keyword, encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    public String getQueryStringAddEvent(Query query) {
        String search = "";
        try {
            search = GooglePlacesClient.PLACES_API_BASE
                    + GooglePlacesClient.TYPE_NEARBY
                    + GooglePlacesClient.OUT_JSON
                    + "?sensor=false"
                    + "&key=" + GooglePlacesClient.API_KEY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    public String getQueryStringDeleteEvent(Query query) {
        String search = "";
        try {
            search = GooglePlacesClient.PLACES_API_BASE
                    + GooglePlacesClient.TYPE_NEARBY
                    + GooglePlacesClient.OUT_JSON
                    + "?sensor=false"
                    + "&key=" + GooglePlacesClient.API_KEY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return search;
    }

    public static String doGet(URL url) {
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

    public static String doPost(String input, URL url) {
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
