package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;

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

public class ReviewUpdateServiceTest extends ServiceTestCase<ReviewUpdateService> {
    private static final String TAG = ReviewUpdateServiceTest.class.getName();

    private ReviewAdapter reviewAdapter;
    private CountDownLatch signal;

    public ReviewUpdateServiceTest() {
        super(ReviewUpdateService.class);
    }

    private void cleanDatabase() {
        ReviewAdapter reviewData = new ReviewAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAll();
            for (Review review : reviewList) {
                reviewData.deleteReview(review);
            }
        }
        catch(Exception e){}
        finally {
            reviewData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
        signal = new CountDownLatch(1);
    }

    public void testReviewUpdateIntent () throws InterruptedException {
        startService(new Intent(getContext(), ReviewUpdateService.class)
                                .putExtra(SyncIntentParams.PARAM_ID, 1));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        try {
            reviewAdapter = new ReviewAdapter(getContext());
            reviewAdapter.open();

            List<Review> reviews = reviewAdapter.getAll();
            assertTrue(reviews.size() > 0);
        }
        finally {
            reviewAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
