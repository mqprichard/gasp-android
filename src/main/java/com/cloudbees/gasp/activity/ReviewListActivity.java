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

package com.cloudbees.gasp.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;

import java.util.Collections;
import java.util.List;

public class ReviewListActivity extends ListActivity {
    private ReviewAdapter reviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasp_data_layout);

        reviewAdapter = new ReviewAdapter(this);
        reviewAdapter.open();

        List<Review> reviews = reviewAdapter.getAll();
        Collections.reverse(reviews);

        ArrayAdapter<Review> adapter = new ArrayAdapter<Review>(this,
                android.R.layout.simple_list_item_1, reviews);
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {
        reviewAdapter.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        reviewAdapter.close();
        super.onPause();
    }
}