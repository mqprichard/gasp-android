package com.appdynamics.demo.gasp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appdynamics.demo.gasp.R;
import com.appdynamics.demo.gasp.model.Restaurant;
import com.appdynamics.demo.gasp.utils.Preferences;

import java.net.MalformedURLException;
import java.net.URL;

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

public class RestaurantDialogFragment extends DialogFragment {
    private static final String TAG = DialogFragment.class.getName();

    public RestaurantDialogFragment() {
    }

    public static RestaurantDialogFragment newInstance(String title, Restaurant restaurant) {
        RestaurantDialogFragment frag = new RestaurantDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("restaurant", restaurant);
        frag.setArguments(args);
        return frag;
    }

    private void setTextView(View rootView, int id, String text) {
        if (text != null) {
            TextView textView = (TextView) rootView.findViewById(id);
            textView.setText(text);
        }
    }

    private void addViews(View view, Restaurant restaurant) {
        setTextView(view, R.id.restaurant_dialog_name, "Name:" + restaurant.getName());
        setTextView(view, R.id.restaurant_dialog_placesId, "Google Places Id: " + restaurant.getPlacesId());
        if (restaurant.getWebsite() != null) {
            setTextView(view, R.id.restaurant_dialog_website, "Website: " + restaurant.getWebsite());
        }
        try {
            String baseUrl = Preferences.getGaspServerUrl().replaceAll("/$", "");
            URL restaurantUrl = new URL(baseUrl + restaurant.getUrl());
            setTextView(view, R.id.restaurant_dialog_url, "Gasp! Restaurant: " + restaurantUrl);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addButton(View view) {
        Button button = (Button) view.findViewById(R.id.restaurant_dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gasp_restaurant_dialog, container);

        String title = getArguments().getString("title");
        Restaurant restaurant = (Restaurant) getArguments().getSerializable("restaurant");
        addViews(view, restaurant);
        addButton(view);

        getDialog().setTitle(title);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
