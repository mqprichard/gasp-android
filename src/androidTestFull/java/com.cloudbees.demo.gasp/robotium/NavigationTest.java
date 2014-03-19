package com.cloudbees.gasp.robotium;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.activity.LocationsActivity;
import com.cloudbees.demo.gasp.activity.RestaurantListActivity;
import com.cloudbees.demo.gasp.activity.ReviewListActivity;
import com.cloudbees.demo.gasp.activity.SetPreferencesActivity;
import com.cloudbees.demo.gasp.activity.TwitterStreamActivity;
import com.cloudbees.demo.gasp.activity.UserListActivity;
import com.jayway.android.robotium.solo.Solo;

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

public class NavigationTest extends ActivityInstrumentationTestCase2<LocationsActivity> {
    private Solo solo;

    public NavigationTest() {
        super(LocationsActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /**
     * Start Activity from Action Bar
     * @param resId     Resource id for the Activity
     * @param to        Activity subclass to start
     */
    private void actionBarActivity(int resId, Class<? extends Activity> to) {
        solo.clickOnActionBarItem(resId);
        solo.waitForActivity(to, 5000);
        solo.assertCurrentActivity("Action Bar: " + to.getClass().getName(), to);
    }

    /**
     * Start Activity from Options Menu
     * @param text  Menu item text
     * @param to    Activity subclass to start
     */
    private void optionsMenuActivity(String text, Class<? extends Activity> to) {
        solo.clickOnMenuItem(text);
        solo.waitForActivity(to, 5000);
        solo.assertCurrentActivity("Action Bar: " + to.getClass().getName(), to);
    }

    /**
     * Simulate Action Bar Home click
     * @param to    Parent Activity
     */
    private void actionBarHome(Class<? extends Activity> to) {
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(to, 5000);
        solo.assertCurrentActivity("Action Bar Home", to);
    }

    /**
     * Simulate Hardware back button press
     * @param to    Parent Activity
     */
    private void backButton(Class<? extends Activity> to) {
        solo.goBack();
        solo.waitForActivity(to, 5000);
        solo.assertCurrentActivity("Back: " + to.getClass().getName(), to);
    }

    public void testTwitterNavigation() {
        actionBarActivity(R.id.gasp_menu_twitter, TwitterStreamActivity.class);
        backButton(LocationsActivity.class);
    }

    public void testSettingsNavigation() {
        optionsMenuActivity(solo.getString(R.string.gasp_settings), SetPreferencesActivity.class);
        backButton(LocationsActivity.class);
    }

    public void testRestaurantsNavigation() {
        optionsMenuActivity(solo.getString(R.string.gasp_restaurants_data), RestaurantListActivity.class);
        backButton(LocationsActivity.class);
    }

    public void testReviewsNavigation() {
        optionsMenuActivity(solo.getString(R.string.gasp_reviews_data), ReviewListActivity.class);
        backButton(LocationsActivity.class);
    }

    public void testUsersNavigation() {
        optionsMenuActivity(solo.getString(R.string.gasp_users_data), UserListActivity.class);
        backButton(LocationsActivity.class);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
