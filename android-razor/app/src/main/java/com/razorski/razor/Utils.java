package com.razorski.razor;

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
}
