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
        } finally {
            reviewData.close();
        }

        reviewData.open();
        for (int i = 0; i <= MAX_ELEMENTS; i++) {
            Review review = new Review();
            review.setId(testId + i);
            review.setComment(testComment + i);
            reviewData.insert(review);
        }
    }

    public void testGetAll() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAll();
            assertEquals(reviewList.get(0).getId(), testId);
            assertEquals(reviewList.size(), MAX_ELEMENTS);
            assertEquals(reviewList.get(MAX_ELEMENTS).getId(), MAX_ELEMENTS);
        } catch (Exception e) {
        } finally {
            reviewData.close();
        }
    }

    public void testGetAllDesc() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAllDesc();
            assertEquals(reviewList.get(0).getId(), testId + MAX_ELEMENTS);
            assertEquals(reviewList.size(), MAX_ELEMENTS);
            assertEquals(reviewList.get(MAX_ELEMENTS).getId(), 0);
        } catch (Exception e) {
        } finally {
            reviewData.close();
        }
    }

    public void testGetLastNDesc() {
        final int n = 5;
        final int nn = 50;

        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getLastNDesc(n);
            assertEquals(reviewList.get(0).getId(), MAX_ELEMENTS);
            assertEquals(reviewList.size(), n);
            assertEquals(reviewList.get(MAX_ELEMENTS).getId(), MAX_ELEMENTS - n);

            reviewList = reviewData.getLastNDesc(nn);
            assertEquals(reviewList.get(0).getId(), MAX_ELEMENTS);
            assertEquals(reviewList.size(), n);
            assertEquals(reviewList.get(MAX_ELEMENTS).getId(), 0);
        } catch (Exception e) {
        } finally {
            reviewData.close();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
