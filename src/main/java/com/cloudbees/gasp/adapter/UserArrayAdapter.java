package com.cloudbees.gasp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.User;

import java.util.List;

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

public class UserArrayAdapter extends ArrayAdapter<User> {
    private final static String TAG = UserArrayAdapter.class.getName();

    private List<User> mUsers;
    private int mResource;

    public UserArrayAdapter(Context context, int resource, List<User> users) {
        super(context, resource, users);
        this.mUsers = users;
        this.mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        try {
            if (view == null) {
                LayoutInflater inflater
                        = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(mResource, null);
            }
            TextView viewUrl = (TextView) view.findViewById(R.id.user_url);
            TextView viewName = (TextView) view.findViewById(R.id.user_name);

            User user = mUsers.get(position);

            if (user != null) {
                viewUrl.setText("Url: " + user.getUrl());
                viewName.setText("Name: " + user.getName());
            } else {
                Log.e(TAG, "Error: view is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }
}
