package com.example.nhan.myapplication.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.nhan.myapplication.MainActivity;
import com.example.nhan.myapplication.SQLite.DrivingDataContract.DrivingEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nhan on 11/24/2014.
 */
public class DAL {

    private Context mContext;

    public DAL(Context context) {
        mContext = context;
    }

    public void WriteLog(ContentValues values)
    {
        // testing
        DriveSafeDbHelper mDbHelper = new DriveSafeDbHelper(mContext);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        /* Create a new map of values, where column names are the keys
        //ContentValues values = new ContentValues();
        values.put(DrivingEntry.COLUMN_NAME_ENTRY_ID, "testId");
        values.put(DrivingEntry.COLUMN_NAME_ACTIVITY_STATUS, "testStatus");
        values.put(DrivingEntry.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
        values.put(DrivingEntry.COLUMN_NAME_MPH, 55);
        values.put(DrivingEntry.COLUMN_NAME_COORDINATE, "testCoords");
        values.put(DrivingEntry.COLUMN_NAME_CREATED_TIME, "testTime");
        values.put(DrivingEntry.COLUMN_NAME_DIRECTION, "testDir");
        values.put(DrivingEntry.COLUMN_NAME_USER_ID, "testUserId");
        values.put(DrivingEntry.COLUMN_NAME_SYNCED, 0);
        values.put(DrivingEntry.COLUMN_NAME_HAS_LOCATION, 0);
        values.put(DrivingEntry.COLUMN_NAME_CONFIDENCE, 0);*/

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                DrivingEntry.TABLE_NAME,
                null,
                values);
    }

}
