package com.cloudbees.gasp.twitter;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

public class TwitterAuthentication {
    private static final String TAG = TwitterAuthentication.class.getName();

    private static final String twitterApiOAuthToken = "https://api.twitter.com/oauth2/token";

    public static String getTwitterApiOAuthToken() {
        return twitterApiOAuthToken;
    }

    private static final String consumerKey = "VtBCY5oJxMteTAd6o9IpA";
    private static final String consumerSecret = "WN6Al8vAzIZDwfZmfZfGJrAFiMDjNHky9qfarMaPePY";
    private static final String charSet = "UTF-8";

    public static String getEncodedBase64Credentials() {
        String authEncodedBase64 = "";
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
}
