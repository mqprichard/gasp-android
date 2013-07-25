package com.cloudbees.gasp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by markprichard on 7/14/13.
 */
public class UserAdapter implements IRestListener {
    private static final String TAG = UserAdapter.class.getName();

    private Uri mUri;
    private AsyncRestTask mAsyncRestTask = new AsyncRestTask(mUri, this);

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

    public void doSync() {
        mAsyncRestTask.doRest();
    }

    public void callCompleted(String result) {
        Log.d(TAG, result);
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> list = gson.fromJson(result, type);
    }
}
