package com.cloudbees.gasp.model;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 7/24/13.
 */
public class AsyncRestTestAll extends AndroidTestCase implements IRestListener {
    private static final String TAG = AsyncRestTestAll.class.getName();
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
            asyncRestCall.getAll();

            // Allow 10 secs for the async REST call to complete
            signal.await(10, TimeUnit.SECONDS);
        }
        catch (Exception e) {}
    }

    @Override
    public void onCompleted(String result) {
        assertNotNull(result);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Review>>() {}.getType();
        List<Review> list = gson.fromJson(result, type);
        assertTrue(list.size() > 0);
        Log.d(TAG, "# of items: " + list.size());
        signal.countDown();
    }
}
