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
public class AdapterTest extends AndroidTestCase implements IRestListener {
    private static final String TAG = AdapterTest.class.getName();
    private static final String REVIEWS = "http://gasp.mqprichard.cloudbees.net/reviews";

    AsyncRestTask asyncRestCall;
    CountDownLatch signal;


    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
    }

    @UiThreadTest
    public void testAsyncRestTask() throws InterruptedException {
        try {
            asyncRestCall = new AsyncRestTask(Uri.parse(REVIEWS), this);
            asyncRestCall.doRest();
            signal.await(30, TimeUnit.SECONDS);
        }
        catch (Exception e) {}
    }

    @Override
    public void callCompleted(String result) {
        assertNotNull(result);
        Log.d(TAG, result);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Review>>() {}.getType();
        List<Review> list = gson.fromJson(result, type);
        assertTrue(list.size() > 0);
        signal.countDown();
    }
}
