package com.cloudbees.demo.gasp.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cloudbees.demo.gasp.adapter.ReviewDataAdapter;
import com.cloudbees.demo.gasp.model.Review;

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

public class ReviewsFragment extends ListFragment {
    private static final String TAG = ReviewsFragment.class.getName();

    public ReviewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ReviewDataAdapter reviewAdapter = new ReviewDataAdapter(inflater.getContext());
        reviewAdapter.open();

        // Get all reviews in descending order
        List<Review> reviews = reviewAdapter.getAllDesc();
        reviewAdapter.open();
        ArrayAdapter<Review> adapter =
                new ArrayAdapter<Review>(inflater.getContext(),
                                         android.R.layout.simple_list_item_1,
                                         reviews);
        reviewAdapter.close();

        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
