/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.fragment.TwitterAuthenticationFragment;
import com.cloudbees.gasp.gcm.GCMRegistration;
import com.cloudbees.gasp.service.RestaurantSyncService;
import com.cloudbees.gasp.service.ReviewSyncService;
import com.cloudbees.gasp.service.UserSyncService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getName();

    // Base URL of the Gasp! GCM Push Server
    private static String SERVER_URL;
    public static String getSERVER_URL() {
        return SERVER_URL;
    }

    // Google API project id registered to use GCM.
    private static final String SENDER_ID = "960428562804";

    // Constants used for GCM Registration
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private AsyncTask<Void, Void, Void> mRegisterTask;
    private TextView mDisplay;
    private ResponseReceiver mGaspMessageReceiver;
    private GoogleCloudMessaging gcm;
    private Context context;
    private String regId;

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        } catch (NullPointerException e) {
            throw new RuntimeException("NPE getting package name: " + e);
        }
        return packageInfo.versionCode;
    }

    /**
     * Add Support for third-party monitoring libraries
     */
    private void addThirdPartyLibs() {
        // Initialize NewRelic monitoring agent
        //NewRelic.withApplicationToken("AA83f38cfac2e854342e6964065753db86d00c513c").start(this.getApplication());

        // Initialize TestFlight SDK agent
        //TestFlight.takeOff(this.getApplication(), "6f8d819d-c09b-4080-b06d-1f048f0b6fcb");

        // Initialise Vessel
        //VesselSDK.initialize(getApplicationContext(), "a0RNYlBzaU9qUlVMckt1RlZmdEJjcWx6" );
    }

    /**
     * Registers the application with GCM/Gasp Push Notification Server.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences. Use when first registering with GCM/Gasp Push Server.
     */
    private void doRegisterGCM() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    // Register device with Google Cloud Messaging
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(SENDER_ID);
                    msg = "Device registered: " + regId;

                    // Register with Gasp GCM Push Notification Server
                    boolean registered = GCMRegistration.register(context, regId);
                    if (registered) Log.d(TAG, "Registered with server (" + SERVER_URL + "): " + regId);
                    else Log.e(TAG, "Could not register with server (" + SERVER_URL + ")");

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // TODO:
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    /**
     * Unregister Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    private void doUnregisterGasp() {
        if (! (regId = getRegistrationId(context)).isEmpty()) {
            try{
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            GCMRegistration.unregister(context, regId);
                            return ("Unregistered Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Unregister failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        mDisplay.append(msg + "\n");
                    }
                }.execute(null, null, null);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e(TAG, "Registration Id not found");
        }
    }

    /**
     * Register Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    private void doRegisterGasp() {
        try {
            if (! (regId = getRegistrationId(context)).isEmpty()) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            if (GCMRegistration.register(context, regId))
                                return ("Registered Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Unregister failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        mDisplay.append(msg + "\n");
                    }
                }.execute(null, null, null);

            }
            else {
                Log.e(TAG, "Registration Id not found");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Request a Twitter API v1.1 OAuth Token
     * Uses TwitterAuthenticationFragment
     */
    private void requestTwitterOAuthToken() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        TwitterAuthenticationFragment responder =
                (TwitterAuthenticationFragment) fm.findFragmentByTag("TwitterAuthentication");
        if (responder == null) {
            responder = new TwitterAuthenticationFragment();

            ft.add(responder, "TwitterAuthentication");
        }
        ft.commit();
    }

    // BroadcastReceiver for Gasp sync/update messages
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String PARAM_IN_MSG = "syncSend";
        public static final String PARAM_OUT_MSG = "syncRecv";
        public static final String PARAM_ID = "id";
        public static final String ACTION_RESP =
                "com.cloudbees.gasp.gcm.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra(PARAM_OUT_MSG);
            Log.d(TAG, text);
            mDisplay.append(text + "\n");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addThirdPartyLibs();

        context = getApplicationContext();

        setContentView(R.layout.gasp_console_layout);
        mDisplay = (TextView) findViewById(R.id.display);

        // Load shared preferences from res/xml/preferences.xml (first time only)
        // Subsequent activations will use the saved shared preferences from the device
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.i(TAG, "Using Gasp Server Reviews URI: " + gaspSharedPreferences.getString("gasp_reviews_uri", ""));
        Log.i(TAG, "Using Gasp Server Restaurants URI: " + gaspSharedPreferences.getString("gasp_restaurants_uri", ""));
        Log.i(TAG, "Using Gasp Server Users URI: " + gaspSharedPreferences.getString("gasp_users_uri", ""));
        Log.i(TAG, "Using Gasp GCM Push Server URI: " + gaspSharedPreferences.getString("gasp_push_uri", ""));
        SERVER_URL = gaspSharedPreferences.getString("gasp_push_uri", "");

        // Add support for third-party libraries
        //addThirdPartyLibs();

        // Register Broadcast Receiver to listen for replies from data sync services
        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        mGaspMessageReceiver = new ResponseReceiver();
        registerReceiver(mGaspMessageReceiver, filter);

        // Intent Services handle initial data sync
        Intent reviewsIntent = new Intent(this, ReviewSyncService.class);
        reviewsIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "reviews");
        startService(reviewsIntent);

        Intent restaurantsIntent = new Intent(this, RestaurantSyncService.class);
        restaurantsIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "restaurants");
        startService(restaurantsIntent);

        Intent usersIntent = new Intent(this, UserSyncService.class);
        usersIntent.putExtra(ResponseReceiver.PARAM_IN_MSG, "users");
        startService(usersIntent);

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId(context);

            if (regId.isEmpty()) {
                doRegisterGCM();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        // Request Twitter OAuth Token
        requestTwitterOAuthToken();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //final String regId;

        switch(item.getItemId()) {
            // (Re-)register with Gasp GCM  Server
            case R.id.options_register:
                doRegisterGasp();
                return true;

            // Unregister with Gasp GCM Server
            case R.id.options_unregister:
                doUnregisterGasp();
                return true;

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

            case R.id.gasp_menu_twitter:
                intent = new Intent();
                intent.setClass(MainActivity.this, TwitterStreamActivity.class);
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

            case R.id.gasp_menu_places:
                intent = new Intent();
                intent.setClass(MainActivity.this, PlacesActivity.class);
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
        // Unregister from Gasp GCM Server
        doUnregisterGasp();
        unregisterReceiver(mGaspMessageReceiver);
        super.onDestroy();
    }
}