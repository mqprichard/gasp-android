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

import com.cloudbees.gasp.service.RESTService;

/**
 * Closely modeled on Neil Goodman's Android REST tutorials
 * https://github.com/posco2k8/rest_service_tutorial
 * https://github.com/posco2k8/rest_loader_tutorial.git
 *
 * @author Mark Prichard
 */
public abstract class RESTResponderFragment extends Fragment {
    
    private final ResultReceiver mReceiver;
    
    // We are going to use a constructor here to make our ResultReceiver,
    // but be careful because Fragments are required to have only zero-arg
    // constructors. Normally you don't want to use constructors at all
    // with Fragments.
    public RESTResponderFragment() {
        mReceiver = new ResultReceiver(new Handler()) {

            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultData != null && resultData.containsKey(RESTService.REST_RESULT)) {
                    onRESTResult(resultCode, resultData.getString(RESTService.REST_RESULT));
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
        
        // This tells our Activity to keep the same instance of this
        // Fragment when the Activity is re-created during lifecycle
        // events. This is what we want because this Fragment should
        // be available to receive results from our RESTService no
        // matter what the Activity is doing.
        setRetainInstance(true);
    }
    
    public ResultReceiver getResultReceiver() {
        return mReceiver;
    }

    // Implementers of this Fragment will handle the result here.
    abstract public void onRESTResult(int code, String result);
}
