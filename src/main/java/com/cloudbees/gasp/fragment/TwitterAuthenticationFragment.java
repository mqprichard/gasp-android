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

package com.cloudbees.gasp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.activity.MainActivity;
import com.cloudbees.gasp.activity.TwitterStreamActivity;
import com.cloudbees.gasp.model.TwitterTokenResponse;
import com.cloudbees.gasp.service.RESTIntentService;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Closely modeled on Neil Goodman's Android REST tutorials
 * https://github.com/posco2k8/rest_service_tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 *
 * @author Mark Prichard
 */
public class TwitterAuthenticationFragment extends RESTResponderFragment {
    private static final String TAG = TwitterAuthenticationFragment.class.getName();

    private static final String twitterApiOAuthToken = "https://api.twitter.com/oauth2/token";

    public String getTwitterApiOAuthToken() {
        return twitterApiOAuthToken;
    }

    private String authEncodedBase64 = "";

    private static final String consumerKey = "VtBCY5oJxMteTAd6o9IpA";
    private static final String consumerSecret = "WN6Al8vAzIZDwfZmfZfGJrAFiMDjNHky9qfarMaPePY";
    private static final String charSet = "UTF-8";

    public String getEncodedBase64Credentials() {
        try {
            String urlEncoded = URLEncoder.encode(consumerKey, charSet)
                    + ":"
                    + URLEncoder.encode(consumerSecret, charSet);
            byte[] urlEncodedBytes = urlEncoded.getBytes(charSet);
            authEncodedBase64 = "Basic "
                    + Base64.encodeToString(urlEncodedBytes, Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Check URL encoding", e);
        }
        return authEncodedBase64;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        requestOAuthToken();
    }

    private void requestOAuthToken() {
        try {
            MainActivity activity = (MainActivity) getActivity();

            Intent intent = new Intent(activity, RESTIntentService.class);
            intent.setData(Uri.parse(twitterApiOAuthToken));

            Bundle params = new Bundle();
            params.putString("grant_type", "client_credentials");

            Bundle headers = new Bundle();
            headers.putString("Authorization", getEncodedBase64Credentials());

            intent.putExtra(RESTIntentService.EXTRA_HTTP_VERB, RESTIntentService.POST);
            intent.putExtra(RESTIntentService.EXTRA_HEADERS, headers);
            intent.putExtra(RESTIntentService.EXTRA_PARAMS, params);
            intent.putExtra(RESTIntentService.EXTRA_RESULT_RECEIVER, getResultReceiver());

            activity.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRESTResult(int code, String result) {
        if (code == 200 && result != null) {
            TwitterTokenResponse twitterToken = new Gson().fromJson(result, TwitterTokenResponse.class);
            TwitterStreamActivity.setTwitterOAuthToken(twitterToken.getAccess_token());
            Log.d(TAG, "Twitter OAuth Access Token: " + twitterToken.getAccess_token());
            Log.d(TAG, "Twitter OAuth Token Type: " + twitterToken.getToken_type());
        } else {
            Activity activity = getActivity();
            if (activity != null) {
                Toast.makeText(activity,
                        getResources().getString(R.string.gasp_twitter_auth_error),
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
