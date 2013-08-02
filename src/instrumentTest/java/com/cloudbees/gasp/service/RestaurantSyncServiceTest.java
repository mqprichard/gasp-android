package com.cloudbees.gasp.service;

import android.content.Intent;
import android.test.ServiceTestCase;

import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by markprichard on 8/2/13.
 */
public class RestaurantSyncServiceTest extends ServiceTestCase<RestaurantSyncService> {
    private static final String TAG = RestaurantSyncServiceTest.class.getName();
    CountDownLatch signal;

    public RestaurantSyncServiceTest() {
        super(RestaurantSyncService.class);
        signal = new CountDownLatch(1);
    }

    protected void setUp() {
        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getContext());
        restaurantAdapter.open();
        try {
            List<Restaurant> restaurantList = restaurantAdapter.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantAdapter.deleteRestaurant(restaurant);
            }
        }
        catch (Exception e) {}
        finally {
            restaurantAdapter.close();
        }
    }

    public void testRestaurantSyncIntent () throws InterruptedException {
        startService(new Intent(getContext(), RestaurantSyncService.class));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getContext());
        restaurantAdapter.open();

        List<Restaurant> restaurantList = restaurantAdapter.getAll();
        assertTrue(restaurantList.size() > 0);
    }

    protected void tearDown() {
        signal.countDown();

        RestaurantAdapter restaurantAdapter = new RestaurantAdapter(getContext());
        restaurantAdapter.open();
        try {
            List<Restaurant> restaurantList = restaurantAdapter.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantAdapter.deleteRestaurant(restaurant);
            }
        }
        catch (Exception e) {}
        finally {
            restaurantAdapter.close();
        }
    }
}
