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
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.ConsoleActivity;
import com.cloudbees.gasp.R;
import com.cloudbees.gasp.adapter.ReviewDataAdapter;
import com.cloudbees.gasp.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ReviewUpdateService extends IntentService implements IRESTListener {
    private static final String TAG = ReviewUpdateService.class.getName();

    private Uri mGaspReviewsUri;

    private void getGaspReviewsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString(getString(R.string.gasp_server_uri_preferences), "")
                + getString(R.string.gasp_reviews_location);

        this.mGaspReviewsUri = Uri.parse(gaspReviewsUri);
    }

    public ReviewUpdateService() {
        super("ReviewUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int index = intent.getIntExtra(ConsoleActivity.ResponseReceiver.PARAM_ID, 0);

        if (index == 0) {
            Log.d(TAG, "Error - invalid index");
        } else {
            getGaspReviewsUriSharedPreferences();

            AsyncRESTClient asyncRestCall = new AsyncRESTClient(mGaspReviewsUri, this);
            asyncRestCall.getIndex(index);
        }
    }

    @Override
    public void onCompleted(String result) {
        Log.i(TAG, "Response:" + result + '\n');

        if (result != null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Review>() {
                }.getType();
                Review mReview = gson.fromJson(result, type);

                ReviewDataAdapter reviewsDB = new ReviewDataAdapter(getApplicationContext());
                reviewsDB.open();
                reviewsDB.insert(mReview);
                reviewsDB.close();

                String resultTxt = "Loaded review: " + mReview.getId();
                Log.i(TAG, resultTxt + '\n');

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(ConsoleActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(ConsoleActivity.ResponseReceiver.PARAM_OUT_MSG, resultTxt);
                sendBroadcast(broadcastIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
