package com.razorski.razor;

import android.app.Application;
import android.content.Context;

/**
 * Basically provides a simple way to get the application context from everywhere.
 */
public class RazorApp extends Application {
    public static Context context;

    @Override public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}