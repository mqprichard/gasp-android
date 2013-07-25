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
public class AsyncRestClient {
    private static final String TAG = AsyncRestClient.class.getName();

    private Uri mBaseUri;
    private IRestListener2 mListener;

    public AsyncRestClient(Uri baseUri, IRestListener2 listener) {
        this.mBaseUri = baseUri;
        this.mListener = listener;
    }

    public void getAll() {
        AsyncRestCall restCall = new AsyncRestCall() {
            @Override
            protected void onPostExecute(String results) {
                mListener.onCompletedAll(results);
            }
        };

        restCall.setRestUri(mBaseUri);
        restCall.execute();
    }

    public void getIndex(int index) {
        AsyncRestCall restCall = new AsyncRestCall() {
            @Override
            protected void onPostExecute(String results) {
                mListener.onCompletedIndex(results);
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

                Log.d(TAG, responseBody);
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
