package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.location.GooglePlacesClient;
import com.cloudbees.gasp.model.PlaceDetail;
import com.cloudbees.gasp.model.PlaceDetails;
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

public class PlacesDetailActivityTest extends ActivityInstrumentationTestCase2 <PlacesDetailActivity> {
    private static final String TAG = PlacesDetailActivityTest.class.getName();

    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private PlaceDetail mPlaceDetail;

    // Test data: Google Places API reference
    private static final String testReference = "CqQBmQAAAKy-ODU_UpEhyk22zCw4GM3gMckCPag71PABfXXY1HOBySnYbuo7bBPvrro3Nj1gCZzwsSoqu8Z6-YACCR9nwQolaSsWWEw4uOqumKAH8m2CIBpc7HdrushQ4yWwd_Kope_YbuDScbjgwEAmEfzptzrVMwXmc6_Jw2Lgo6Q4yNlYUCM6-YABgsFy3jUhjv2nH_W3XJ0xl3MrtNGjSWh6ULgSEH-xADGUVyTFfbC_KqSCkDAaFBETAuYzYhCGeuGir988AzSnSscc";

    public PlacesDetailActivityTest() {
        super(PlacesDetailActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        // Get test data from Google Places API
        GooglePlacesClient google = new GooglePlacesClient();
        String jsonOutput = google.doGet(new URL(google.getQueryStringPlaceDetails(testReference)));
        PlaceDetails placeDetails = new Gson().fromJson(jsonOutput, PlaceDetails.class);
        mPlaceDetail = placeDetails.getResult();
        assertNotNull(mPlaceDetail);
        assertNotNull(mPlaceDetail.getName());
        assertNotNull(mPlaceDetail.getWebsite());
        assertNotNull(mPlaceDetail.getFormatted_address());
        assertNotNull(mPlaceDetail.getFormatted_phone_number());
        assertNotNull(mPlaceDetail.getGeometry());

        // Send intent data to Activity before calling getActivity()
        Intent intent = new Intent();
        intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_SERIALIZED, mPlaceDetail);
        intent.putExtra(PlacesDetailActivity.PLACES_DETAIL_REFERENCE, testReference);
        setActivityIntent(intent);

        mActivity = getActivity();
    }

    public void testFragments() throws Exception {
        mFragmentManager = mActivity.getFragmentManager();
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_gasp_database)));
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_gasp_restaurant)));
        assertNotNull(mFragmentManager.findFragmentByTag(mActivity.getString(R.string.fragment_gasp_review)));
    }

    public void testViews() throws Exception {
        TextView detailName = (TextView)mActivity.findViewById(R.id.detail_name);
        assertEquals(detailName.getText(), mPlaceDetail.getName());
        TextView detailWebsite = (TextView)mActivity.findViewById(R.id.detail_website);
        assertEquals(detailWebsite.getText(), mPlaceDetail.getWebsite());
        TextView detailAddress = (TextView)mActivity.findViewById(R.id.detail_address);
        assertEquals(detailAddress.getText(), mPlaceDetail.getFormatted_address());
        TextView detailPhone = (TextView)mActivity.findViewById(R.id.detail_phone);
        assertEquals(detailPhone.getText(), mPlaceDetail.getFormatted_phone_number());
        TextView detailLatitude = (TextView)mActivity.findViewById(R.id.detail_latitude);
        assert(detailLatitude.getText().toString().contains(mPlaceDetail.getGeometry().getLocation().getLat().toString()));
        TextView detailLongitude = (TextView)mActivity.findViewById(R.id.detail_longitude);
        assert(detailLatitude.getText().toString().contains(mPlaceDetail.getGeometry().getLocation().getLng().toString()));
    }

    private void testOptionsMenuStartActivity(String className, int id){
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(className, null, false);
        getInstrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
        getInstrumentation().invokeMenuActionSync(mActivity, id, 0);
        Activity a = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
        assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
        a.finish();
    }

    public void testOptionsMenuPreferences() throws Exception {
        testOptionsMenuStartActivity(SetPreferencesActivity.class.getName(), R.id.gasp_settings);
    }

    private void testButtonStartActivity(String className, int id){
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(className, null, false);
        final Button button = (Button) mActivity.findViewById(id);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // click button and open next activity.
                button.performClick();
            }
        });
        Activity a = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
        assertNotNull(a);
        a .finish();
    }

    public void testAddReviewButton() throws Exception {
        testButtonStartActivity(ReviewActivity.class.getName(), R.id.detail_review_button);
    }
}
