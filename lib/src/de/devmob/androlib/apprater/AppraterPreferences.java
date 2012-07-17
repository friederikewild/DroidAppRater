/*
 * $Id$
 * 
 * Copyright 2012 Friederike Wild, created 06.05.2012
 */
package de.devmob.androlib.apprater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class to handle the apprater specific preferences.
 * 
 * Inspired by [zemariamm]: http://about.me/zemariamm
 * 
 * @author friederike
 * @version $Rev$ $Date$
 */
public class AppraterPreferences
{
    /** The key to the shared preferences that handles storage of the app rater status quo. */
    private static final String KEY_PREFERENCES       = "de.devmob.APPRATER";

    /** Key to store the date to compare how long the user has the app. */
    private static final String PREF_LONG_START_DATE  = "PREF_LONG_START_DATE";
    /** Key to store how often the app was opend */
    private static final String PREF_INT_COUNT_OPEN   = "PREF_INT_COUNT_OPEN";
    /** Key to store how often the positive event was triggered. */
    private static final String PREF_INT_COUNT_EVENTS = "PREF_INT_COUNT_EVENTS";
    /** Key to store if app rating was done or denied. */
    private static final String PREF_BOOL_NEVERRATE   = "PREF_BOOL_NEVERRATE";

    /**
     * Util method to get the shared preferences that hold the app rater usage status.
     * 
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context)
    {
        int mode = Activity.MODE_PRIVATE;
        return context.getSharedPreferences(KEY_PREFERENCES, mode);
    }

    /**
     * Check if rating was already done or denied. 
     * 
     * @param context
     * @return
     */
    public static boolean isNeverShowApprater(Context context)
    {
        SharedPreferences prefs = getPreferences(context);
        return prefs.getBoolean(PREF_BOOL_NEVERRATE, false);
    }

    /**
     * Reset the app rating counter and settings to automatically ask for rating later again. 
     * 
     * @param context
     */
    public static void resetToRateLater(Context context)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        // Reset count starts and first start date
        editor.putInt(PREF_INT_COUNT_OPEN, 0);
        editor.putInt(PREF_INT_COUNT_EVENTS, 0);
        // Reset the day to restart comparing the days gone by
        editor.putLong(PREF_LONG_START_DATE, System.currentTimeMillis());

        editor.commit();

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Info: Marked to ask later for rating!");
        }
    }

    /**
     * Store to never ask for a rating again.
     * 
     * @param context
     */
    public static void markNeverRate(Context context)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();

        // Remove all stored keys to clean up
        editor.remove(PREF_INT_COUNT_OPEN);
        editor.remove(PREF_INT_COUNT_EVENTS);
        editor.remove(PREF_LONG_START_DATE);

        // Store to never ask for rating again
        editor.putBoolean(PREF_BOOL_NEVERRATE, true);
        editor.commit();

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Info: Marked to never show rating dialog again!");
        }
    }

    /**
     * Get the stored start date.
     * This is the first date in millis the app was launched on,
     * or the date the user decied to be asked later.
     * 
     * @param context The current activity context
     * @return The date in millis to compare current date with
     */
    public static long getStoredStartDate(Context context)
    {
        SharedPreferences prefs = getPreferences(context);
        long storedDate = prefs.getLong(PREF_LONG_START_DATE, 0);
        
        if (storedDate == 0)
        {
            SharedPreferences.Editor editor = getPreferences(context).edit();
            editor.putLong(PREF_LONG_START_DATE, System.currentTimeMillis());
            editor.commit();
        }

        return storedDate;
    }

    /**
     * Get the current count of opening the application.
     * Calling this method increases the counter before returning if the increase parameter is true.
     * 
     * @param context The current activity context
     * @param increase Flag if the counter should be increased before returning.
     * @return The count of new app starts.
     */
    public static int getCountOpened(Context context, boolean increase)
    {
        SharedPreferences prefs = getPreferences(context);
        int count = prefs.getInt(PREF_INT_COUNT_OPEN, 0);

        if (increase)
        {
            // Increase the counter
            count++;
        }

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Current count open: " + count);
        }

        // Store updated count
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(PREF_INT_COUNT_OPEN, count);
        editor.commit();

        return count;
    }

    /**
     * Get the current count of positive events logged in the application.
     * Calling this method increases the counter before returning if the increase parameter is true.
     * 
     * @param context The current activity context
     * @param increase Flag if the counter should be increased before returning.
     * @return The count of positive events.
     */
    public static int getCountEvents(Context context, boolean increase)
    {
        SharedPreferences prefs = getPreferences(context);
        int count = prefs.getInt(PREF_INT_COUNT_EVENTS, 0);

        if (increase)
        {
            // Increase the counter
            count++;
        }

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Current count events: " + count);
        }

        // Store updated count
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(PREF_INT_COUNT_EVENTS, count);
        editor.commit();

        return count;
    }
}