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

import com.cloudbees.gasp.model.Restaurant;

/**
 * Adapter class mapping the Gasp database to com.cloudbees.gasp.model.Restaurant
 * Implements insert, cursor and list methods: main interface to Restaurant data
 */
public class RestaurantDataAdapter extends GaspDataAdapter<Restaurant> {
    private static final String TAG = RestaurantDataAdapter.class.getName();

    private final String[] allColumns = {GaspSQLiteHelper.RESTAURANTS_COLUMN_ID,
            GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME,
            GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE,
            GaspSQLiteHelper.RESTAURANTS_COLUMN_PLACESID};

    private static final String idColumnName = GaspSQLiteHelper.RESTAURANTS_COLUMN_ID;
    private static final String tableName = GaspSQLiteHelper.RESTAURANTS_TABLE;

    public RestaurantDataAdapter(Context context) {
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
    public String[] getAllColumns() {
        return allColumns;
    }

    @Override
    protected void putValues(ContentValues values, Restaurant restaurant) {
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_ID, restaurant.getId());
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_NAME, restaurant.getName());
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_WEBSITE, restaurant.getWebsite());
        values.put(GaspSQLiteHelper.RESTAURANTS_COLUMN_PLACESID, restaurant.getPlacesId());
    }

    @Override
    protected Restaurant fromCursor(Cursor cursor) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(cursor.getInt(0));
        restaurant.setName(cursor.getString(1));
        restaurant.setWebsite(cursor.getString(2));
        restaurant.setPlacesId(cursor.getString(3));
        return restaurant;
    }

    /**
     * Lookup restaurant by Google Places API id
     *
     * @param placesId Google Places id
     * @return Gasp Restaurant object
     */
    public Restaurant findRestaurantByPlacesId(String placesId) {
        Cursor cursor = null;
        Restaurant restaurant = null;

        try {
            cursor = database.query(GaspSQLiteHelper.RESTAURANTS_TABLE, allColumns,
                    GaspSQLiteHelper.RESTAURANTS_COLUMN_PLACESID + " = ?",
                    new String[]{placesId}, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                restaurant = fromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return restaurant;
    }
}
