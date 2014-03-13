package com.cloudbees.demo.gasp.fragment;

import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import com.cloudbees.demo.gasp.R;
import com.cloudbees.demo.gasp.model.Review;

import java.util.ArrayList;
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

public class ReviewDetailsFragment extends ListFragment {
    private static final String TAG = ReviewDetailsFragment.class.getName();

    public ReviewDetailsFragment() {
    }

    public void showReviewDetails(List<Review> reviews) {
        // Use a simple TextView layout for ArrayAdapter constructor
        ArrayAdapter<String> mReviewAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.gasp_generic_textview, new ArrayList<String>());
        setListAdapter(mReviewAdapter);
        for (Review review : reviews) {
            mReviewAdapter.add(review.toString());
        }
    }
}
