package com.example.nhan.myapplication.AppPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.nhan.myapplication.DriveSafeApp;

/**
 * Created by Nhan on 3/16/2015.
 */
public class AppPrefs {

    public AppPrefs(){

    }

   public static void SetUserInfo(String validationId){
       AppPrefs appPref = new AppPrefs();
       Context ctx = DriveSafeApp.getContext();
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
       SharedPreferences.Editor prefsEdit = prefs.edit();
       prefsEdit.putString("userInfo", validationId);
       prefsEdit.commit(); // commit the edit!
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

}
