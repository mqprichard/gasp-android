package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;

import com.cloudbees.gasp.R;

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

public class PlacesActivityTest extends ActivityInstrumentationTestCase2<PlacesActivity> {
    private Activity mActivity;
    private FragmentManager mFragmentManager;

    public PlacesActivityTest() {
        super(PlacesActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    public void testFragments() throws Exception {
        mFragmentManager = mActivity.getFragmentManager();
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_add_event)));
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_delete_event)));
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_location_search)));
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_place_details)));
    }

    private void testOptionsMenuStartActivity(String className, int id){
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(className, null, false);

        // Simulate Options Menu -> {id}
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(mActivity, id, 0);

        // Check Activity started correctly
        Activity a = getInstrumentation().waitForMonitorWithTimeout(am, TestParams.getTIMEOUT());
        assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
        a.finish();
    }

    public void testOptionsMenuPreferences() throws Exception {
        testOptionsMenuStartActivity(SetPreferencesActivity.class.getName(), R.id.gasp_settings);
    }
}
