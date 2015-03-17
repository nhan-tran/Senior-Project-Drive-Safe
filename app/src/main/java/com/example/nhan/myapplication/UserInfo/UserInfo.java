package com.example.nhan.myapplication.UserInfo;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Build;

import com.example.nhan.myapplication.AppPrefs.AppPrefs;
import com.example.nhan.myapplication.DriveSafeApp;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Nhan on 3/16/2015.
 */
public class UserInfo
{
    public String _AndroidUserId;
    public String _BusinessId;
    public String _ValidationCode;
    public int _ActiveUser;
    public int _Selected;
    public String _NickName;

    public UserInfo(){

    }


    // check if there's a user_info record that is 'selected' meaning the phone using this user and return it, if there isn't create one and return it
    public static UserInfo init(){

        UserInfo returnUser = new UserInfo();
        DriveSafeProvider dsProvider = new DriveSafeProvider(DriveSafeApp.getContext());

        Cursor cUserInfo = dsProvider.UserInfoGetSelectedUser();
        if (cUserInfo.getCount() > 0) {
            // there is a selected user so return it
            cUserInfo.moveToFirst();

            returnUser._AndroidUserId = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID));
            returnUser._BusinessId = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_BUSINESS_ID));
            returnUser._ValidationCode = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_VALIDATION_CODE));
            returnUser._ActiveUser = cUserInfo.getInt(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ACTIVE_USER));
            returnUser._Selected = cUserInfo.getInt(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_SELECTED));
            returnUser._NickName = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_NICK_NAME));
        }
        else {
            // no selected users were found, create a new UserInfo and return it
            ContentValues cv = new ContentValues();
            UserInfo newUser = new UserInfo();
            int countNickName = AppPrefs.GetCountNickName();
            AppPrefs.SetCountNickName(++countNickName); // increment the count

            newUser._AndroidUserId = newUser.getUniquePsuedoID();
            newUser._NickName = "New User " + countNickName;
            newUser._ActiveUser = 1;
            newUser._Selected = 1;
            newUser._BusinessId = "0";
            newUser._ValidationCode = UUID.randomUUID().toString();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            cv.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID, newUser._AndroidUserId);
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_NICK_NAME, newUser._NickName);
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_ACTIVE_USER, newUser._ActiveUser);
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_SELECTED, newUser._Selected);
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_BUSINESS_ID, newUser._BusinessId);
            cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_VALIDATION_CODE, newUser._ValidationCode);

            long success = dsProvider.InsertRecord(DrivingDataContract.USER_INFO.TABLE_NAME, cv);
            if (success > 0) {
                returnUser = newUser;
                // set the UserInfo prefs
                AppPrefs.SetUserInfo(returnUser._ValidationCode);
            }
            else
            {
                // set the UserInfo prefs
                AppPrefs.SetUserInfo("");
            }
        }

        cUserInfo.close();  // close cursor

        return returnUser;
    }

    /**
     * Return pseudo unique ID
     * @return ID
     */
    protected String getUniquePsuedoID() {
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
