package com.appdynamics.demo.gasp.fragment;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.appdynamics.demo.gasp.R;
import com.appdynamics.demo.gasp.model.PlaceDetail;
import com.appdynamics.demo.gasp.model.PlaceEvent;

import java.util.ArrayList;

/**
 * Copyright (c) 2013 Mark Prichard
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

public class EventDetailsFragment extends ListFragment {
    private static final String TAG = EventDetailsFragment.class.getName();

    public EventDetailsFragment() {
    }

    public void showEventDetails(PlaceDetail placeDetail){
        // Use a simple TextView layout for ArrayAdapter constructor
        ArrayAdapter<String> mEventAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gasp_generic_textview, new ArrayList<String>());
        setListAdapter(mEventAdapter);

        if (placeDetail.getEvents() != null) {
            for (PlaceEvent event : placeDetail.getEvents()) {
                Log.d(TAG, "Event Id: " + event.getEvent_id());
                Log.d(TAG, "Event Summary: " + event.getSummary());
                mEventAdapter.add(event.getSummary() + ": " + event.getUrl());
            }
        }
    }
}
