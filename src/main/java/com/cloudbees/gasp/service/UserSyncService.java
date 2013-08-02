package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.User;
import com.cloudbees.gasp.model.UserAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by markprichard on 8/1/13.
 */
public class UserSyncService extends IntentService implements IRestListener {
    private static final String TAG = UserSyncService.class.getName();

    private Uri mGaspUsersUri;

    private void getGaspUsersUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_restaurants_uri", "");

        this.mGaspUsersUri = Uri.parse(gaspReviewsUri);
    }

    public Uri getGaspUsersUri() {
        return mGaspUsersUri;
    }

    public UserSyncService() {
        super(UserSyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String msg = intent.getStringExtra(SyncIntentParams.PARAM_IN_MSG);

        getGaspUsersUriSharedPreferences();
        Log.i(TAG, "Using Gasp Server Restaurants URI: " + getGaspUsersUri());

        AsyncRestClient asyncRestCall = new AsyncRestClient(getGaspUsersUri(), this);
        asyncRestCall.getAll();
    }

    @Override
    public void onCompleted(String results){
        Log.i(TAG, "Response from " + mGaspUsersUri.toString() + " :" + results + '\n');

        if (results!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<List<User>>() {}.getType();
                List<User> users = gson.fromJson(results, type);

                UserAdapter userDB = new UserAdapter(getApplicationContext());
                userDB.open();
                ListIterator<User> iterator = users.listIterator();
                int index = 0;
                while (iterator.hasNext()) {
                    User user = iterator.next();
                    userDB.insertUser(user);
                    index = user.getId();
                }
                userDB.close();

                String resultTxt = "Loaded " + index + " users from " + mGaspUsersUri;
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
