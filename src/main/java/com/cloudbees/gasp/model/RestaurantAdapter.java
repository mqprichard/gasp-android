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
 * Copyright (c) 2013 Mark Prichard, CloudBees
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class RestaurantAdapter {
    private static final String TAG = RestaurantAdapter.class.getName();

    private SQLiteDatabase database;
    private final GaspSQLiteHelper dbHelper;

    private final String[] allColumns = { GaspSQLiteHelper.RESTAURANTS_COLUMN_ID,
                                    GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME,
                                    GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE,
                                    GaspSQLiteHelper.RESTAURANTS_COLUMN_ADDRESS};

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
        try {
            ContentValues values = new ContentValues();
            values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_ID, restaurant.getId());
            values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME, restaurant.getName());
            values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE, restaurant.getWebsite());
            values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_ADDRESS, restaurant.getAddress());
            long insertId = database.insertOrThrow(GaspSQLiteHelper.RESTAURANTS_TABLE, null, values);
            if (insertId != -1) {
                Log.d(TAG, "Inserted restaurant with id: " + insertId);
            } else {
                Log.e(TAG, "Error inserting restaurant with id: " + restaurant.getId());
            }
        } catch (SQLiteConstraintException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
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

    private Restaurant cursorToRestaurant(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(cursor.getInt(0));
        restaurant.setName(cursor.getString(1));
        restaurant.setWebsite(cursor.getString(2));
        restaurant.setAddress(cursor.getString(3));
        return restaurant;
    }
}
