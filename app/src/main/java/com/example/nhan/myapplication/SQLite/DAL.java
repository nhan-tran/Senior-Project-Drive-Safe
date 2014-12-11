package com.example.nhan.myapplication.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nhan.myapplication.SQLite.DrivingDataContract.LOCATION_LOG;

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

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    LOCATION_LOG.TABLE_NAME,
                    null,
                    values);
        }
    }

    public void InsertSessionActivity(ContentValues values){
        // testing
        DriveSafeDbHelper mDbHelper = new DriveSafeDbHelper(mContext);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME,
                    null,
                    values);
        }
    }

    public Cursor LatestSessionActivities(){
        DriveSafeDbHelper mDbHelper = new DriveSafeDbHelper(mContext);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from "  + DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME +
                            " order by _id desc", new String[]{} );

        return cursor;
    }

}
