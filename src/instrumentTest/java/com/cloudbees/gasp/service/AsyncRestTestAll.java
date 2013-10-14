package com.cloudbees.gasp.service;

import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.UiThreadTest;
import android.util.Log;

import com.cloudbees.gasp.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
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

public class AsyncRestTestAll extends AndroidTestCase implements IRESTListener {
    private static final String TAG = AsyncRestTestAll.class.getName();
    private static final String REVIEWS = "http://gasp.partnerdemo.cloudbees.net/reviews";

    private CountDownLatch signal;

    protected void setUp() throws Exception {
        super.setUp();
        signal = new CountDownLatch(1);
    }

    @UiThreadTest
    public void testAsyncRestTask() throws InterruptedException {
        try {
            AsyncRESTClient asyncRestCall = new AsyncRESTClient(Uri.parse(REVIEWS), this);
            asyncRestCall.getAll();

            // Allow 20 secs for the async REST call to complete
            signal.await(20, TimeUnit.SECONDS);
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
