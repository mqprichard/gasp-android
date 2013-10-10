/*
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cloudbees.gasp.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
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

public class UserSyncService extends IntentService implements IRestListener {
    private static final String TAG = UserSyncService.class.getName();

    private Uri mGaspUsersUri;

    private void getGaspUsersUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspReviewsUri = gaspSharedPreferences.getString("gasp_users_uri", "");

        this.mGaspUsersUri = Uri.parse(gaspReviewsUri);
    }

    private Uri getGaspUsersUri() {
        return mGaspUsersUri;
    }

    public UserSyncService() {
        super(UserSyncService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
                    try {
                        User user = iterator.next();
                        userDB.insertUser(user);
                        index = user.getId();
                    } catch (SQLiteConstraintException e) {
                        // Attempting to overwrite existing records will throw an exception
                        // Ignore these as we want to re-sync on startup
                    }
                }
                userDB.close();

                String resultTxt = "Loaded " + index + " users from " + mGaspUsersUri;
                Log.i(TAG, resultTxt + '\n');

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(MainActivity.ResponseReceiver.PARAM_OUT_MSG, resultTxt);
                sendBroadcast(broadcastIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
