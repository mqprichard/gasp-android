package com.cloudbees.demo.gasp.adapter;

import android.test.AndroidTestCase;

import com.cloudbees.demo.gasp.model.Restaurant;
import com.cloudbees.demo.gasp.model.Review;
import com.cloudbees.demo.gasp.model.User;

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
 * Tests GaspDataAdapter insert/delete methods
 */
public class DatabaseTest extends AndroidTestCase {

    private static final int testId = 1;
    private static final int testUserId = 1;
    private static final int testRestaurantId = 1;
    private static final int testStar = 1;
    private static final String testComment = "Test Comment";
    private static final String testName = "Test Name";
    private static final String testWebsite = "http://www.restaurant.com/";
    private static final String testPlacesId = "1234567890";
    private static final String testPlacesId2 = "0123456789";
    private static final String testPlacesId3 = "0000000000";

    protected void setUp() {
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

        RestaurantDataAdapter restaurantAdapter = new RestaurantDataAdapter(getContext());
        restaurantAdapter.open();
        try {
            List<Restaurant> restaurantList = restaurantAdapter.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantAdapter.delete(restaurant);
            }
        } catch (Exception e) {
        } finally {
            restaurantAdapter.close();
        }

        UserDataAdapter userAdapter = new UserDataAdapter(getContext());
        userAdapter.open();
        try {
            List<User> userList = userAdapter.getAll();
            for (User user : userList) {
                userAdapter.delete(user);
            }
        } catch (Exception e) {
        } finally {
            userAdapter.close();
        }
    }

    protected void tearDown() {
    }

    public void testReviewAdapter() {
        ReviewDataAdapter reviewData = new ReviewDataAdapter(getContext());
        List<Review> reviewList;

        reviewData.open();
        assertEquals(0, reviewData.getLastId());
        reviewData.close();

        Review review = new Review();

        review.setId(testId);
        review.setRestaurant_id(testRestaurantId);
        review.setUser_id(testUserId);
        review.setComment(testComment);
        review.setStar(testStar);

        reviewData.open();
        reviewData.insert(review);
        reviewList = reviewData.getAll();
        assertEquals(reviewList.size(), 1);

        assertEquals(reviewList.get(0).getId(), testId);
        assertEquals(reviewList.get(0).getUser_id(), testUserId);
        assertEquals(reviewList.get(0).getRestaurant_id(), testRestaurantId);
        assertEquals(reviewList.get(0).getComment(), testComment);
        assertEquals(reviewList.get(0).getStar(), testStar);

        review.setId(testId + 1);
        reviewData.insert(review);
        reviewList = reviewData.getAll();
        assertEquals(reviewList.size(), 2);
        assertEquals(reviewList.get(1).getId(), testId + 1);
        assertEquals(2, reviewData.getLastId());

        reviewData.delete(review);
        reviewList = reviewData.getAll();
        assertEquals(reviewList.size(), 1);
        reviewData.close();

        reviewData.open();
        assertEquals(1, reviewData.getLastId());
        reviewData.close();
    }

    public void testRestaurantAdapter() {
        RestaurantDataAdapter restaurantData = new RestaurantDataAdapter(getContext());
        List<Restaurant> restaurantList;

        restaurantData.open();
        assertEquals(0, restaurantData.getLastId());
        restaurantData.close();

        Restaurant restaurant = new Restaurant();

        restaurant.setId(testId);
        restaurant.setName(testName);
        restaurant.setWebsite(testWebsite);
        restaurant.setPlacesId(testPlacesId);

        restaurantData.open();
        restaurantData.insert(restaurant);
        restaurantList = restaurantData.getAll();
        assertEquals(restaurantList.size(), 1);

        assertEquals(restaurantList.get(0).getId(), testId);
        assertEquals(restaurantList.get(0).getName(), testName);
        assertEquals(restaurantList.get(0).getWebsite(), testWebsite);
        assertEquals(restaurantList.get(0).getPlacesId(), testPlacesId);

        restaurant.setId(testId + 1);
        restaurant.setPlacesId(testPlacesId2);
        restaurantData.insert(restaurant);
        restaurantList = restaurantData.getAll();
        assertEquals(restaurantList.size(), 2);
        assertEquals(restaurantList.get(1).getId(), 2);

        Restaurant testResult = restaurantData.findRestaurantByPlacesId(testPlacesId2);
        assert (testResult.getPlacesId().equals(testPlacesId2));
        assertEquals(2, restaurantData.getLastId());

        assert (restaurantData.findRestaurantByPlacesId(testPlacesId3) == null);

        restaurantData.delete(restaurant);
        restaurantList = restaurantData.getAll();
        assertEquals(restaurantList.size(), 1);

        restaurantData.close();

        restaurantData.open();
        restaurantData.getLastId();
        restaurantData.close();
    }

    public void testUserAdapter() {
        UserDataAdapter userData = new UserDataAdapter(getContext());
        List<User> userList;

        userData.open();
        assertEquals(0, userData.getLastId());
        userData.close();

        User user = new User();

        user.setId(testId);
        user.setName(testName);

        userData.open();
        userData.insert(user);
        userList = userData.getAll();
        assertEquals(userList.size(), 1);

        assertEquals(userList.get(0).getId(), testId);
        assertEquals(userList.get(0).getName(), testName);

        user.setId(testId + 1);
        userData.insert(user);
        userList = userData.getAll();
        assertEquals(userList.size(), 2);
        assertEquals(userList.get(1).getId(), 2);
        assertEquals(2, userData.getLastId());

        userData.delete(user);
        userList = userData.getAll();
        assertEquals(userList.size(), 1);

        userData.close();

        userData.open();
        userData.getLastId();
        userData.close();
    }
}
