package com.cloudbees.gasp.model;

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

/**
 * Created by markprichard on 7/24/13.
 */
public class AsyncRestTask {
    private static final String TAG = AsyncRestTask.class.getName();
    Uri mUri;
    IRestListener mListener;

    public AsyncRestTask(Uri uri, IRestListener listener) {
        this.mUri = uri;
        this.mListener = listener;
    }

    public void doRest() {
        new ReviewsRESTQuery().execute();
    }

    private class ReviewsRESTQuery extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            ResponseHandler<String> handler = new BasicResponseHandler();
            HttpGet httpGet = new HttpGet(mUri.toString());
            String responseBody = null;

            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);
                responseBody = handler.handleResponse(response);

                Log.d(TAG, responseBody);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String results) {
            mListener.callCompleted(results);
        }
    }
}
