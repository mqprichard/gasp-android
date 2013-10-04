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

package com.cloudbees.gasp.gcm;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.service.RestaurantUpdateService;
import com.cloudbees.gasp.service.ReviewUpdateService;
import com.cloudbees.gasp.service.SyncIntentParams;
import com.cloudbees.gasp.service.UserUpdateService;

import static com.cloudbees.gasp.gcm.CommonUtilities.displayMessage;
import static com.cloudbees.gasp.gcm.CommonUtilities.getSenderId;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(getSenderId());
    }

    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        ServerUtilities.register(context, registrationId);
    }

    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!intent.hasExtra("id")) {
            Log.d(TAG, "Message received");
            generateNotification(getApplicationContext(), "Gasp! Update");
            return;
        }

        int index = Integer.parseInt(intent.getStringExtra("id"));
        String table = intent.getStringExtra("table");
        Log.i(TAG, "New " + table + " update (" + index + ")");

        try {
            if (table != null) {
                if (table.matches("reviews")) {
                    startService(new Intent(getApplicationContext(), ReviewUpdateService.class)
                            .putExtra(SyncIntentParams.PARAM_ID, index));
                }
                else if (table.matches("restaurants")) {
                    startService(new Intent(getApplicationContext(), RestaurantUpdateService.class)
                            .putExtra(SyncIntentParams.PARAM_ID, index));
                }
                else if (table.matches("users")) {
                    startService(new Intent(getApplicationContext(), UserUpdateService.class)
                            .putExtra(SyncIntentParams.PARAM_ID, index));
                }
                else {
                    Log.e(TAG, "Error: unknown table: " + table);
                    return;
                }
            }

            // Send notification message for message bar display etc
            generateNotification(getApplicationContext(), "New " + table + ": " + index);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    // TODO Fix Notification version issues
    private static void generateNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.
                                        Builder(context).
                                        setContentTitle("New Gasp! Update").
                                        setSmallIcon(R.drawable.ic_stat_gcm).
                                        build();

        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
