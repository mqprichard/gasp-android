package com.cloudbees.gasp.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.server.GaspServerAPI;
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

public abstract class AddRestaurantFragment extends Fragment {
    private static final String TAG = AddRestaurantFragment.class.getName();

    private String mLocation = "";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void addRestaurant(final Restaurant restaurant, final URL url) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    String jsonInput = new Gson().toJson(restaurant, Restaurant.class);
                    mLocation = GaspServerAPI.newGaspEntity(jsonInput, url);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return mLocation;
            }

            @Override
            protected void onPostExecute(String location) {
                super.onPostExecute(location);
                try {
                    if ((location != null) && (! location.isEmpty())) {
                        onSuccess(location);
                    }
                    else {
                        onFailure();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // Callbacks to calling Activity
    abstract public void onSuccess(String location);
    abstract public void onFailure();
}
