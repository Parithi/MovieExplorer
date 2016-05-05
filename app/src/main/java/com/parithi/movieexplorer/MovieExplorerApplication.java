package com.parithi.movieexplorer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by earul on 12/28/15.
 */
public class MovieExplorerApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        MovieExplorerApplication.context = context;
    }

    public static void setStoredSortMode(String status) {
        SharedPreferences settings = context.getSharedPreferences(
                Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.SELECTED_SORT_MODE, status);
        editor.commit();
    }

    public static String getStoredSortMode() {
        SharedPreferences settings = context.getSharedPreferences(
                Constants.SHARED_PREFERENCES, MODE_PRIVATE);
        return settings.getString(Constants.SELECTED_SORT_MODE, Constants.MOST_POPULAR_PARAM);
    }
}
