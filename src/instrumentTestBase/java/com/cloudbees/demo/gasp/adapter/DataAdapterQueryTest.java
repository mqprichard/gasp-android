package com.cloudbees.demo.gasp.adapter;

import android.test.AndroidTestCase;

import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.model.Review;

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
    private static final String testPlacesId = "0123456789";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Clear Review data
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

        // Insert test Review data
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
        reviewData.close();

        // Clear Restaurant data
        RestaurantDataAdapter restaurantData = new RestaurantDataAdapter(getContext());
        restaurantData.open();
        try {
            List<Restaurant> restaurantList = restaurantData.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantData.delete(restaurant);
            }
        } catch (Exception e) {
            fail();
        } finally {
            restaurantData.close();
        }

        // Insert test Restaurant data
        restaurantData.open();
        for (int i = 0; i < MAX_ELEMENTS; i++) {
            Restaurant restaurant = new Restaurant();
            restaurant.setId(testId + i);
            restaurant.setName("test");
            restaurant.setPlacesId(testPlacesId + i);
            restaurant.setWebsite("http://www.gasp.com/");
            restaurantData.insert(restaurant);
        }
        restaurantData.close();

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

    public void testReviewsByRestaurant() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();

        try {
            List<Review> reviewList = reviewData.getAllByRestaurant(testId);

            // Check selection by Restaurant
            assert (reviewList.size() > 0);
            for (Review aReviewList : reviewList) {
                assertEquals(aReviewList.getRestaurant_id(), testId);
            }

            // Check ordering
            assert (reviewList.get(0).getId() < reviewList.get(1).getId());
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    public void testReviewsLastNByRestaurant() {
        final int n = 2;

        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        reviewData.open();

        try {
            List<Review> reviewList = reviewData.getLastNByRestaurant(testId, n);

            // Check # of rows returned
            assertEquals(reviewList.size(), n);

            // Check selection by Restaurant
            for (Review aReviewList : reviewList) {
                assertEquals(aReviewList.getRestaurant_id(), testId);
            }

            // Check DESC ordering
            assert (reviewList.get(0).getId() > reviewList.get(1).getId());
        } catch (Exception e) {
            fail();
        } finally {
            reviewData.close();
        }
    }

    public void testRestaurantByPlacesId() {
        RestaurantDataAdapter restaurantData = new RestaurantDataAdapter(getContext());
        restaurantData.open();

        try {
            Restaurant restaurant =
                    restaurantData.findRestaurantByPlacesId(testPlacesId + (MAX_ELEMENTS - 1));
            assert (restaurant.getPlacesId().equals(String.valueOf(testPlacesId + (MAX_ELEMENTS - 1))));

            // Check for null on no-match
            restaurant = restaurantData.findRestaurantByPlacesId(testPlacesId + (MAX_ELEMENTS + 1));
            assert (restaurant == null);
        } catch (Exception e) {
            fail();
        } finally {
            restaurantData.close();
        }

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
