package com.example.nhan.myapplication.AppPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.nhan.myapplication.DriveSafeApp;
import com.example.nhan.myapplication.SQLite.DrivingDataContract;

/**
 * Created by Nhan on 3/16/2015. This class is a common place for the app to get/set its shared prefs through static methods.
 */
public class AppPrefs {

    public AppPrefs(){

    }

   public static void SetMembershipId(String validationId){
       AppPrefs appPref = new AppPrefs();
       Context ctx = DriveSafeApp.getContext();
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
       SharedPreferences.Editor prefsEdit = prefs.edit();
       prefsEdit.putString(DrivingDataContract.USER_INFO.COLUMN_NAME_MEMBERSHIP_ID, validationId);
       prefsEdit.commit(); // commit the edit!
   }

   public static String GetUserMemberId(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DriveSafeApp.getContext());
        String id = prefs.getString(DrivingDataContract.USER_INFO.COLUMN_NAME_MEMBERSHIP_ID, "");
        return id;
    }

    public static void SetAndroidId(String androidId){
        AppPrefs appPref = new AppPrefs();
        Context ctx = DriveSafeApp.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putString(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID, androidId);
        prefsEdit.commit(); // commit the edit!
    }

    public static String GetAndroidId(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DriveSafeApp.getContext());
        String id = prefs.getString(DrivingDataContract.USER_INFO.COLUMN_NAME_ANDROID_USER_ID, "");
        return id;
    }

   public static void SetCountNickName(int count){
       AppPrefs appPref = new AppPrefs();
       Context ctx = DriveSafeApp.getContext();
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
       SharedPreferences.Editor prefsEdit = prefs.edit();
       prefsEdit.putInt("countNickName", count);
       prefsEdit.commit(); // commit the edit!
   }

   public static int GetCountNickName(){
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DriveSafeApp.getContext());
       int count = prefs.getInt("countNickName", 1);
       return count;
   }

    public static void SetIsDrivingForSure(boolean value){
        AppPrefs appPref = new AppPrefs();
        Context ctx = DriveSafeApp.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("isDrivingForSure", value);
        prefsEdit.commit(); // commit the edit!
    }

    public static boolean GetIsDrivingForSure(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DriveSafeApp.getContext());
        boolean forSure = prefs.getBoolean("isDrivingForSure", false);
        return forSure;
    }

    public static void SetIsMonitoring(boolean status){
        Context ctx = DriveSafeApp.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("isMonitoring", status);
        prefsEdit.commit(); // commit the edit!
    }

    public static boolean GetIsMonitoring(){
        Context ctx = DriveSafeApp.getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean status = prefs.getBoolean("isMonitoring", false);
        return status;
    }
}
