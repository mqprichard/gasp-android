package com.cloudbees.gasp.robotium;

import android.test.ActivityInstrumentationTestCase2;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.activity.ConsoleActivity;
import com.cloudbees.gasp.activity.PlacesActivity;
import com.cloudbees.gasp.activity.PlacesDetailActivity;
import com.cloudbees.gasp.activity.RestaurantListActivity;
import com.cloudbees.gasp.activity.ReviewListActivity;
import com.cloudbees.gasp.activity.SetPreferencesActivity;
import com.cloudbees.gasp.activity.TwitterStreamActivity;
import com.cloudbees.gasp.activity.UserListActivity;
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

public class NavigationTest extends ActivityInstrumentationTestCase2<ConsoleActivity> {
    private Solo solo;
    // TODO: Robotium bug? Fix to use resource ids
    private String menuRestaurants = "Gasp! Restaurants";
    private String menuReviews = "Gasp! Reviews";
    private String menuUsers = "Gasp! Users";
    private String menuSettings = "Gasp! Settings";

    public NavigationTest() {
        super(ConsoleActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    // TODO: Refactor into private methods
    public void testPlacesNavigation() {
        // Action Bar -> PlacesActivity
        solo.clickOnActionBarItem(R.id.gasp_menu_places);
        solo.waitForActivity(PlacesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar PlacesActivity", PlacesActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testPlacesDetailNavigation() {
        // Action Bar -> PlacesActivity
        solo.clickOnActionBarItem(R.id.gasp_menu_places);
        solo.waitForActivity(PlacesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar PlacesActivity", PlacesActivity.class);

        // Select first item from list
        solo.sleep(5000);
        assertTrue(solo.waitForView(R.id.places_list));
        solo.clickInList(1);
        solo.waitForActivity(PlacesDetailActivity.class,5000);
        solo.assertCurrentActivity("Clicked", PlacesDetailActivity.class);

        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(PlacesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", PlacesActivity.class);

        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testTwitterNavigation() {
        // Action Bar -> TwitterActivity
        solo.clickOnActionBarItem(R.id.gasp_menu_twitter);
        solo.waitForActivity(TwitterStreamActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar TwitterStreamActivity", TwitterStreamActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testSettingsNavigation() {
        // Options Menu -> Settings
        solo.clickOnMenuItem(menuSettings);
        solo.waitForActivity(SetPreferencesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar SetPreferencesActivity", SetPreferencesActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testPlacesSettingsNavigation() {
        // Action Bar -> PlacesActivity
        solo.clickOnActionBarItem(R.id.gasp_menu_places);
        solo.waitForActivity(PlacesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar PlacesActivity", PlacesActivity.class);
        // Options Menu -> Settings
        solo.clickOnMenuItem(menuSettings);
        solo.waitForActivity(SetPreferencesActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar SetPreferencesActivity", SetPreferencesActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testRestaurantsNavigation() {
        // OptionsMenu -> RestaurantListActivity
        solo.clickOnMenuItem(menuRestaurants);
        solo.waitForActivity(RestaurantListActivity.class, 5000);
        solo.assertCurrentActivity("Options Menu RestaurantListActivity", RestaurantListActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testReviewsNavigation() {
        // OptionsMenu -> ReviewListActivity
        solo.clickOnMenuItem(menuReviews);
        solo.waitForActivity(ReviewListActivity.class, 5000);
        solo.assertCurrentActivity("Options Menu ReviewListActivity", ReviewListActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    public void testUsersNavigation() {
        // OptionsMenu -> UserListActivity
        solo.clickOnMenuItem(menuUsers);
        solo.waitForActivity(UserListActivity.class, 5000);
        solo.assertCurrentActivity("Options Menu UserListActivity", UserListActivity.class);
        // Action Bar -> Home
        solo.clickOnActionBarHomeButton();
        solo.waitForActivity(ConsoleActivity.class, 5000);
        solo.assertCurrentActivity("Action Bar Home", ConsoleActivity.class);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
