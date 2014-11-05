package com.appdynamics.demo.gasp.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.appdynamics.demo.gasp.adapter.UserDataAdapter;
import com.appdynamics.demo.gasp.model.User;

import java.util.List;

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

public class UsersFragment extends ListFragment {

    public UsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UserDataAdapter userDataAdapter = new UserDataAdapter(inflater.getContext());
        userDataAdapter.open();

        // Get all users in descending order
        List<User> users = userDataAdapter.getAllDesc();
        userDataAdapter.close();
        ArrayAdapter<User> adapter =
                new ArrayAdapter<User>(inflater.getContext(),
                                       android.R.layout.simple_list_item_1,
                                       users);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
