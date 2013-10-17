package com.cloudbees.gasp.location;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.cloudbees.gasp.activity.PlacesActivity;
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

public class PlacesSearchActivityTest extends ActivityUnitTestCase<PlacesActivity> {
    private static final String TAG = PlacesSearchActivityTest.class.getName();

    private CountDownLatch signal;
    private PlacesActivity testActivity;
    private PlacesSearchFragment testFragment;

    private static final double lat = 37.3750274;
    private static final double lng = -122.1142916;
    private static final int radius = 500;

    public PlacesSearchActivityTest(Class<PlacesActivity> activityClass) {
        super(activityClass);

        try {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), PlacesActivity.class);
            startActivity(intent, null, null);
            testActivity = getActivity();
            testFragment = new PlacesSearchFragment();
            FragmentManager fm = testActivity.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(testFragment, "PlacesSearchFragment");
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testSearchFragment () {
        try {
        Query query = new Query (lat, lng, radius, "");
        testFragment.placesSearch(query);

        // Allow 20 secs for the async REST call to complete
        signal.await(20, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        signal.countDown();
    }
}
