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
public class RestaurantUpdateServiceTest extends ServiceTestCase<RestaurantUpdateService> {
    private static final String TAG = RestaurantUpdateServiceTest.class.getName();

    RestaurantAdapter restaurantAdapter;
    CountDownLatch signal;

    public RestaurantUpdateServiceTest() {
        super(RestaurantUpdateService.class);
        signal = new CountDownLatch(1);
    }

    private void cleanDatabase() {
        RestaurantAdapter restaurantData = new RestaurantAdapter(getContext());
        restaurantData.open();
        try {
            List<Restaurant> restaurantList = restaurantData.getAll();
            for (Restaurant restaurant : restaurantList) {
                restaurantData.deleteRestaurant(restaurant);
            }
        }
        catch(Exception e){}
        finally {
            restaurantData.close();
        }
    }

    protected void setUp() {
        cleanDatabase();
    }

    public void testRestaurantUpdateIntent () throws InterruptedException {
        startService(new Intent(getContext(), RestaurantUpdateService.class)
                .putExtra(SyncIntentParams.PARAM_ID, 1));

        // Allow 10 secs for the async REST call to complete
        signal.await(10, TimeUnit.SECONDS);

        try {
            restaurantAdapter = new RestaurantAdapter(getContext());
            restaurantAdapter.open();

            List<Restaurant> restaurants = restaurantAdapter.getAll();
            assertTrue(restaurants.size() > 0);
        }
        finally {
            restaurantAdapter.close();
        }
    }

    protected void tearDown() {
        signal.countDown();

        cleanDatabase();
    }
}
