package com.example.nhan.myapplication.SQLite;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DriveSafeDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TBL_LOCATION_LOG =
            "CREATE TABLE " + DrivingDataContract.LOCATION_LOG.TABLE_NAME + " (" +
                    DrivingDataContract.LOCATION_LOG._ID + " INTEGER PRIMARY KEY," +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACTIVITY_STATUS + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SPEED + REAL_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LATITUDE + REAL_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LONGITUDE + REAL_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_LOCATION_TIME + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_SYNCED + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_HAS_LOCATION + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_BEARING + REAL_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_ACCURACY + REAL_TYPE + COMMA_SEP +
                    DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CONFIDENCE + INT_TYPE +
                    " )";

    private static final String SQL_CREATE_TBL_SESSION_ACTIVITIES =
            "CREATE TABLE " + DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME + " (" +
                    DrivingDataContract.SESSION_ACTIVITIES._ID + " INTEGER PRIMARY KEY," +
                    DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_ACTIVITY_STATUS + TEXT_TYPE + COMMA_SEP +
                    DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_ACTIVITY_TYPE + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_CONFIDENCE + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_IS_DRIVING + INT_TYPE + COMMA_SEP +
                    DrivingDataContract.SESSION_ACTIVITIES.COLUMN_NAME_CREATED_DATE + TEXT_TYPE + " )";


    // onUpdate Delete queries // test
    private static final String SQL_DELETE_TBL_LOCATION_LOG =
            "DROP TABLE IF EXISTS " + DrivingDataContract.LOCATION_LOG.TABLE_NAME;

    private static final String SQL_DELETE_TBL_SESSION_ACTIVITIES =
            "DROP TABLE IF EXISTS " + DrivingDataContract.SESSION_ACTIVITIES.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "DRIVE_SAFE_DEV.db";

    public DriveSafeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TBL_LOCATION_LOG);
        db.execSQL(SQL_CREATE_TBL_SESSION_ACTIVITIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_TBL_LOCATION_LOG);
        //db.execSQL(SQL_DELETE_TBL_SESSION_ACTIVITIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //*****************************NOT YOUR STUFF DON'T TOUCH***************************************
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
