/*
 * Copyright (C) 2012 Friederike Wild <friederike.wild@devmob.de>
 * Created 06.05.2012
 * 
 * https://github.com/friederikewild/DroidAppRater
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.devmob.androlib.apprater;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class to handle the apprater specific preferences.
 * 
 * Inspired Arash Payan http://arashpayan.com/blog/2009/09/07/presenting-appirater 
 * and José Moura with https://github.com/zemariamm/Appirater-for-Android
 * 
 * @author Friederike Wild
 */
public class AppRaterPreferences
{
    /** The key to the shared preferences that handles storage of the app rater status quo. */
    private static final String KEY_PREFERENCES       = "de.devmob.APPRATER";

    /** Key to store the date to compare how long the user has the app. */
    private static final String PREF_LONG_START_DATE  = "PREF_LONG_START_DATE";
    /** Key to store how often the app was opened */
    private static final String PREF_INT_COUNT_OPEN   = "PREF_INT_COUNT_OPEN";
    /** Key to store how often the positive event was triggered. */
    private static final String PREF_INT_COUNT_EVENTS = "PREF_INT_COUNT_EVENTS";
    /** Key to store if app rating was done for this version */
    private static final String PREF_BOOL_RATED       = "PREF_BOOL";
    /** Key to store if app rating was denied */
    private static final String PREF_BOOL_NEVERRATE   = "PREF_BOOL_NEVERRATE";

    private SharedPreferences preferences;
    private boolean verbose;

    public AppRaterPreferences(Context context, boolean verbose)
    {
        // Get the shared preferences that hold the app rater usage status.
        int mode = Activity.MODE_PRIVATE;
        this.preferences = context.getSharedPreferences(KEY_PREFERENCES, mode);
        this.verbose = verbose;
    }

    /**
     * Check if rating was already done or denied. 
     * 
     * @return
     */
    public boolean isRatingRequestDeactivated()
    {
        SharedPreferences prefs = preferences;
        boolean userRated = prefs.getBoolean(PREF_BOOL_RATED, false);
        boolean userChoseNeverRate = prefs.getBoolean(PREF_BOOL_NEVERRATE, false);
        return userRated || userChoseNeverRate;
    }

    public boolean isRatingRequestDeclined()
    {
        SharedPreferences prefs = preferences;
        boolean userChoseNeverRate = prefs.getBoolean(PREF_BOOL_NEVERRATE, false);
        return userChoseNeverRate;
    }

    /**
     * Get the stored start date.
     * This is the first date in millis the app was launched on,
     * or the date the user decided to be asked later.
     * 
     * @return The date in millis to compare current date with
     */
    public long getStoredStartDate()
    {
        SharedPreferences prefs = preferences;
        long storedDate = prefs.getLong(PREF_LONG_START_DATE, 0);
        
        if (storedDate == 0)
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(PREF_LONG_START_DATE, System.currentTimeMillis());
            editor.commit();
        }

        return storedDate;
    }

    /**
     * Get the current count of opening the application.
     * Calling this method increases the counter before returning if the increase parameter is true.
     * 
     * @param increase Flag if the counter should be increased before returning.
     * @return The count of new app starts.
     */
    public int getCountOpened(boolean increase)
    {
        SharedPreferences prefs = preferences;
        int count = prefs.getInt(PREF_INT_COUNT_OPEN, 0);

        if (increase)
        {
            // Increase the counter
            count++;
        }

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Current count open: " + count);
        }

        // Store updated count
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_INT_COUNT_OPEN, count);
        editor.commit();

        return count;
    }

    /**
     * Get the current count of positive events logged in the application.
     * Calling this method increases the counter before returning if the increase parameter is true.
     * 
     * @param increase Flag if the counter should be increased before returning.
     * @return The count of positive events.
     */
    public int getCountEvents(boolean increase)
    {
        SharedPreferences prefs = preferences;
        int count = prefs.getInt(PREF_INT_COUNT_EVENTS, 0);

        if (increase)
        {
            // Increase the counter
            count++;
        }

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Current count events: " + count);
        }

        // Store updated count
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_INT_COUNT_EVENTS, count);
        editor.commit();

        return count;
    }

    protected void reset()
    {
        SharedPreferences.Editor editor = preferences.edit();

        // Remove all stored keys to clean up
        editor.remove(PREF_INT_COUNT_OPEN);
        editor.remove(PREF_INT_COUNT_EVENTS);
        editor.remove(PREF_LONG_START_DATE);
        editor.remove(PREF_BOOL_RATED);
        editor.remove(PREF_BOOL_NEVERRATE);
        editor.commit();

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Info: Reset all stored preferences!");
        }
    }

    /**
     * Store that user rated - don't ask until further notice
     * e.g. about major version update.
     */
    protected void storeRated()
    {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_BOOL_RATED, true);
        editor.commit();

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Info: Marked as rated!");
        }
    }

    /**
     * Reset the app rating counter and settings to automatically ask for rating later again. 
     */
    protected void storeToRateLater()
    {
        SharedPreferences.Editor editor = preferences.edit();
        // Reset count starts and first start date
        editor.putInt(PREF_INT_COUNT_OPEN, 0);
        editor.putInt(PREF_INT_COUNT_EVENTS, 0);
        // Reset the day to restart comparing the days gone by
        editor.putLong(PREF_LONG_START_DATE, System.currentTimeMillis());

        editor.commit();

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Info: Marked to ask later for rating!");
        }
    }

    /**
     * Store to never ask for a rating again.
     */
    protected void storeRatingDeclined()
    {
        SharedPreferences.Editor editor = preferences.edit();

        // Remove all stored keys to clean up
        editor.remove(PREF_INT_COUNT_OPEN);
        editor.remove(PREF_INT_COUNT_EVENTS);
        editor.remove(PREF_LONG_START_DATE);

        // Store to never ask for rating again
        editor.putBoolean(PREF_BOOL_NEVERRATE, true);
        editor.commit();

        if (this.verbose)
        {            
            Log.i(AppRater.LOG_TAG, "Info: Marked to never show rating dialog again!");
        }
    }
}