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
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cloudbees.gasp.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class mapping the Gasp database to com.cloudbees.gasp.model.Review
 * Implements insert, cursor and list methods: main interface to Review data
 */
public class ReviewAdapter {
    private static final String TAG = ReviewAdapter.class.getName();

    private SQLiteDatabase database;
    private final GaspSQLiteHelper dbHelper;

    private final String[] allColumns = {GaspSQLiteHelper.REVIEWS_COLUMN_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID,
            GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT,
            GaspSQLiteHelper.REVIEWS_COLUMN_STAR};

    public ReviewAdapter(Context context) {
        dbHelper = new GaspSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertReview(Review review) {
        try {
            ContentValues values = new ContentValues();
            values.put(GaspSQLiteHelper.REVIEWS_COLUMN_ID, review.getId());
            values.put(GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID, review.getRestaurant_id());
            values.put(GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID, review.getUser_id());
            values.put(GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT, review.getComment());
            values.put(GaspSQLiteHelper.REVIEWS_COLUMN_STAR, review.getStar());
            long insertId = database.insertOrThrow(GaspSQLiteHelper.REVIEWS_TABLE, null, values);
            if (insertId != -1) {
                Log.d(TAG, "Inserted review with id: " + insertId);
            } else {
                Log.e(TAG, "Error inserting review with id: " + review.getId());
            }
        } catch (SQLiteConstraintException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteReview(Review Review) {
        long id = Review.getId();
        Log.d(TAG, "Deleting review with id: " + id);
        database.delete(GaspSQLiteHelper.REVIEWS_TABLE, GaspSQLiteHelper.REVIEWS_COLUMN_ID
                + " = " + id, null);
    }

    public List<Review> getAll() {
        List<Review> reviews = new ArrayList<Review>();

        Cursor cursor = database.query(GaspSQLiteHelper.REVIEWS_TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = cursorToReview(cursor);
            reviews.add(review);
            cursor.moveToNext();
        }
        cursor.close();
        return reviews;
    }

    public List<Review> getAllByRestaurant(int id) {
        List<Review> reviews = new ArrayList<Review>();

        Cursor cursor = database.query(GaspSQLiteHelper.REVIEWS_TABLE, allColumns,
                GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = cursorToReview(cursor);
            reviews.add(review);
            cursor.moveToNext();
        }
        cursor.close();
        return reviews;
    }

    public long getLastId() {
        final String query = "SELECT " + GaspSQLiteHelper.REVIEWS_COLUMN_ID
                + " from " + GaspSQLiteHelper.REVIEWS_TABLE
                + " order by " + GaspSQLiteHelper.REVIEWS_COLUMN_ID
                + " DESC limit 1";
        long lastId = 0;

        try {
            Cursor cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                lastId = cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lastId;
    }

    private Review cursorToReview(Cursor cursor) {
        Review Review = new Review();
        Review.setId(cursor.getInt(0));
        Review.setRestaurant_id(cursor.getInt(1));
        Review.setUser_id(cursor.getInt(2));
        Review.setComment(cursor.getString(3));
        Review.setStar(cursor.getInt(4));
        return Review;
    }
}
