package com.example.nhan.myapplication.UserInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.nhan.myapplication.AppPrefs.AppPrefs;
import com.example.nhan.myapplication.DriveSafeApp;
import com.example.nhan.myapplication.SQLite.DriveSafeDbHelper;
import com.example.nhan.myapplication.SQLite.DriveSafeProvider;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;

import org.json.JSONException;
import org.json.JSONObject;

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
    public String _MembershipId;
    public int _ActiveUser;
    public int _Selected;
    public String _NickName;
    public String _GroupId;

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

            returnUser = new UserInfo();    // init the returnUser
            returnUser._AndroidUserId = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID));
            returnUser._BusinessId = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_BUSINESS_ID));
            returnUser._MembershipId = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_MEMBERSHIP_ID));
            returnUser._ActiveUser = cUserInfo.getInt(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ACTIVE_USER));
            returnUser._Selected = cUserInfo.getInt(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_SELECTED));
            returnUser._NickName = cUserInfo.getString(cUserInfo.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_NICK_NAME));

            // set the UserInfo prefs
            AppPrefs.SetMembershipId(returnUser._MembershipId);
            AppPrefs.SetAndroidId(returnUser._AndroidUserId);
        }
        else {
            // no selected users were found, create a new UserInfo and return it if successful
            UserInfo newUser = UserInfo.CreateUser(null, "DRVSF", "10", 0);
            if (newUser != null) {
                returnUser = newUser;
                // set the UserInfo prefs
                AppPrefs.SetMembershipId(returnUser._MembershipId);
                AppPrefs.SetAndroidId(returnUser._AndroidUserId);
            }
            else
            {
                // set the UserInfo prefs
                AppPrefs.SetMembershipId("");
                AppPrefs.SetAndroidId("");
            }
        }

        cUserInfo.close();  // close cursor

        return returnUser;
    }

    public static UserInfo CreateUser(String nickName, String businessId, String groupId, int selected)
    {
        DriveSafeProvider dsProvider = new DriveSafeProvider(DriveSafeApp.getContext());
        ContentValues cv = new ContentValues();
        UserInfo newUser = new UserInfo();

        if (nickName == null)
        {
            int countNickName = AppPrefs.GetCountNickName();
            AppPrefs.SetCountNickName(++countNickName); // increment the count

            nickName = "New User " + countNickName;
        }

        newUser._AndroidUserId = newUser.getUniquePsuedoID();
        newUser._NickName = nickName;
        newUser._ActiveUser = 1;
        newUser._Selected = selected;
        newUser._BusinessId = businessId;
        newUser._GroupId = groupId;
        newUser._MembershipId = UUID.randomUUID().toString();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        cv.put(DrivingDataContract.LOCATION_LOG.COLUMN_NAME_CREATED_DATE, dateFormat.format(date));
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID, newUser._AndroidUserId);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_NICK_NAME, newUser._NickName);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_ACTIVE_USER, newUser._ActiveUser);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_SELECTED, newUser._Selected);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_BUSINESS_ID, newUser._BusinessId);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_MEMBERSHIP_ID, newUser._MembershipId);
        cv.put(DrivingDataContract.USER_INFO.COLUMN_NAME_GROUP_ID, newUser._GroupId);

        long success = dsProvider.InsertRecord(DrivingDataContract.USER_INFO.TABLE_NAME, cv);
        if (success > 0)
        {
            // new user created - fire off the syncing of all unsynced users
            SyncUserInfo();
            return newUser;
        }
        else
        {
            return null;
        }
    }

    public static Boolean SyncUserInfo(){
        int syncSuccess = 0;
        DriveSafeDbHelper mDbHelper = new DriveSafeDbHelper(DriveSafeApp.getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor mCursor = db.rawQuery("select * from "  + DrivingDataContract.USER_INFO.TABLE_NAME +
                " where " + DrivingDataContract.USER_INFO.COLUMN_NAME_SYNCED + " is null ", new String[]{});

        JSONObject jsonObj = new JSONObject();

        mCursor.moveToFirst();

        while (!mCursor.isAfterLast()) {
            String createdDate = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_CREATED_DATE));
            String android_user_id = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID));
            String business_id = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_BUSINESS_ID));
            String group_id = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_GROUP_ID));
            String membership_id = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_MEMBERSHIP_ID));
            int active_user = mCursor.getInt(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_ACTIVE_USER));
            String nick_name = mCursor.getString(mCursor.getColumnIndex(DrivingDataContract.USER_INFO.COLUMN_NAME_NICK_NAME));

            try {
                jsonObj.put("createddate", createdDate.toString());
                jsonObj.put("android_user_id", android_user_id);
                jsonObj.put("business_id", business_id);
                jsonObj.put("group_id", group_id);
                jsonObj.put("membership_id", membership_id);
                jsonObj.put("active_user", active_user);
                jsonObj.put("nickname", nick_name);

                RequestQueue queue = Volley.newRequestQueue(DriveSafeApp.getContext());  // this = context
                String url = "http://drivesafe-dev.azurewebsites.net/api/AndroidUserInfoSync";

                JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DriveSafeProvider db = new DriveSafeProvider(DriveSafeApp.getContext());
                        Log.i("volley", "response: " + response);

                        String membershipId = "0";
                        try {
                            membershipId = response.getString("Membership_Id");
                        }
                        catch (Exception e){
                            Log.d("Reponse JSON error", "JSON error i onRepsonse");
                        }
                        ContentValues cv = new ContentValues();
                        cv.put("SYNCED", 1);
                        db.UpdateUserInfoRecord(cv, membershipId);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("volley", "error: " + error);
                    }
                });
                queue.add(postRequest);
            } catch (JSONException e) {
                e.printStackTrace();
                syncSuccess = (syncSuccess == -1) ? syncSuccess : -1;
            }

            mCursor.moveToNext();
        }   // while

        return true;
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
