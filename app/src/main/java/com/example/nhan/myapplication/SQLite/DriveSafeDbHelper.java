package com.example.nhan.myapplication.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;

public class DriveSafeDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DrivingDataContract.DrivingEntry.TABLE_NAME + " (" +
                    DrivingDataContract.DrivingEntry._ID + " INTEGER PRIMARY KEY," +
                    DrivingDataContract.DrivingEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_ACTIVITY_STATUS  + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_CREATED_DATE + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_MPH + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_COORDINATE + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_CREATED_TIME + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_DIRECTION + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_SYNCED + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_HAS_LOCATION + INT_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DrivingDataContract.DrivingEntry.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DRIVE_SAFE_DEV.db";

    public DriveSafeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
