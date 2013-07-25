package com.cloudbees.gasp.model;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 7/24/13.
 */
public class AdapterTest2 extends AndroidTestCase implements IRestListener2 {
    private static final String TAG = AdapterTest2.class.getName();
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
            asyncRestCall.getAll();
            signal.await(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {}
    }

    @Override
    public void onCompletedIndex(String result) {
        assertNotNull(result);
        Gson gson = new Gson();
        Type type = new TypeToken<Review>() {}.getType();
        Review review = gson.fromJson(result, type);
        assertEquals(review.getId(), 1);
    }

    @Override
    public void onCompletedAll(String result) {
        assertNotNull(result);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Review>>() {}.getType();
        List<Review> list = gson.fromJson(result, type);
        assertTrue(list.size() > 0);
        signal.countDown();
    }
}
