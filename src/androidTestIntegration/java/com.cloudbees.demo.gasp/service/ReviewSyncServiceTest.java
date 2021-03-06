package com.appdynamics.demo.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.appdynamics.demo.gasp.adapter.ReviewDataAdapter;
import com.appdynamics.demo.gasp.model.Review;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Copyright (c) 2013 Mark Prichard
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ReviewSyncServiceTest extends ServiceTestCase<ReviewSyncService> {
    private static final String TAG = ReviewSyncServiceTest.class.getName();

    private ReviewDataAdapter reviewAdapter;
    private CountDownLatch signal;

    public ReviewSyncServiceTest() {
        super(ReviewSyncService.class);
    }

    private void cleanDatabase() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAll();
            for (Review review : reviewList) {
                reviewData.delete(review);
            }
        } catch (Exception e) {
        } finally {
            reviewData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
        signal = new CountDownLatch(1);
    }

    public void testReviewSyncIntent() throws InterruptedException {
        startService(new Intent(getContext(), ReviewSyncService.class));

        // Allow 20 secs for the async REST call to complete
        signal.await(20, TimeUnit.SECONDS);

        try {
            reviewAdapter = new ReviewDataAdapter(getContext());
            reviewAdapter.open();

            List<Review> reviews = reviewAdapter.getAll();
            assertTrue(reviews.size() > 0);
        } finally {
            reviewAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
