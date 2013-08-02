package com.cloudbees.gasp.service;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.cloudbees.gasp.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 7/24/13.
 */
public class AsyncRestTestIndex extends AndroidTestCase implements IRestListener {
    private static final String TAG = AsyncRestTestIndex.class.getName();
    private static final String REVIEWS = "http://gasp.mqprichard.cloudbees.net/reviews";

    AsyncRestClient asyncRestCall;
    CountDownLatch signal;


    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
    }

    @UiThreadTest
    public void testAsyncRestTask() throws InterruptedException {
        try {
            asyncRestCall = new AsyncRestClient(Uri.parse(REVIEWS), this);
            asyncRestCall.getIndex(1);

            // Allow 10 secs for the async REST call to complete
            signal.await(10, TimeUnit.SECONDS);
        }
        catch (Exception e) {}
    }

    @Override
    public void onCompleted(String result) {
        assertNotNull(result);
        Gson gson = new Gson();
        Type type = new TypeToken<Review>() {}.getType();
        Review review = gson.fromJson(result, type);
        assertEquals(review.getId(), 1);
        Log.d(TAG, "Id: " + review.getId());
        Log.d(TAG, "Star: " + review.getStar());
        Log.d(TAG, "Comment: " + review.getComment());
        signal.countDown();
    }
}
