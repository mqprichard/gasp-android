package com.cloudbees.gasp.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cloudbees.gasp.model.GaspDataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2013 Mark Prichard, CloudBees
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Abstract base class for Gasp Database Adapters
 * Provides methods to open/close database, insert/delete objects and perform SQL queries
 *
 * @param <T> The GaspDataObject type for the Adapter
 */
public abstract class GaspDataAdapter<T extends GaspDataObject> {
    protected String TAG = GaspDataAdapter.class.getName();

    protected SQLiteDatabase database;
    protected GaspSQLiteHelper dbHelper;

    // Sub-classes must implement to support database calls
    abstract protected String getTableName();

    abstract protected String getIdColumnName();

    abstract protected String[] getAllColumns();

    // Sub-classes must implement to allow conversion from database Cursors
    abstract protected void putValues(ContentValues values, T element);

    abstract protected T fromCursor(Cursor cursor);

    /**
     * Constructor: instantiates the GaspSQLiteHelper, which mediates database access
     *
     * @param context The calling activity context
     */
    protected GaspDataAdapter(Context context) {
        dbHelper = new GaspSQLiteHelper(context);
    }

    /**
     * Opens the Gasp database
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes the Gasp database
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Returns the last row in the Gasp database able for this GaspDataObject
     *
     * @return The row id (auto-increment)
     */
    public long getLastId() {
        final String query = "SELECT " + getIdColumnName()
                + " from " + getTableName()
                + " order by " + getIdColumnName()
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

    /**
     * Insert a GaspDataObject into the Gasp database
     *
     * @param element The object to insert
     */
    public void insert(T element) {
        try {
            ContentValues values = new ContentValues();
            putValues(values, element);

            long insertId = database.insertOrThrow(getTableName(), null, values);
            if (insertId != -1) {
                Log.d(TAG, "Inserted review with id: " + insertId);
            }
        } catch (SQLiteConstraintException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete a GaspDataObject from the Gasp database
     *
     * @param element The object to delete
     */
    public void delete(T element) {
        Log.d(TAG, "Deleting review with id: " + element.getId());
        database.delete(getTableName(), getIdColumnName()
                + " = " + element.getId(), null);
    }

    /**
     * Get all rows
     *
     * @return ArrayList containing the GaspDataObjects
     */
    public List<T> getAll() {
        Cursor cursor = database.query(getTableName(), getAllColumns(),
                null, null, null, null, null);
        return listFromCursor(cursor);
    }

    /**
     * Get all rows in descending order
     *
     * @return ArrayList containing the GaspDataObjects
     */
    public List<T> getAllDesc() {
        Cursor cursor = database.query(getTableName(), getAllColumns(),
                null, null, null, null, getIdColumnName() + " DESC", null);
        return listFromCursor(cursor);
    }

    /**
     * Get last N rows in descending order
     *
     * @param n Number of rows ro return
     * @return ArrayList containing the GaspDataObjects
     */
    public List<T> getLastNDesc(int n) {
        Cursor cursor = database.query(getTableName(), getAllColumns(),
                null, null, null, null, getIdColumnName() + " DESC", String.valueOf(n));
        return listFromCursor(cursor);
    }

    /**
     * Generate an ArrayList from a Cursor: useful for sub-classes building custom queries
     *
     * @param cursor The Cursor object from a database query
     * @return ArrayList containing the GaspDataObjects
     */
    protected List<T> listFromCursor(Cursor cursor) {
        List<T> list = new ArrayList<T>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            T element = fromCursor(cursor);
            list.add(element);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
