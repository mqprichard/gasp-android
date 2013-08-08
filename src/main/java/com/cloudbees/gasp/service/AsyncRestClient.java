package com.cloudbees.gasp.service;

import android.net.Uri;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class AsyncRestClient {
    private static final String TAG = AsyncRestClient.class.getName();

    private final Uri mBaseUri;
    private final IRestListener mListener;

    public AsyncRestClient(Uri baseUri, IRestListener listener) {
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

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                responseBody = handler.handleResponse(response);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        abstract protected void onPostExecute(String results);
    }
}
