package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.AsyncRestClient;
import com.cloudbees.gasp.model.IRestListener;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by markprichard on 8/1/13.
 */
public class ReviewSyncService extends IntentService implements IRestListener {
    private static final String TAG = ReviewSyncService.class.getName();

    public static final String PARAM_IN_MSG = "";
    public static final String PARAM_OUT_MSG = "";
    private Uri mGaspReviewsUri;

    private void getGaspReviewsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_reviews_uri", "");

        this.mGaspReviewsUri = Uri.parse(gaspReviewsUri);
    }

    public Uri getGaspReviewsUri() {
        return mGaspReviewsUri;
    }

    public ReviewSyncService() {
        super("ReviewSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msg = intent.getStringExtra(PARAM_IN_MSG);

        getGaspReviewsUriSharedPreferences();
        Log.i(TAG, "Using Gasp Server Reviews URI: " + getGaspReviewsUri());

        AsyncRestClient asyncRestCall = new AsyncRestClient(getGaspReviewsUri(), this);
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
                    Review review = iterator.next();
                    reviewsDB.insertReview(review);
                    index = review.getId();
                }
                reviewsDB.close();

                Log.i(TAG, "Loaded " + index + " reviews from " + getGaspReviewsUri() + '\n');

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
