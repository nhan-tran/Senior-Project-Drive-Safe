package com.example.nhan.myapplication.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.nhan.myapplication.SQLite.DrivingDataContract;

import java.util.ArrayList;

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
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_MPH + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_COORDINATE + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_CREATED_TIME + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_DIRECTION + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_SYNCED + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.DrivingEntry. COLUMN_NAME_HAS_LOCATION + INT_TYPE  +
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

    // AndroidDatabaseManager helper functions
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
