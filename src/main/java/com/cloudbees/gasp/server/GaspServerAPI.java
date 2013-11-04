package com.cloudbees.gasp.server;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

public class GaspServerAPI {
    private static final String TAG = GaspServerAPI.class.getName();

    public static String newGaspEntity (String input, URL url) {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        String location = "";

        try {
            Log.d(TAG, "Request URL: " + url.toString());
            Log.d(TAG, "Request Body: " + input);

            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Length", "" +
                    Integer.toString(input.getBytes().length));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
            wr.writeBytes (input);
            wr.flush ();
            wr.close ();
            if (conn.getHeaderField("Location") != null) {
                location = conn.getHeaderField("Location");
                Log.d(TAG, "Location: " + conn.getHeaderField("Location"));
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        }
        catch (MalformedURLException e) {
            Log.e(TAG, "Malformed Gasp Server URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Gasp Server API", e);
        } catch (Exception e) {
            Log.e(TAG, "Exception: ", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return location;
    }
}
