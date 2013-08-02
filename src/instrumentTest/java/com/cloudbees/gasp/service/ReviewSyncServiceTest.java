package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 8/2/13.
 */
public class ReviewSyncServiceTest extends ServiceTestCase<ReviewSyncService> {
    private static final String TAG = ReviewSyncServiceTest.class.getName();

    ReviewAdapter reviewAdapter;
    CountDownLatch signal;

    public ReviewSyncServiceTest() {
        super(ReviewSyncService.class);
        signal = new CountDownLatch(1);
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
    }

    public void testReviewSyncIntent () throws InterruptedException {
        startService(new Intent(getContext(), ReviewSyncService.class));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        try {
            reviewAdapter = new ReviewAdapter(getContext());
            reviewAdapter.open();

            List<Review> reviews = reviewAdapter.getAll();
            assertTrue(reviews.size() > 1);
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
