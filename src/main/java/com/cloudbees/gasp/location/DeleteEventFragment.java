package com.cloudbees.gasp.location;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.cloudbees.gasp.activity.PlacesActivity;
import com.cloudbees.gasp.model.EventResponse;
import com.cloudbees.gasp.model.Query;
import com.google.gson.Gson;

import java.net.URL;

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

public class DeleteEventFragment extends Fragment {
    private static final String TAG = DeleteEventFragment.class.getName();

    private final String keywords = "Restaurant|food|cafe";
    private final String encoding = "utf8";

    private Query mQuery;
    private String jsonOutput;

    //PlacesActivity activity = (PlacesActivity) getActivity();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void addEvent(String jsonInput) {
        final String jsonEvent = jsonInput;

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String search = GooglePlacesClient.PLACES_API_BASE
                            + GooglePlacesClient.TYPE_NEARBY
                            + GooglePlacesClient.OUT_JSON
                            + "?sensor=false"
                            + "&key=" + GooglePlacesClient.API_KEY;

                    jsonOutput = GooglePlacesClient.doPost(jsonEvent, new URL(search));

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
                    Log.d(TAG, "Status: " + eventResponse.getStatus());
                    
                    PlacesActivity activity = (PlacesActivity) getActivity();
                    // TODO callback to PlacesActivity

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
