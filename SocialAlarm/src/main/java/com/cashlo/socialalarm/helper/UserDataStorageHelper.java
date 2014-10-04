package com.cashlo.socialalarm.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Cash on 2014/10/04.
 */
public class UserDataStorageHelper {
    private static final String SHARED_PREFERENCES_USER_DATA_NAME = "USER_DATA";
    private static final String USER_PROGRESS = "USER_PROGRESS";

    public static final String USER_DATA_GREETING = "USER_DATA_GREETING";
    public static final String USER_DATA_FIRST_NAME = "USER_DATA_FIRST_NAME";
    public static final String USER_DATA_LAST_NAME = "USER_DATA_LAST_NAME";
    public static final String USER_DATA_SPEECH = "USER_DATA_SPEECH";




    public static void storeUserProgress(Context context, int progress){
        SharedPreferences mFacebookUserDate = context.getSharedPreferences(SHARED_PREFERENCES_USER_DATA_NAME, Context.MODE_PRIVATE);
        mFacebookUserDate.edit().putInt(USER_PROGRESS, progress).apply();
    }

    public static int getUserProgress(Context context){
        SharedPreferences mFacebookUserDate = context.getSharedPreferences(SHARED_PREFERENCES_USER_DATA_NAME, Context.MODE_PRIVATE);
        return mFacebookUserDate.getInt(USER_PROGRESS, 0);
    }

    public static void storeUserData(Context context,String key, String value){
        SharedPreferences mFacebookUserDate = context.getSharedPreferences(SHARED_PREFERENCES_USER_DATA_NAME, Context.MODE_PRIVATE);
        mFacebookUserDate.edit().putString(key, value).apply();
    }

    public static String getUserData(Context context, String key){
        SharedPreferences mFacebookUserDate = context.getSharedPreferences(SHARED_PREFERENCES_USER_DATA_NAME, Context.MODE_PRIVATE);
        return mFacebookUserDate.getString(key, "");
    }

}
