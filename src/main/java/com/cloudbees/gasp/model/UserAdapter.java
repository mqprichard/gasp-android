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
public class UserAdapter {
    private static final String TAG = UserAdapter.class.getName();

    // Database fields
    private SQLiteDatabase database;
    private GaspSQLiteHelper dbHelper;
    private String[] allColumns = { GaspSQLiteHelper.USERS_COLUMN_ID,
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
        ContentValues values = new ContentValues();
        values.put(GaspSQLiteHelper.USERS_COLUMN_ID, user.getId());
        values.put(GaspSQLiteHelper.USERS_COLUMN_NAME, user.getName());
        long insertId = database.insert(GaspSQLiteHelper.USERS_TABLE, null, values);
        if (insertId != -1) {
            Log.d(TAG, "Inserted user with id: " + insertId);
        } else {
            Log.e(TAG, "Error inserting user with id: " + user.getId());
        }
    }

    public void deleteUser(User user) {
        long id = user.getId();
        Log.d(TAG, "Deleting user with id: " + id);
        database.delete(GaspSQLiteHelper.USERS_TABLE,
                        GaspSQLiteHelper.USERS_COLUMN_ID
                        + " = " + id, null);
    }

    public List<User> getAllUsers() {
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

    public List<String> getAllUsersAsStrings() {
        List<String> reviewStrings = new ArrayList<String>();

        Cursor cursor = database.query(GaspSQLiteHelper.USERS_TABLE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = cursorToUser(cursor);
            reviewStrings.add(user.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return reviewStrings;
    }

    private User cursorToUser(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getInt(0));
        user.setName(cursor.getString(1));
        return user;
    }
}
