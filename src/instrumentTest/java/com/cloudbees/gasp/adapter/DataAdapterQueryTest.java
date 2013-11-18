package com.cloudbees.gasp.adapter;

import android.test.AndroidTestCase;

import com.cloudbees.gasp.model.Review;

import java.util.List;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
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

/**
 * Tests GaspDataAdapter query methods
 */
public class DataAdapterQueryTest extends AndroidTestCase {
    private static final int MAX_ELEMENTS = 10;
    private static final int testId = 1;
    private static final String testComment = "test";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAll();
            for (Review review : reviewList) {
                reviewData.delete(review);
            }
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }

        reviewData.open();
        for (int i = 0; i < MAX_ELEMENTS; i++) {
            Review review = new Review();
            review.setId(testId + i);
            review.setRestaurant_id(testId + (i % 3));
            review.setUser_id(testId);
            review.setComment("test");
            review.setStar(1);
            reviewData.insert(review);
        }
    }

    public void testGetAll() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAll();

            // Check all rows returned
            assertEquals(reviewList.size(), MAX_ELEMENTS);

            // Check ordering
            assertEquals(reviewList.get(0).getId(), testId);
            assertEquals(reviewList.get(MAX_ELEMENTS - 1).getId(), MAX_ELEMENTS);
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    public void testGetAllDesc() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAllDesc();

            // Check all rows returned
            assertEquals(reviewList.size(), MAX_ELEMENTS);

            // Check ordering
            assertEquals(reviewList.get(0).getId(), MAX_ELEMENTS);
            assertEquals(reviewList.get(MAX_ELEMENTS - 1).getId(), testId);
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    public void testGetLastNDesc() {
        final int n = 5;        // n < MAX_ELEMENTS
        final int nn = 50;      // nn > MAX_ELEMENTS

        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getLastNDesc(n);

            // Check # of rows returned
            assertEquals(reviewList.size(), n);

            // Check ordering
            assertEquals(reviewList.get(0).getId(), MAX_ELEMENTS);
            assertEquals(reviewList.get(n - 1).getId(), testId + (MAX_ELEMENTS - n));

            reviewList = reviewData.getLastNDesc(nn);

            //Check # of rows returned
            assertEquals(reviewList.size(), MAX_ELEMENTS);

            // Check ordering
            assertEquals(reviewList.get(0).getId(), MAX_ELEMENTS);
            assertEquals(reviewList.get(MAX_ELEMENTS - 1).getId(), testId);
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    public void testGetReviewsByRestaurant() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();

        try {
            List<Review> reviewList = reviewData.getAllByRestaurant(testId);
            assert (reviewList.size() > 0);
            for (int i = 0; i < reviewList.size(); i++) {
                assertEquals(reviewList.get(i).getRestaurant_id(), testId);
            }
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
