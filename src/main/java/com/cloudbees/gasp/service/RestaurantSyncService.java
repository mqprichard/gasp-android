package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.AsyncRestClient;
import com.cloudbees.gasp.model.IRestListener;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by markprichard on 8/1/13.
 */
public class RestaurantSyncService extends IntentService implements IRestListener {
    private static final String TAG = RestaurantSyncService.class.getName();

    private Uri mGaspRestaurantsUri;

    private void getGaspRestaurantsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_restaurants_uri", "");

        this.mGaspRestaurantsUri = Uri.parse(gaspReviewsUri);
    }

    public Uri getGaspRestaurantsUri() {
        return mGaspRestaurantsUri;
    }

    public RestaurantSyncService() {
        super(RestaurantSyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msg = intent.getStringExtra(SyncIntentParams.PARAM_IN_MSG);

        getGaspRestaurantsUriSharedPreferences();
        Log.i(TAG, "Using Gasp Server Restaurants URI: " + getGaspRestaurantsUri());

        AsyncRestClient asyncRestCall = new AsyncRestClient(getGaspRestaurantsUri(), this);
        asyncRestCall.getAll();
    }

    @Override
    public void onCompleted(String results){
        Log.i(TAG, "Response from " + mGaspRestaurantsUri.toString() + " :" + results + '\n');

        if (results!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<Restaurant>>() {}.getType();
                List<Restaurant> restaurants = gson.fromJson(results, type);

                RestaurantAdapter restaurantsDB = new RestaurantAdapter(getApplicationContext());
                restaurantsDB.open();
                ListIterator<Restaurant> iterator = restaurants.listIterator();
                int index = 0;
                while (iterator.hasNext()) {
                    Restaurant restaurant = iterator.next();
                    restaurantsDB.insertRestaurant(restaurant);
                    index = restaurant.getId();
                }
                restaurantsDB.close();

                String resultTxt = "Loaded " + index + " restaurants from " + mGaspRestaurantsUri;
                Log.i(TAG, resultTxt + '\n');

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(SyncIntentParams.PARAM_OUT_MSG, resultTxt);
                sendBroadcast(broadcastIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
