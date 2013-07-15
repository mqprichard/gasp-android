package com.cloudbees.gasp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by markprichard on 7/14/13.
 */
public class ReviewsSQLiteHelper extends SQLiteOpenHelper {
    private static String TAG = ReviewsSQLiteHelper.class.getName();

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_COMMENT = "comment";
    public static final String COLUMN_STAR = "star";
    public static final String REVIEWS_TABLE = "reviewsTable";

    private static final String DATABASE_NAME = "gaspDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create a new database.
    private static final String DATABASE_CREATE = "create table " +
            REVIEWS_TABLE + " (" + COLUMN_ID +
            " integer primary key, " +
            COLUMN_USER_ID + " integer not null, " +
            COLUMN_RESTAURANT_ID + " integer not null, " +
            COLUMN_COMMENT + " string, " +
            COLUMN_STAR + " integer not null);";

    public ReviewsSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + REVIEWS_TABLE);
        onCreate(db);
    }

}