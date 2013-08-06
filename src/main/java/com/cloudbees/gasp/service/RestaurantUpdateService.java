package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.Restaurant;
import com.cloudbees.gasp.model.RestaurantAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by markprichard on 8/1/13.
 */
public class RestaurantUpdateService extends IntentService implements IRestListener {
    private static final String TAG = RestaurantUpdateService.class.getName();

    private Uri mGaspRestaurantsUri;
    private Restaurant mRestaurant;

    private void getGaspRestaurantsUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_restaurants_uri", "");

        this.mGaspRestaurantsUri = Uri.parse(gaspReviewsUri);
    }

    public Uri getGaspRestaurantsUri() {
        return mGaspRestaurantsUri;
    }

    public RestaurantUpdateService() {
        super("RestaurantUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int index = intent.getIntExtra(SyncIntentParams.PARAM_ID, 0);

        if (index == 0) {
            Log.d(TAG, "Error - invalid index");
        }
        else {
            getGaspRestaurantsUriSharedPreferences();

            AsyncRestClient asyncRestCall = new AsyncRestClient(mGaspRestaurantsUri, this);
            asyncRestCall.getIndex(index);
        }
    }

    @Override
    public void onCompleted(String result){
        Log.i(TAG, "Response:" + result + '\n');

        if (result!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Restaurant>() {}.getType();
                mRestaurant = gson.fromJson(result, type);

                RestaurantAdapter restaurantsDB = new RestaurantAdapter(getApplicationContext());
                restaurantsDB.open();
                restaurantsDB.insertRestaurant(mRestaurant);
                restaurantsDB.close();

                String resultTxt = "Loaded restaurant: " + mRestaurant.getId();
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
