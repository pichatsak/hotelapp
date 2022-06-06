package com.devpos.hotelapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;


public class MyApplication extends Application {
    private static String user_id;
    private static String rentIdViewCur;
    private static String type;
    private static String urlStorage ="gs://engless2022.appspot.com";
    @Override
    public void onCreate() {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();
    }

    public static String getUser_id() {
        return user_id;
    }

    public static void setUser_id(String user_id) {
        MyApplication.user_id = user_id;
    }

    public static String getRentIdViewCur() {
        return rentIdViewCur;
    }

    public static void setRentIdViewCur(String rentIdViewCur) {
        MyApplication.rentIdViewCur = rentIdViewCur;
    }
}
