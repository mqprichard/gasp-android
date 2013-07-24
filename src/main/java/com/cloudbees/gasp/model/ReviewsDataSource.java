package com.cloudbees.gasp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markprichard on 7/14/13.
 */
public class ReviewsDataSource {
    private static final String TAG = ReviewsDataSource.class.getName();

    // Database fields
    private SQLiteDatabase database;
    private GaspSQLiteHelper dbHelper;
    private String[] allColumns = { GaspSQLiteHelper.REVIEWS_COLUMN_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_STAR };

    public ReviewsDataSource(Context context) {
        dbHelper = new GaspSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_ID, review.getId());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID, review.getRestaurant_id());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID, review.getUser_id());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT, review.getComment());
        values.put(GaspSQLiteHelper.REVIEWS_COLUMN_STAR, review.getStar());
        long insertId = database.insert(GaspSQLiteHelper.REVIEWS_TABLE, null,
                values);
        if (insertId != -1) {
            Log.d(TAG, "Inserted review with id: " + insertId);
        } else {
            Log.e(TAG, "Error inserting review with id: " + review.getId());
        }
    }

    public void deleteReview(Review Review) {
        long id = Review.getId();
        Log.d(TAG, "Deleting review with id: " + id);
        database.delete(GaspSQLiteHelper.REVIEWS_TABLE, GaspSQLiteHelper.REVIEWS_COLUMN_ID
                + " = " + id, null);
    }

    public void insertRestaurant(Restaurant restaurant) {
        ContentValues values = new ContentValues();
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_ID, restaurant.getId());
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME, restaurant.getName());
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE, restaurant.getWebsite());
        long insertId = database.insert(GaspSQLiteHelper.RESTAURANTS_TABLE, null,
                values);
        if (insertId != -1) {
            Log.d(TAG, "Inserted restaurant with id: " + insertId);
        } else {
            Log.e(TAG, "Error inserting restaurant with id: " + restaurant.getId());
        }
    }

    public void deleteRestaurant(Restaurant restaurant) {
        long id = restaurant.getId();
        Log.d(TAG, "Deleting restaurant with id: " + id);
        database.delete(GaspSQLiteHelper.RESTAURANTS_TABLE,
                GaspSQLiteHelper.RESTAURANTS_COLUMN_ID
                        + " = " + id, null);
    }

    public List<Review> getAllReviews() {
        List<Review> Reviews = new ArrayList<Review>();

        Cursor cursor = database.query(GaspSQLiteHelper.REVIEWS_TABLE,
                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = cursorToReview(cursor);
            Reviews.add(review);
            cursor.moveToNext();
        }
        cursor.close();
        return Reviews;
    }

    public List<String> getAllReviewsAsStrings() {
        List<String> reviewStrings = new ArrayList<String>();

        Cursor cursor = database.query(GaspSQLiteHelper.REVIEWS_TABLE,
                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Review review = cursorToReview(cursor);
            reviewStrings.add(review.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return reviewStrings;
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
