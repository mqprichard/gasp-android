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
public class RestaurantAdapter {
    private static final String TAG = RestaurantAdapter.class.getName();

    private SQLiteDatabase database;
    private GaspSQLiteHelper dbHelper;

    private String[] allColumns = { GaspSQLiteHelper.RESTAURANTS_COLUMN_ID,
                                    GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME,
                                    GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE};

    public RestaurantAdapter(Context context) {
        dbHelper = new GaspSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
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

    public List<Restaurant> getAll() {
        List<Restaurant> restaurants = new ArrayList<Restaurant>();

        Cursor cursor = database.query(GaspSQLiteHelper.RESTAURANTS_TABLE,
                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Restaurant restaurant = cursorToRestaurant(cursor);
            restaurants.add(restaurant);
            cursor.moveToNext();
        }
        cursor.close();
        return restaurants;
    }

    public List<String> getStringList() {
        List<String> restaurantStrings = new ArrayList<String>();

        Cursor cursor = database.query(GaspSQLiteHelper.RESTAURANTS_TABLE,
                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Restaurant restaurant = cursorToRestaurant(cursor);
            restaurantStrings.add(restaurant.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return restaurantStrings;
    }

    private Restaurant cursorToRestaurant(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(cursor.getInt(0));
        restaurant.setName(cursor.getString(1));
        restaurant.setWebsite(cursor.getString(2));
        return restaurant;
    }
}
