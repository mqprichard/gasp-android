package com.cloudbees.demo.gasp.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.model.Review;
import com.cloudbees.demo.gasp.utils.Preferences;

import java.net.MalformedURLException;
import java.net.URL;

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

public class ReviewDialogFragment extends DialogFragment {
    private static final String TAG = ReviewDialogFragment.class.getName();

    public ReviewDialogFragment() {
    }

    public static ReviewDialogFragment newInstance(String title, Review review) {
        ReviewDialogFragment frag = new ReviewDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("review", review);
        frag.setArguments(args);
        return frag;
    }

    private void setTextView(View rootView, int id, String text) {
        if (text != null) {
            TextView textView = (TextView) rootView.findViewById(id);
            textView.setText(text);
        }
    }

    private void addViews(View view, Review review) {
        try {
            String baseUrl = Preferences.getGaspServerUrl().replaceAll("/$", "");
            URL restaurantUrl = new URL(baseUrl + review.getRestaurant());
            URL reviewUrl = new URL(baseUrl + review.getUrl());
            URL userUrl = new URL(baseUrl + review.getUser());

            Log.d(TAG, "Review: " + reviewUrl);
            Log.d(TAG, "Restaurant: " + restaurantUrl);
            Log.d(TAG, "User: " + userUrl);

            setTextView(view, R.id.review_dialog_url, "Gasp! Review: " + reviewUrl.toString());
            setTextView(view, R.id.review_dialog_restaurant, "Gasp! Restaurant: " + restaurantUrl.toString());
            setTextView(view, R.id.review_dialog_user, "Gasp! User: " + userUrl.toString());
            setTextView(view, R.id.review_dialog_comment, "Comment: " + review.getComment());
            setTextView(view, R.id.review_dialog_star, "Stars: " + String.valueOf(review.getStar()));
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addButton(View view) {
        Button button = (Button) view.findViewById(R.id.review_dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gasp_review_dialog, container);

        String title = getArguments().getString("title");
        Review review = (Review) getArguments().getSerializable("review");
        addViews(view, review);
        addButton(view);
        getDialog().setTitle(title);

        return view;
    }
}
