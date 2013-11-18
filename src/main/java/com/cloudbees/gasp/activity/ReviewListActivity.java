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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cloudbees.gasp.R;
import com.cloudbees.gasp.adapter.ReviewArrayAdapter;
import com.cloudbees.gasp.adapter.ReviewDataAdapter;
import com.cloudbees.gasp.model.Review;

import java.util.List;

public class ReviewListActivity extends ListActivity {
    private ReviewDataAdapter reviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        reviewAdapter = new ReviewDataAdapter(this);
        reviewAdapter.open();

        // Get all reviews in descending order
        List<Review> reviews = reviewAdapter.getAllDesc();

        ReviewArrayAdapter reviewArrayAdapter = new ReviewArrayAdapter(this, R.layout.gasp_review_list, reviews);
        setListAdapter(reviewArrayAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_short, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.gasp_settings:
                Intent intent = new Intent();
                intent.setClass(this, SetPreferencesActivity.class);
                startActivityForResult(intent, 0);
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}