package com.cloudbees.gasp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by markprichard on 7/14/13.
 */
public class ReviewAdapter {
    private static final String TAG = ReviewAdapter.class.getName();

    private SQLiteDatabase database;
    private GaspSQLiteHelper dbHelper;

    private String[] allColumns = { GaspSQLiteHelper.REVIEWS_COLUMN_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_RESTAURANT_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_USER_ID,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_COMMENT,
                                    GaspSQLiteHelper.REVIEWS_COLUMN_STAR };

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
        } catch (SQLiteConstraintException e){
            throw e;
        } catch (Exception e){
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
