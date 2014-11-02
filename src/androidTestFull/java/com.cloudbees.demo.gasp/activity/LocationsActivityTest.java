package com.appdynamics.demo.gasp.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.appdynamics.demo.gasp.R;

import java.lang.Exception;

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

public class LocationsActivityTest extends ActivityInstrumentationTestCase2<LocationsActivity> {
    static final String TAG = LocationsActivityTest.class.getName();

    private LocationsActivity mActivity;

    public LocationsActivityTest() {
        super(LocationsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    private void testOptionsMenuStartActivity(String className, int id){
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(className, null, false);

        // Simulate Options Menu -> {id}
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(mActivity, id, 0);

        // Check Activity started correctly
        Activity a = getInstrumentation().waitForMonitorWithTimeout(am, 3000);
        assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
        a.finish();
    }

    /*
    public void testOptionsMenuRestaurants() throws Throwable {
        testOptionsMenuStartActivity(RestaurantListActivity.class.getName(), R.id.gasp_restaurants_data);
    }

    public void testOptionsMenuReviews() throws Exception {
        testOptionsMenuStartActivity(ReviewListActivity.class.getName(), R.id.gasp_reviews_data);
    }

    public void testOptionsMenuUsers() throws Exception {
        testOptionsMenuStartActivity(UserListActivity.class.getName(), R.id.gasp_users_data);
    }
    */

    public void testOptionsMenuTwitter() throws Exception {
        testOptionsMenuStartActivity(TwitterStreamActivity.class.getName(), R.id.gasp_menu_twitter);
    }

    public void testOptionsMenuPreferences() throws Exception {
        testOptionsMenuStartActivity(SetPreferencesActivity.class.getName(), R.id.gasp_settings);
    }

    public void testOptionsMenuGaspData() throws Exception {
        testOptionsMenuStartActivity(GaspDataActivity.class.getName(), R.id.gasp_data);
    }
}
