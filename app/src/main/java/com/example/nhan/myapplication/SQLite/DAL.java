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

        if (values != null){
            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    DrivingEntry.TABLE_NAME,
                    null,
                    values);
        }

    }

}
