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

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Random;

class AsyncRESTClient {
    private static final String TAG = AsyncRESTClient.class.getName();

    private final Uri mBaseUri;
    private final IRESTListener mListener;

    public AsyncRESTClient(Uri baseUri, IRESTListener listener) {
        this.mBaseUri = baseUri;
        this.mListener = listener;
    }

    public void getAll() {
        AsyncRestCall restCall = new AsyncRestCall() {
            @Override
            protected void onPostExecute(String results) {
                mListener.onCompleted(results);
            }
        };

        restCall.setRestUri(mBaseUri);
        restCall.execute();
    }

    public void getIndex(int index) {
        AsyncRestCall restCall = new AsyncRestCall() {
            @Override
            protected void onPostExecute(String results) {
                mListener.onCompleted(results);
            }
        };

        restCall.setRestUri(Uri.withAppendedPath(mBaseUri, String.valueOf(index)));
        restCall.execute();
    }

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    private abstract class AsyncRestCall extends AsyncTask<Void, Void, String> {
        private Uri mRestUri;

        private void setRestUri(Uri restUri) {
            this.mRestUri = restUri;
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            ResponseHandler<String> handler = new BasicResponseHandler();
            HttpGet httpGet = new HttpGet(mRestUri.toString());
            String responseBody = null;

            long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

            for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                Log.d(TAG, "Attempt #" + i + " to connect to " + mBaseUri);

                try {
                    HttpResponse response = httpClient.execute(httpGet, localContext);
                    responseBody = handler.handleResponse(response);
                    break;

                } catch (IOException e) {
                    Log.e(TAG, "Failed to connect to " + mBaseUri + " on attempt " + i, e);
                    if (i == MAX_ATTEMPTS) {
                        break;
                    }
                    try {
                        Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            // Activity finished before we complete - exit.
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                            break;
                        }
                        // increase backoff exponentially
                        backoff *= 2;
                    }
                }
            return responseBody;
        }

        @Override
        abstract protected void onPostExecute(String results);
    }
}
