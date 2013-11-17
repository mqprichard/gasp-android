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

package com.cloudbees.gasp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.cloudbees.gasp.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class mapping the Gasp database to com.cloudbees.gasp.model.Review
 * Implements insert, cursor and list methods: main interface to Review data
 */
public class ReviewDataAdapter extends GaspDataAdapter<Review> {
    private static final String TAG = ReviewDataAdapter.class.getName();

    private static final String[] allColumns = {
            GaspSQLiteHelper.REVIEWS_COLUMN_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT,
            GaspSQLiteHelper.REVIEWS_COLUMN_STAR
    };

    private static final String idColumnName = GaspSQLiteHelper.REVIEWS_COLUMN_ID;
    private static final String tableName = GaspSQLiteHelper.REVIEWS_TABLE;

    public ReviewDataAdapter(Context context) {
        super(context);
    }

    @Override
    protected String getIdColumnName() {
        return idColumnName;
    }

    @Override
    protected String getTableName() {
        return tableName;
    }

    @Override
    protected String[] getAllColumns() {
        return allColumns;
    }

    @Override
    protected void putValues(ContentValues values, Review review) {
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_ID, review.getId());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID, review.getRestaurant_id());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID, review.getUser_id());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT, review.getComment());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_STAR, review.getStar());
    }

    @Override
    protected Review fromCursor(Cursor cursor) {
        Review Review = new Review();
        Review.setId(cursor.getInt(0));
        Review.setRestaurant_id(cursor.getInt(1));
        Review.setUser_id(cursor.getInt(2));
        Review.setComment(cursor.getString(3));
        Review.setStar(cursor.getInt(4));
        return Review;
    }

    /**
     * Find all reviews matching a given restaurant id
     *
     * @param id Restaurant id
     * @return ArrayList of reviews
     */
    public List<Review> getAllByRestaurant(int id) {
        List<Review> reviews = new ArrayList<Review>();

        Cursor cursor = database.query(GaspSQLiteHelper.REVIEWS_TABLE, allColumns,
                GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        return listFromCursor(cursor);
    }

}
