package com.cloudbees.gasp.activity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.cloudbees.gasp.gcm.R;
import com.cloudbees.gasp.model.Review;
import com.cloudbees.gasp.model.ReviewAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Created by markprichard on 7/15/13.
 */
public class ReviewListActivity extends ListActivity {
    private ReviewAdapter reviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasp_database_list);

        reviewAdapter = new ReviewAdapter(this);
        reviewAdapter.open();

        List<Review> reviews = reviewAdapter.getAllReviews();
        Collections.reverse(reviews);

        // Use the SimpleCursorAdapter to show the
        // elements in a ListView
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