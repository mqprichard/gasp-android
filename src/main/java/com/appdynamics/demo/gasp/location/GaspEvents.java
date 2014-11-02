package com.appdynamics.demo.gasp.location;

import android.os.AsyncTask;
import android.util.Log;

import com.appdynamics.demo.gasp.model.EventRequest;
import com.appdynamics.demo.gasp.model.EventResponse;
import com.google.gson.Gson;

import java.net.URL;

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

public abstract class GaspEvents {
    private static final String TAG = GaspEvents.class.getName();

    /**
     * Add an event using Google Places API
     * Calling activity receives notifications via callbacks
     * @param eventRequest  Gasp model object with event details
     */
    public void addEvent(final EventRequest eventRequest) {
        Log.d(TAG, "Event reference: " + eventRequest.getReference());
        Log.d(TAG, "Event summary: " + eventRequest.getSummary());
        Log.d(TAG, "Event url: "+ eventRequest.getUrl());
        Log.d(TAG, "Event duration: " + eventRequest.getDuration());
        Log.d(TAG, "Event language: " + eventRequest.getLanguage());

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String jsonOutput = new String();
                try {
                    String search = GooglePlacesClient.getQueryStringAddEvent();
                    jsonOutput = GooglePlacesClient.doPost(
                            new Gson().toJson(eventRequest, EventRequest.class), new URL(search));

                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                }
                return jsonOutput;
            }

            @Override
            protected void onPostExecute(String jsonOutput) {
                super.onPostExecute(jsonOutput);
                try {
                    EventResponse eventResponse = new Gson().fromJson(jsonOutput, EventResponse.class);

                    if (eventResponse.getStatus().equalsIgnoreCase("OK"))
                        onEventAdded(eventResponse);
                    else
                        onErrorAddEvent(eventResponse.getStatus());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Callbacks to calling Activity
    abstract public void onEventAdded(EventResponse eventResponse);
    abstract public void onErrorAddEvent(String status);

    /**
     * Delete an event using Google Places API
     * Calling activity receives notifications via callbacks
     * @param eventRequest  Gasp model object with event details
     */
    public void deleteEvent(final EventRequest eventRequest) {
        Log.d(TAG, "Event reference: " + eventRequest.getReference());
        Log.d(TAG, "Event reference: " + eventRequest.getEvent_id());

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String jsonOutput = new String();
                try {
                    String search = GooglePlacesClient.getQueryStringDeleteEvent();
                    jsonOutput = GooglePlacesClient.doPost(
                            new Gson().toJson(eventRequest, EventRequest.class), new URL(search));

                } catch (Exception e) {
                    Log.e(TAG, "Exception: ", e);
                }
                return jsonOutput;
            }

            @Override
            protected void onPostExecute(String jsonOutput) {
                super.onPostExecute(jsonOutput);
                try {
                    EventResponse eventResponse = new Gson().fromJson(jsonOutput, EventResponse.class);

                    if (eventResponse.getStatus().equalsIgnoreCase("OK"))
                        onEventDeleted(eventResponse);
                    else
                        onErrorDeleteEvent(eventResponse.getStatus());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Callbacks to calling Activity
    abstract public void onEventDeleted(EventResponse eventResponse);
    abstract public void onErrorDeleteEvent(String status);
}
