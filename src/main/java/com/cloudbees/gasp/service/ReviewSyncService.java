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

package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

public class ReviewSyncService extends IntentService implements IRESTListener {
    private static final String TAG = ReviewSyncService.class.getName();

    private Uri mGaspReviewsUri;

    private void getGaspReviewsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_reviews_uri", "");

        this.mGaspReviewsUri = Uri.parse(gaspReviewsUri);
    }

    private Uri getGaspReviewsUri() {
        return mGaspReviewsUri;
    }

    public ReviewSyncService() {
        super("ReviewSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getGaspReviewsUriSharedPreferences();
        Log.i(TAG, "Using Gasp Server Reviews URI: " + getGaspReviewsUri());

        AsyncRESTClient asyncRestCall = new AsyncRESTClient(getGaspReviewsUri(), this);
        asyncRestCall.getAll();
    }

    @Override
    public void onCompleted(String results){
        Log.i(TAG, "Response from " + mGaspReviewsUri.toString() + " :" + results + '\n');

        if (results!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Review>>() {}.getType();
                List<Review> reviews = gson.fromJson(results, type);

                ReviewAdapter reviewsDB = new ReviewAdapter(getApplicationContext());
                reviewsDB.open();
                ListIterator<Review> iterator = reviews.listIterator();
                int index = 0;

                while (iterator.hasNext()) {
                    try {
                        Review review = iterator.next();
                        reviewsDB.insertReview(review);
                        index = review.getId();
                    } catch (SQLiteConstraintException e) {
                        // Attempting to overwrite existing records will throw an exception
                        // Ignore these as we want to re-sync on startup
                    }
                }
                reviewsDB.close();

                String resultTxt = "Loaded " + index + " reviews from " + getGaspReviewsUri();
                Log.i(TAG, resultTxt + '\n');

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(MainActivity.ResponseReceiver.PARAM_OUT_MSG, resultTxt);
                sendBroadcast(broadcastIntent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
