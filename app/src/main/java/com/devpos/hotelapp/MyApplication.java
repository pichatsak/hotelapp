package com.devpos.hotelapp;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Calendar;

import io.realm.Realm;


public class MyApplication extends Application {
    private static String user_id;
    private static String rentIdViewCur;
    private static String type;
    private static boolean isBackFromFragment=false;
    private static String urlStorage ="gs://engless2022.appspot.com";

    private static Calendar dateStartChoose = null;
    private static Calendar dateEndChoose = null;

    @Override
    public void onCreate() {

        Realm.init(this);
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

    public static Calendar getDateStartChoose() {
        return dateStartChoose;
    }

    public static void setDateStartChoose(Calendar dateStartChoose) {
        MyApplication.dateStartChoose = dateStartChoose;
    }

    public static Calendar getDateEndChoose() {
        return dateEndChoose;
    }

    public static void setDateEndChoose(Calendar dateEndChoose) {
        MyApplication.dateEndChoose = dateEndChoose;
    }

    public static boolean isIsBackFromFragment() {
        return isBackFromFragment;
    }

    public static void setIsBackFromFragment(boolean isBackFromFragment) {
        MyApplication.isBackFromFragment = isBackFromFragment;
    }
}
