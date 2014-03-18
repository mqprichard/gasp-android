package com.cloudbees.demo.gasp.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.cloudbees.demo.gasp.activity.LocationsActivity;
import com.cloudbees.demo.gasp.utils.GaspSharedPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

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

public class GaspRegistrationClient {
    private static final String TAG = GaspRegistrationClient.class.getName();

    private static GoogleCloudMessaging gcm;
    private static String regId;

    // Constants used for GCM Registration
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

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
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(LocationsActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getPreferences(context);
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
     * registration ID.
     */
    public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration Id not found.");
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
     * Registers the application with GCM/Gasp Push Notification Server.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences. Use when first registering with GCM/Gasp Push Server.
     */
    public void registerGCM(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Register device with Google Cloud Messaging
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regId = getRegistrationId(context);

                    if (regId.isEmpty()) {
                        regId = gcm.register(GCMProjectKey.SENDER_ID);
                        Log.d(TAG, "Registered device: " + regId);
                    }
                    else
                        Log.d(TAG, "Device already registered: " + regId + '\n');

                    // Register with Gasp GCM Push Notification Server
                    GaspSharedPreferences gaspSharedPreferences = new GaspSharedPreferences(context);
                    String gaspPushServerUrl = gaspSharedPreferences.getGaspPushServerUrl();

                    boolean registered = GCMRegistrationServices.register(context, regId, gaspPushServerUrl);
                    if (registered)
                        Log.d(TAG, "Registered with server (" + gaspPushServerUrl + "): " + regId);
                    else
                        Log.e(TAG, "Could not register with server (" + gaspPushServerUrl + ")");

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    // TODO:
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return null;
            }
        }.execute(null, null, null);
    }

    /**
     * Register Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    public void doRegisterGasp(final Context context) {
        try {
            if (!(regId = getRegistrationId(context)).isEmpty()) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            GaspSharedPreferences gaspSharedPreferences = new GaspSharedPreferences(context);
                            String gaspPushServerUrl = gaspSharedPreferences.getGaspPushServerUrl();

                            if (GCMRegistrationServices.register(context, regId, gaspPushServerUrl))
                                return ("Registered Device Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Registration failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.d(TAG, msg + "\n");
                    }
                }.execute(null, null, null);

            } else {
                Log.e(TAG, "Registration Id not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unregister Device with Gasp GCM Server (for Options Menu)
     * Assumes Registration ID is already set, does not register Device with GCM
     */
    public void doUnregisterGasp(final Context context) {
        if (!(regId = getRegistrationId(context)).isEmpty()) {
            try {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        try {
                            GaspSharedPreferences gaspSharedPreferences = new GaspSharedPreferences(context);
                            String gaspPushServerUrl = gaspSharedPreferences.getGaspPushServerUrl();

                            GCMRegistrationServices.unregister(context, regId, gaspPushServerUrl);
                            return ("Unregistered Device Id: " + regId);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return ("Unregister failed for Id: " + regId);
                    }

                    @Override
                    protected void onPostExecute(String msg) {
                        Log.d(TAG, msg + "\n");
                    }
                }.execute(null, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Device not registered");
        }
    }

}
