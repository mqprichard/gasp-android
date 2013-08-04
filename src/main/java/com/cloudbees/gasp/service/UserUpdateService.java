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

/**
 * Created by markprichard on 8/1/13.
 */
public class UserUpdateService extends IntentService implements IRestListener {
    private static final String TAG = UserUpdateService.class.getName();

    private Uri mGaspUsersUri;
    private User mUser;

    private void getGaspUsersUriSharedPreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences gaspSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String gaspUsersUri = gaspSharedPreferences.getString("gasp_users_uri", "");

        this.mGaspUsersUri = Uri.parse(gaspUsersUri);
    }

    public Uri getGaspUsersUri() {
        return mGaspUsersUri;
    }

    public UserUpdateService() {
        super("UserUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int index = intent.getIntExtra(SyncIntentParams.PARAM_ID, 0);

        if (index == 0) {
            Log.d(TAG, "Error - invalid index");
        }
        else {
            getGaspUsersUriSharedPreferences();

            AsyncRestClient asyncRestCall = new AsyncRestClient(mGaspUsersUri, this);
            asyncRestCall.getIndex(index);
        }
    }

    @Override
    public void onCompleted(String result){
        Log.i(TAG, "Response:" + result + '\n');

        if (result!=null) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<User>() {}.getType();
                mUser = gson.fromJson(result, type);

                UserAdapter usersDB = new UserAdapter(getApplicationContext());
                usersDB.open();
                usersDB.insertUser(mUser);
                usersDB.close();

                String resultTxt = "Loaded user: " + mUser.getId();
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
