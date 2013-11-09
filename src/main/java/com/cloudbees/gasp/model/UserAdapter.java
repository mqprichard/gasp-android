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

public class UserAdapter {
    private static final String TAG = UserAdapter.class.getName();

    private SQLiteDatabase database;
    private final GaspSQLiteHelper dbHelper;

    private final String[] allColumns = { GaspSQLiteHelper.USERS_COLUMN_ID,
                                          GaspSQLiteHelper.USERS_COLUMN_NAME };

    public UserAdapter(Context context) {
        dbHelper = new GaspSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertUser(User user) {
        try {
            ContentValues values = new ContentValues();
            values.put(GaspSQLiteHelper.USERS_COLUMN_ID, user.getId());
            values.put(GaspSQLiteHelper.USERS_COLUMN_NAME, user.getName());
            long insertId = database.insertOrThrow(GaspSQLiteHelper.USERS_TABLE, null, values);
            if (insertId != -1) {
                Log.d(TAG, "Inserted user with id: " + insertId);
            } else {
                Log.e(TAG, "Error inserting user with id: " + user.getId());
            }
        } catch (SQLiteConstraintException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(User user) {
        long id = user.getId();
        Log.d(TAG, "Deleting user with id: " + id);
        database.delete(GaspSQLiteHelper.USERS_TABLE,
                        GaspSQLiteHelper.USERS_COLUMN_ID
                        + " = " + id, null);
    }

    public List<User> getAll() {
        List<User> users = new ArrayList<User>();

        Cursor cursor = database.query(GaspSQLiteHelper.USERS_TABLE,
                        allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        return user;
    }
}
