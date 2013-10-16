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
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Closely modeled on Neil Goodman's Android REST tutorials
 * https://github.com/posco2k8/rest_service_tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 *
 * @author Mark Prichard
 */
public class RESTIntentService extends IntentService {
    private static final String TAG = RESTIntentService.class.getName();
    
    public static final int GET    = 0x1;
    public static final int POST   = 0x2;
    public static final int PUT    = 0x3;
    public static final int DELETE = 0x4;

    public static final String EXTRA_HTTP_VERB       = "com.cloudbees.gasp.EXTRA_HTTP_VERB";
    public static final String EXTRA_PARAMS          = "com.cloudbees.gasp.EXTRA_PARAMS";
    public static final String EXTRA_HEADERS         = "com.cloudbees.gasp.EXTRA_HEADERS";
    public static final String EXTRA_RESULT_RECEIVER = "com.cloudbees.gasp.EXTRA_RESULT_RECEIVER";
    public static final String REST_RESULT           = "com.cloudbees.gasp.REST_RESULT";

    public RESTIntentService() {
        super(TAG);
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        Uri    action = intent.getData();
        Bundle extras = intent.getExtras();
        
        if (extras == null || action == null || !extras.containsKey(EXTRA_RESULT_RECEIVER)) {
            Log.e(TAG, "You did not pass extras or data with the Intent.");
            return;
        }

        int verb = extras.getInt(EXTRA_HTTP_VERB, GET);
        Bundle params = extras.getParcelable(EXTRA_PARAMS);
        Bundle headers = extras.getParcelable(EXTRA_HEADERS);
        ResultReceiver receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);

        try {
            HttpRequestBase request = null;

            // Get query params from Bundle and build URL
            switch (verb) {
                case GET: {
                    request = new HttpGet();
                    attachUriWithQuery(request, action, params);
                }
                break;
                
                case DELETE: {
                    request = new HttpDelete();
                    attachUriWithQuery(request, action, params);
                }
                break;
                
                case POST: {
                    request = new HttpPost();
                    request.setURI(new URI(action.toString()));

                    HttpPost postRequest = (HttpPost) request;
                    
                    if (params != null) {
                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                        postRequest.setEntity(formEntity);
                    }
                }
                break;
                
                case PUT: {
                    request = new HttpPut();
                    request.setURI(new URI(action.toString()));

                    HttpPut putRequest = (HttpPut) request;
                    
                    if (params != null) {
                        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(paramsToList(params));
                        putRequest.setEntity(formEntity);
                    }
                }
                break;
            }

            // Get Headers from Bundle
            for (BasicNameValuePair header: paramsToList(headers)){
                request.setHeader(header.getName(), header.getValue());
            }
            
            if (request != null) {
                HttpClient client = new DefaultHttpClient();

                Log.d(TAG, "Executing request: "+ verbToString(verb) +": "+ action.toString());
                
                HttpResponse response = client.execute(request);
                
                HttpEntity responseEntity = response.getEntity();
                StatusLine responseStatus = response.getStatusLine();
                int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;
                
                if ((responseEntity != null) && (responseStatus.getStatusCode() == 200)) {
                    Bundle resultData = new Bundle();
                    resultData.putString(REST_RESULT, EntityUtils.toString(responseEntity));
                    receiver.send(statusCode, resultData);
                }
                else {
                    receiver.send(statusCode, null);
                }
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect. "+ verbToString(verb) +": "+ action.toString(), e);
            receiver.send(0, null);
        }
        catch (UnsupportedEncodingException e) {
            Log.e(TAG, "A UrlEncodedFormEntity was created with an unsupported encoding.", e);
            receiver.send(0, null);
        }
        catch (ClientProtocolException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            receiver.send(0, null);
        }
        catch (IOException e) {
            Log.e(TAG, "There was a problem when sending the request.", e);
            receiver.send(0, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void attachUriWithQuery(HttpRequestBase request, Uri uri, Bundle params) {
        try {
            if (params == null) {
                request.setURI(new URI(uri.toString()));
            }
            else {
                Uri.Builder uriBuilder = uri.buildUpon();
                
                // Loop through our params and append them to the Uri.
                for (BasicNameValuePair param : paramsToList(params)) {
                    uriBuilder.appendQueryParameter(param.getName(), param.getValue());
                }
                
                uri = uriBuilder.build();
                request.setURI(new URI(uri.toString()));
            }
        }
        catch (URISyntaxException e) {
            Log.e(TAG, "URI syntax was incorrect: "+ uri.toString(), e);
        }
    }
    
    private static String verbToString(int verb) {
        switch (verb) {
            case GET:
                return "GET";
                
            case POST:
                return "POST";
                
            case PUT:
                return "PUT";
                
            case DELETE:
                return "DELETE";
        }
        
        return "";
    }
    
    private static List<BasicNameValuePair> paramsToList(Bundle params) {
        ArrayList<BasicNameValuePair> formList = new ArrayList<BasicNameValuePair>(params.size());
        
        for (String key : params.keySet()) {
            Object value = params.get(key);

            if (value != null) formList.add(new BasicNameValuePair(key, value.toString()));
        }
        
        return formList;
    }

}
