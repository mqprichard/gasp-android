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

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.cloudbees.gasp.service.RESTIntentService;

/**
 * Closely modeled on Neil Goodman's Android REST tutorials
 * https://github.com/posco2k8/rest_service_tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 *
 * @author Mark Prichard
 */
public abstract class RESTResponderFragment extends Fragment {
    
    private final ResultReceiver mReceiver;

    public RESTResponderFragment() {
        mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(RESTIntentService.REST_RESULT)) {
                    onRESTResult(resultCode, resultData.getString(RESTIntentService.REST_RESULT));
                }
                else {
                    onRESTResult(resultCode, null);
                }
            }
            
        };
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep this instance if Activity is re-created
        setRetainInstance(true);
    }
    
    public ResultReceiver getResultReceiver() {
        return mReceiver;
    }

    abstract public void onRESTResult(int code, String result);
}
