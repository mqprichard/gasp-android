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

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
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
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

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
            sendNotification("Gasp! Update");
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
            sendNotification("New " + table + ": " + index);

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Send a notification message that a new update has been received
     * @param msg
     */
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("New Gasp! Update")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


}
