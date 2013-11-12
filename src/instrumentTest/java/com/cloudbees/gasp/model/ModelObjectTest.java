package com.cloudbees.gasp.model;

import junit.framework.TestCase;

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

public class ModelObjectTest extends TestCase {
    private static final int testId = 1;
    private static final int testUserId = 1;
    private static final int testRestaurantId = 1;
    private static final int testStar = 1;
    private static final String testComment = "Test Comment";
    private static final String testName = "Test Name";
    private static final String testWebsite = "http://www.restaurant.com/";
    private static final String testPlacesId = "1234567890";

    protected void setUp() {}
    protected void tearDown() {}

    public void testReview() {
        Review review = new Review();

        review.setId(testId);
        review.setRestaurant_id(testRestaurantId);
        review.setUser_id(testUserId);
        review.setComment(testComment);
        review.setStar(testStar);

        assertEquals(review.getId(), testId);
        assertEquals(review.getRestaurant_id(), testRestaurantId);
        assertEquals(review.getUser_id(), testUserId);
        assertEquals(review.getStar(), testStar);
        assertEquals(review.getComment(), testComment);
        assertEquals(review.getRestaurant(), "/restaurants/" + testRestaurantId);
        assertEquals(review.getUser(), "/users/" + testUserId);
        assertEquals(review.toString(), "Review #" + testId + ": ("
                                        + testStar + " Stars) " + testComment);
    }

    public void testRestaurant() {
        Restaurant restaurant = new Restaurant();

        restaurant.setId(testId);
        restaurant.setName(testName);
        restaurant.setWebsite(testWebsite);
        restaurant.setPlacesId(testPlacesId);

        assertEquals(restaurant.getId(), testId);
        assertEquals(restaurant.getName(), testName);
        assertEquals(restaurant.getWebsite(), testWebsite);
        assertEquals(restaurant.getUrl(), "/restaurants/" + testId);
        assertEquals(restaurant.getPlacesId(), testPlacesId);
        assertEquals(restaurant.toString(), "Restaurant #" + testId + ": "
                + testName + " (" + testPlacesId + ")");
    }

    public void testUser() {
        User user = new User();

        user.setId(testId);
        user.setName(testName);

        assertEquals(user.getId(), testId);
        assertEquals(user.getName(), testName);
        assertEquals(user.getUrl(), "/users/" + testId);
        assertEquals(user.toString(), "User #" + testId + ": " + testName);
    }
}

