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

    public static abstract class USER_INFO implements BaseColumns{
        public static final String TABLE_NAME = "USER_INFO";
        public static final String COLUMN_NAME_ID = "_ID";
        public static final String COLUMN_NAME_ANDROID_USER_ID = "ANDROID_USER_ID";
        public static final String COLUMN_NAME_CREATED_DATE = "CREATED_DATE";
        public static final String COLUMN_NAME_GROUP_ID = "GROUP_ID";
        public static final String COLUMN_NAME_BUSINESS_ID = "BUSINESS_ID";
        public static final String COLUMN_NAME_VALIDATION_CODE = "VALIDATION_CODE";
        public static final String COLUMN_NAME_ACTIVE_USER = "ACTIVE_USER";
        public static final String COLUMN_NAME_SELECTED = "SELECTED";
        public static final String COLUMN_NAME_NICK_NAME = "NICK_NAME";
    }

    public static abstract class LOCATION_LOG implements BaseColumns{
        public static final String TABLE_NAME = "LOCATION_LOG";
        public static final String COLUMN_NAME_CREATED_DATE = "CREATED_DATE";
        public static final String COLUMN_NAME_SPEED = "SPEED";
        public static final String COLUMN_NAME_LONGITUDE = "LONGITUDE";
        public static final String COLUMN_NAME_LATITUDE = "LATITUDE";
        public static final String COLUMN_NAME_LOCATION_TIME = "LOCATION_TIME";
        public static final String COLUMN_NAME_BEARING = "BEARING";
        public static final String COLUMN_NAME_USER_ID = "USER_ID";
        public static final String COLUMN_NAME_SYNCED = "SYNCED";
        public static final String COLUMN_NAME_ACCURACY = "ACCURACY";
    }

    public static abstract class SESSION_ACTIVITIES implements BaseColumns{
        public static final String TABLE_NAME = "SESSION_ACTIVITIES";
        public static final String COLUMN_NAME_ID = "_ID";
        public static final String COLUMN_NAME_ACTIVITY_STATUS = "ACTIVITY_STATUS";
        public static final String COLUMN_NAME_ACTIVITY_TYPE = "ACTIVITY_TYPE"; // enum of the activity type
        public static final String COLUMN_NAME_CONFIDENCE = "CONFIDENCE";
        public static final String COLUMN_NAME_IS_DRIVING = "IS_DRIVING";
        public static final String COLUMN_NAME_CREATED_DATE = "CREATED_DATE";
    }
}
