package com.example.nhan.myapplication;

import android.app.Application;
import android.content.Context;

/**
 * Created by Nhan on 3/16/2015.
 * A static way to get the context of the application for passing into calls.
 * http://stackoverflow.com/questions/987072/using-application-context-everywhere
 */
public class DriveSafeApp extends Application {
        private static DriveSafeApp instance;

        public DriveSafeApp() {
            instance = this;
        }

        public static Context getContext() {
            return instance;
        }
}
