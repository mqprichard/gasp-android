/*
 * Copyright 2012 Google Inc.
 * Copyright (c) 2013 Mark Prichard, CloudBees
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
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.service.RestaurantUpdateService;
import com.cloudbees.gasp.service.ReviewUpdateService;
import com.cloudbees.gasp.service.UserUpdateService;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Calendar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {
    private static final String TAG = "GCMIntentService";

    //public static final int NOTIFICATION_ID = 1;

    public GCMIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        try {
            if (!extras.isEmpty()) {
                if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                    Log.i(TAG, "Send error: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                    Log.i(TAG, "Deleted messages on server: " + extras.toString());
                } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    Log.i(TAG, "Received: " + extras.toString());

                    int index = Integer.valueOf(extras.getString("id"));
                    String table = extras.getString("table");
                    String notificationMessage = "";

                    if (table != null) {
                        if (table.matches("reviews")) {
                            startService(new Intent(getApplicationContext(), ReviewUpdateService.class)
                                    .putExtra(MainActivity.ResponseReceiver.PARAM_ID, index));
                            notificationMessage = "There's a new Gasp! review - check it out!";
                        } else if (table.matches("restaurants")) {
                            startService(new Intent(getApplicationContext(), RestaurantUpdateService.class)
                                    .putExtra(MainActivity.ResponseReceiver.PARAM_ID, index));
                            notificationMessage = "There's a new restaurant on Gasp!";

                        } else if (table.matches("users")) {
                            startService(new Intent(getApplicationContext(), UserUpdateService.class)
                                    .putExtra(MainActivity.ResponseReceiver.PARAM_ID, index));
                            notificationMessage = "There's a new reviewer on Gasp!";
                        }
                        // Send notification message for message bar display etc
                        sendNotification(notificationMessage);
                    } else {
                        Log.e(TAG, "Error: table not specified");
                    }
                    // Release the wake lock provided by the WakefulBroadcastReceiver.
                    GCMBroadcastReceiver.completeWakefulIntent(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a notification message that a new update has been received
     *
     * @param msg Notification message
     */
    private void sendNotification(String msg) {
        long timeNow = Calendar.getInstance().getTimeInMillis();

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class).setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_gcm)
                        .setContentTitle("Gasp! Update")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify((int) timeNow, mBuilder.build());
    }


}
