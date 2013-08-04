/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.gcm.ServerUtilities;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.service.RestaurantSyncService;
import com.cloudbees.gasp.service.ReviewSyncService;
import com.cloudbees.gasp.service.SyncIntentParams;
import com.cloudbees.gasp.service.UserSyncService;
import com.google.android.gcm.GCMRegistrar;

import java.util.List;

import static com.cloudbees.gasp.gcm.CommonUtilities.getDisplayMessageAction;
import static com.cloudbees.gasp.gcm.CommonUtilities.getExtraMessage;
import static com.cloudbees.gasp.gcm.CommonUtilities.getSenderId;
import static com.cloudbees.gasp.gcm.CommonUtilities.getServerUrl;

/**
 * Main UI for the demo app.
 */
public class MainActivity extends Activity {
    private static String TAG = MainActivity.class.getName();

    private TextView mDisplay;
    private ResponseReceiver receiver;

    private Uri mGaspReviewsUri;
    private Uri mGaspRestaurantsUri;
    private Uri mGaspUsersUri;

    private List<Review> mReviewList;
    private List<Restaurant> mRestaurantList;
    private List<User> mUserList;

    private AsyncTask<Void, Void, Void> mRegisterTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load shared preferences from res/xml/preferences.xml (first time only)
        // Subsequent activations will use the saved shared preferences from the device
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "Using Gasp Server Reviews URI: " + gaspSharedPreferences.getString("gasp_reviews_uri", ""));
        mGaspReviewsUri = Uri.parse(gaspSharedPreferences.getString("gasp_reviews_uri", ""));
        Log.i(TAG, "Using Gasp Server Restaurants URI: " + gaspSharedPreferences.getString("gasp_restaurants_uri", ""));
        mGaspRestaurantsUri = Uri.parse(gaspSharedPreferences.getString("gasp_restaurants_uri", ""));
        Log.i(TAG, "Using Gasp Server Users URI: " + gaspSharedPreferences.getString("gasp_users_uri", ""));
        mGaspUsersUri = Uri.parse(gaspSharedPreferences.getString("gasp_users_uri", ""));

        // Register Broadcast Receiver to listen for replies from data sync services
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        // Intent Services handle initial data sync
        Intent reviewsIntent = new Intent(this, ReviewSyncService.class);
        reviewsIntent.putExtra(SyncIntentParams.PARAM_IN_MSG, "reviews");
        startService(reviewsIntent);

        Intent restaurantsIntent = new Intent(this, RestaurantSyncService.class);
        restaurantsIntent.putExtra(SyncIntentParams.PARAM_IN_MSG, "restaurants");
        startService(restaurantsIntent);

        Intent usersIntent = new Intent(this, UserSyncService.class);
        usersIntent.putExtra(SyncIntentParams.PARAM_IN_MSG, "users");
        startService(usersIntent);

        checkNotNull(getServerUrl(), "SERVER_URL");
        checkNotNull(getSenderId(), "SENDER_ID");
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
        setContentView(R.layout.gcm_demo);
        mDisplay = (TextView) findViewById(R.id.display);
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(getDisplayMessageAction()));
        final String regId = GCMRegistrar.getRegistrationId(this);
        if (regId.equals("")) {
            // Automatically registers application on startup.
            GCMRegistrar.register(this, getSenderId());
        } else {
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                mDisplay.append(getString(R.string.already_registered) + "\n");
            } else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        boolean registered =
                                ServerUtilities.register(context, regId);

                        if (!registered) {
                            GCMRegistrar.unregister(context);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            /*
             * Typically, an application registers automatically, so options
             * below are disabled. Uncomment them if you want to manually
             * register or unregister the device (you will also need to
             * uncomment the equivalent options on options_menu.xml).
             */
            /*
            case R.id.options_register:
                GCMRegistrar.register(this, SENDER_ID);
                return true;
            case R.id.options_unregister:
                GCMRegistrar.unregister(this);
                return true;
             */
            case R.id.options_clear:
                mDisplay.setText(null);
                return true;

            case R.id.options_exit:
                finish();
                return true;

            case R.id.gasp_settings:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SetPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_reviews_data:
                intent = new Intent();
                intent.setClass(MainActivity.this, ReviewListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_restaurants_data:
                intent = new Intent();
                intent.setClass(MainActivity.this, RestaurantListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case R.id.gasp_users_data:
                intent = new Intent();
                intent.setClass(MainActivity.this, UserListActivity.class);
                startActivityForResult(intent, 0);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        unregisterReceiver(mHandleMessageReceiver);
        GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    private void checkNotNull(Object reference, String name) {
        if (reference == null) {
            throw new NullPointerException(
                    getString(R.string.error_config, name));
        }
    }

    // BroadcastReceiver for GCMIntentService
    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(getExtraMessage());
                    Log.d(TAG,newMessage);
                    mDisplay.append(newMessage + "\n");
                }
            };

    // BroadcastReceiver for Gasp IntentService classes
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP =
                "com.cloudbees.gasp.gcm.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(SyncIntentParams.PARAM_OUT_MSG);
            Log.d(TAG, text);
            mDisplay.append(text + "\n");
        }
    }
}