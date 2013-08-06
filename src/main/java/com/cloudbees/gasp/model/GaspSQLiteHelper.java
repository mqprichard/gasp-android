package com.cloudbees.gasp.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by markprichard on 7/14/13.
 */
public class GaspSQLiteHelper extends SQLiteOpenHelper {
    private static String TAG = GaspSQLiteHelper.class.getName();

    public static final String REVIEWS_COLUMN_ID = "id";
    public static final String REVIEWS_COLUMN_RESTAURANT_ID = "restaurant_id";
    public static final String REVIEWS_COLUMN_USER_ID = "user_id";
    public static final String REVIEWS_COLUMN_COMMENT = "comment";
    public static final String REVIEWS_COLUMN_STAR = "star";

    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_NAME = "name";

    public static final String RESTAURANTS_COLUMN_ID = "id";
    public static final String RESTAURANTS_COLUMN_NAME = "name";
    public static final String RESTAURANTS_COLUMN_WEBSITE = "website";
    public static final String RESTAURANTS_COLUMN_ADDRESS = "address";

    public static final String REVIEWS_TABLE = "reviews";
    public static final String RESTAURANTS_TABLE = "restaurants";
    public static final String USERS_TABLE = "users";

    private static final String DATABASE_NAME = "gasp.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statements to create a new database.
    private static final String CREATE_REVIEWS_TABLE = "create table " +
            REVIEWS_TABLE + " (" +
            REVIEWS_COLUMN_ID + " integer primary key, " +
            REVIEWS_COLUMN_USER_ID + " integer not null, " +
            REVIEWS_COLUMN_RESTAURANT_ID + " integer not null, " +
            REVIEWS_COLUMN_COMMENT + " string, " +
            REVIEWS_COLUMN_STAR + " integer not null);";

    private static final String CREATE_RESTAURANTS_TABLE = "create table " +
            RESTAURANTS_TABLE + " (" +
            RESTAURANTS_COLUMN_ID + " integer primary key, " +
            RESTAURANTS_COLUMN_NAME + " string, " +
            RESTAURANTS_COLUMN_ADDRESS + " string, " +
            RESTAURANTS_COLUMN_WEBSITE + " string);";

    private static final String CREATE_USERS_TABLE = "create table " +
            USERS_TABLE + " (" +
            USERS_COLUMN_ID + " integer primary key, " +
            USERS_COLUMN_NAME + " string not null);";

    public GaspSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_RESTAURANTS_TABLE);
        database.execSQL(CREATE_REVIEWS_TABLE);
        database.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REVIEWS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        onCreate(db);
    }

}