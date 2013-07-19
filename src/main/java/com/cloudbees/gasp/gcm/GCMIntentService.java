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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.ReviewSyncActivity;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewsDataSource;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.lang.reflect.Type;

import static com.cloudbees.gasp.gcm.CommonUtilities.SENDER_ID;
import static com.cloudbees.gasp.gcm.CommonUtilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        Log.i(TAG, "New Review: " + intent.getStringExtra("id"));

        try {
            SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            Uri mGaspReviewsUri = Uri.parse(gaspSharedPreferences.getString("gasp_endpoint_uri", ""));
            Uri reviewUri = Uri.parse(mGaspReviewsUri + "/" + intent.getStringExtra("id"));

            ReviewsRESTQuery getReview = new ReviewsRESTQuery();
            getReview.setEndpoint(reviewUri);
            getReview.execute();

            String message = "Loaded review from: " + reviewUri;
            displayMessage(context, message);
            generateNotification(context, "New Review: " + intent.getStringExtra("id"));
        } catch (Exception e) {
            Log.e(TAG, e.getStackTrace().toString());
        }
    }

    private class ReviewsRESTQuery extends AsyncTask<Void, Void, String> {
        Review mReview;
        Uri mUri;

        protected void setEndpoint(Uri endpoint) {
            mUri = endpoint;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            ResponseHandler<String> handler = new BasicResponseHandler();
            HttpGet httpGet = new HttpGet(mUri.toString());
            String responseBody = null;

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                responseBody = handler.handleResponse(response);

                Log.d(TAG, responseBody);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result!=null) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Review>() {}.getType();
                    mReview = gson.fromJson(result, type);

                    ReviewsDataSource reviewsDB = new ReviewsDataSource(getApplicationContext());
                    reviewsDB.open();
                    reviewsDB.insertReview(mReview);
                    reviewsDB.close();
                } catch(Exception e) {
                    Log.e(TAG, e.getStackTrace().toString());
                }
            }
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);

        displayMessage(context, message);
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    // TODO Fix Notification version issues
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.
                                        Builder(context).
                                        setContentTitle("New Gasp! Review").
                                        setSmallIcon(R.drawable.ic_stat_gcm).
                                        build();
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, ReviewSyncActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

}
