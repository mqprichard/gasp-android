package com.cloudbees.demo.gasp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.demo.gasp.R;

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

public class Preferences {
    private static final String TAG = Preferences.class.getName();

    // Base URL of the Gasp! GCM Push Server (Shared Preferences)
    private static String mGaspPushServerUrl;
    public static String getGaspPushServerUrl() { return mGaspPushServerUrl; }

    // Google Places API Search radius (Shared Preferences)
    private static int mGaspSearchRadius;
    public static int getGaspSearchRadius() { return mGaspSearchRadius; }

    // URL of the Gasp GCM Push Server (Shared Preferences)
    private static String mGaspServerUrl;
    public static String getGaspServerUrl() { return mGaspServerUrl; }

    public Preferences(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String gaspServerKey = context.getResources().getString(R.string.gasp_server_uri_preferences);
        mGaspServerUrl = gaspSharedPreferences.getString(gaspServerKey, "");
        Log.i(TAG, "Using Gasp Server URI: " + mGaspServerUrl);

        String gaspPushServerKey = context.getResources().getString(R.string.gasp_push_uri_preferences);
        mGaspPushServerUrl = gaspSharedPreferences.getString(gaspPushServerKey, "");
        Log.i(TAG, "Using Gasp Push Server URI: " + mGaspPushServerUrl);

        String key = context.getResources().getString(R.string.places_search_radius_preferences, "");
        String defaultRadius = context.getResources().getStringArray(R.array.radius_entry_values)[0];
        mGaspSearchRadius = Integer.valueOf(gaspSharedPreferences.getString(key, defaultRadius));
        Log.i(TAG, "Using Google Places API search radius: " + mGaspSearchRadius);
    }
}
