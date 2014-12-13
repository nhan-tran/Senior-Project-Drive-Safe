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
    DriveSafeDbHelper mDbHelper;
    //SQLiteDatabase db;

    public DAL(Context context) {
        mContext = context;
        mDbHelper = new DriveSafeDbHelper(mContext);

    }

    public void WriteLog(ContentValues values)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //db = local;

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    LOCATION_LOG.TABLE_NAME,
                    null,
                    values);
        }
        db.close();
    }

   /* public long InsertSessionActivity(ContentValues values)
    {
        long newRowId = 0;

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(
                    DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME,
                    null,
                    values);
        }

        return newRowId;
    }*/

    public Cursor GetLatestSessionActivities()
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "  + DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME +
                            " order by _id desc", new String[]{} );

        return cursor;
    }

    // Insert a record into the Drive_Safe.db with the @values
    public long InsertRecord(String table, ContentValues values)
    {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = 0;

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            newRowId = db.insert(table, null, values);
        }

        return newRowId;
    }
}
