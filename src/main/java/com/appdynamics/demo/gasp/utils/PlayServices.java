package com.appdynamics.demo.gasp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.appdynamics.demo.gasp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

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

public class PlayServices {
    private static final String TAG = PlayServices.class.getName();

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     * @param context the application context
     * @return whether Play Services APK available
     */
    public static boolean checkPlayServices(Context context) {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            // TODO: Handle recoverable Play Services errors
            //if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
            //    GooglePlayServicesUtil.getErrorDialog(resultCode, this,
            //            PLAY_SERVICES_RESOLUTION_REQUEST).show();
            Log.e(TAG, "This device is not supported.");
            Toast.makeText(context,
                           R.string.common_google_play_services_unsupported_text,
                           Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
