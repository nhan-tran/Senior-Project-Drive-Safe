package com.example.nhan.myapplication.SQLite;

import android.provider.BaseColumns;

import com.google.android.gms.drive.Drive;

/**
 * Created by Nhan on 11/24/2014.
 * This is a contract class to define the table and data that will be logged in SQLite DB.
 */
public class DrivingDataContract {
    // prevent accidental initialization of this class by creating empty constructor
    public DrivingDataContract(){};

    public static abstract class DrivingEntry implements BaseColumns{
        public static final String TABLE_NAME = "DRIVE_LOG";
        public static final String COLUMN_NAME_ENTRY_ID = "DS_ID";
        public static final String COLUMN_NAME_ACTIVITY_STATUS = "ACTIVITY_STATUS";
        public static final String COLUMN_NAME_CREATED_DATE = "CREATED_DATE";
        public static final String COLUMN_NAME_MPH = "MPH";
        public static final String COLUMN_NAME_COORDINATE = "COORDINATE";
        public static final String COLUMN_NAME_CREATED_TIME = "CREATED_TIME";
        public static final String COLUMN_NAME_DIRECTION = "DIRECTION";
        public static final String COLUMN_NAME_USER_ID = "USER_ID";
        public static final String COLUMN_NAME_SYNCED = "SYNCED";
        public static final String COLUMN_NAME_HAS_LOCATION = "HAS_LOCATION";
    }
}
