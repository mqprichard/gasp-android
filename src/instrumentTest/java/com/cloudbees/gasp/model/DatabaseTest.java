package com.cloudbees.gasp.model;

import android.test.AndroidTestCase;

import java.util.List;

/**
 * Created by markprichard on 7/23/13.
 */
public class DatabaseTest extends AndroidTestCase {

    private static final int testId = 1;
    private static final int testUserId = 1;
    private static final int testRestaurantId = 1;
    private static final int testStar = 1;
    private static final String testComment = "Test Comment";
    private static final String testName = "Test Name";
    private static final String testWebsite = "http://www.restaurant.com/";

    protected void setUp() {
        ReviewAdapter reviewData = new ReviewAdapter(getContext());
        reviewData.open();
        try {
            List<Review> reviewList = reviewData.getAllReviews();
            for (Review review : reviewList) {
                reviewData.deleteReview(review);
            }
        }
        catch(Exception e){}
        finally {
            reviewData.close();
        }

        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getContext());
        restaurantAdapter.open();
        try {
            List<Restaurant> restaurantList = restaurantAdapter.getAllRestaurants();
            for (Restaurant restaurant : restaurantList) {
                restaurantAdapter.deleteRestaurant(restaurant);
            }
        }
        catch (Exception e) {}
        finally {
            restaurantAdapter.close();
        }

        UserAdapter userAdapter = new UserAdapter(getContext());
        userAdapter.open();
        try {
            List<User> userList = userAdapter.getAllUsers();
            for (User user : userList) {
                userAdapter.deleteUser(user);
            }
        }
        catch (Exception e) {}
        finally {
            userAdapter.close();
        }
    }

    protected void tearDown() {}

    public void testReviewAdapter() {
        ReviewAdapter reviewData = new ReviewAdapter(getContext());
        List<Review> reviewList;

        Review review = new Review();

        review.setId(testId);
        review.setRestaurant_id(testRestaurantId);
        review.setUser_id(testUserId);
        review.setComment(testComment);
        review.setStar(testStar);

        reviewData.open();
        reviewData.insertReview(review);
        reviewList = reviewData.getAllReviews();
        assertEquals(reviewList.size(), 1);

        assertEquals(reviewList.get(0).getId(), testId);
        assertEquals(reviewList.get(0).getUser_id(), testUserId);
        assertEquals(reviewList.get(0).getRestaurant_id(), testRestaurantId);
        assertEquals(reviewList.get(0).getComment(), testComment);
        assertEquals(reviewList.get(0).getStar(), testStar);

        List<String> reviewStringList = reviewData.getAllReviewsAsStrings();
        assertEquals(reviewStringList.size(), 1);
        assertEquals(reviewStringList.get(0), review.toString());

        review.setId(testId + 1);
        reviewData.insertReview(review);
        reviewList = reviewData.getAllReviews();
        assertEquals(reviewList.size(), 2);
        assertEquals(reviewList.get(1).getId(), testId + 1);

        reviewData.deleteReview(review);
        reviewList = reviewData.getAllReviews();
        assertEquals(reviewList.size(), 1);
        reviewData.close();
    }

    public void testRestaurantAdapter() {
        RestaurantAdapter restaurantData = new RestaurantAdapter(getContext());
        List<Restaurant> restaurantList;

        Restaurant restaurant = new Restaurant();

        restaurant.setId(testId);
        restaurant.setName(testName);
        restaurant.setWebsite(testWebsite);

        restaurantData.open();
        restaurantData.insertRestaurant(restaurant);
        restaurantList = restaurantData.getAllRestaurants();
        assertEquals(restaurantList.size(), 1);

        assertEquals(restaurantList.get(0).getId(), testId);
        assertEquals(restaurantList.get(0).getName(), testName);
        assertEquals(restaurantList.get(0).getWebsite(), testWebsite);

        List<String> restaurantStringList = restaurantData.getAllRestaurantsAsStrings();
        assertEquals(restaurantStringList.size(), 1);
        assertEquals(restaurantStringList.get(0), restaurant.toString());

        restaurant.setId(testId + 1);
        restaurantData.insertRestaurant(restaurant);
        restaurantList = restaurantData.getAllRestaurants();
        assertEquals(restaurantList.size(), 2);
        assertEquals(restaurantList.get(1).getId(), 2);

        restaurantData.deleteRestaurant(restaurant);
        restaurantList = restaurantData.getAllRestaurants();
        assertEquals(restaurantList.size(), 1);

        restaurantData.close();
    }

    public void testUserAdapter() {
        UserAdapter userData = new UserAdapter(getContext());
        List<User> userList;

        User user = new User();

        user.setId(testId);
        user.setName(testName);

        userData.open();
        userData.insertUser(user);
        userList = userData.getAllUsers();
        assertEquals(userList.size(), 1);

        assertEquals(userList.get(0).getId(), testId);
        assertEquals(userList.get(0).getName(), testName);

        List<String> userStringList = userData.getAllUsersAsStrings();
        assertEquals(userStringList.size(), 1);
        assertEquals(userStringList.get(0), user.toString());

        user.setId(testId + 1);
        userData.insertUser(user);
        userList = userData.getAllUsers();
        assertEquals(userList.size(), 2);
        assertEquals(userList.get(1).getId(), 2);

        userData.deleteUser(user);
        userList = userData.getAllUsers();
        assertEquals(userList.size(), 1);

        userData.close();
    }
}
