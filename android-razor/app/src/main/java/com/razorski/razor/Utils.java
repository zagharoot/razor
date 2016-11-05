package com.razorski.razor;

import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility functions.
 */

public class Utils {

    public static String NiceTimeFormatFromMillis(long millis) {
        Date date = new Date(millis);
        return SimpleDateFormat.getDateTimeInstance().format(date);
    }

    public static String NiceDurationFormatFromMillis(long millis) {
        long duration = millis;
        if (duration < 1000) {
            return "" + duration + " MS";
        }

        duration /= 1000;
        if (duration < 60) {
            return "" + duration + " S";
        }

        duration /= 60;
        if (duration < 60) {
            return "" + duration + " M";
        }

        duration /= 60;
        return "" + duration + " H";
    }

    // Returns true if someone is logged in.
    public static boolean isLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // Makes the view visible only if a user is logged in.
    public static void visibleIfLoggedIn(View view) {
        if (isLoggedIn()) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
}
