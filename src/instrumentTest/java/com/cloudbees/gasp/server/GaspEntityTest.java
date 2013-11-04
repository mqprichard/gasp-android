package com.cloudbees.gasp.server;

import android.content.Context;
import android.os.AsyncTask;
import android.test.InstrumentationTestCase;

import com.cloudbees.gasp.R;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

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

public abstract class GaspEntityTest extends InstrumentationTestCase {
    protected CountDownLatch signal, signal2;
    protected String mGaspRestaurantsUrl;
    protected String mGaspReviewsUrl;
    protected String mGaspUsersUrl;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
        signal2 = new CountDownLatch(1);

        // Retrieve Gasp server URLs from application context
        Context gaspContext = getInstrumentation().getTargetContext();
        mGaspReviewsUrl = gaspContext.getString(R.string.gasp_reviews_url);
        mGaspRestaurantsUrl = gaspContext.getString(R.string.gasp_restaurants_url);
        mGaspUsersUrl = gaspContext.getString(R.string.gasp_users_url);
    }

    public void addGaspEntity(final String jsonInput, final URL url) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String location = "";
                try {
                    location = GaspServerAPI.newGaspEntity(jsonInput, url);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return location;
            }

            @Override
            protected void onPostExecute(String location) {
                super.onPostExecute(location);
                try {
                    if ((location != null) && (! location.isEmpty())) {
                        onEntityAdded(location);
                    }
                    else {
                        fail();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                signal.countDown();
            }
        }.execute();
    }

    public void deleteGaspEntity(final URL url) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GaspServerAPI.deleteGaspEntity(url);
                }
                catch (Exception e) {
                    fail();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                signal2.countDown();
            }
        }.execute();
    }

    protected abstract void onEntityAdded(String location);
}
