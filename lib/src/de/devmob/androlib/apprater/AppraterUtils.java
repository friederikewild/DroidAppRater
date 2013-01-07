/*
 * $Id$
 * 
 * Copyright 2012 Friederike Wild, created 06.05.2012
 */
package de.devmob.androlib.apprater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Class to use the app rater component.
 * 
 * @author friederike
 * @version $Rev$ $Date$
 */
public class AppraterUtils
{
    /** 
     * Meta key to configure the amount of app launches, till the rating dialog should be shown for the first time.
     *  
     * <meta-data android:name="de.devmob.launch_till_rate" android:value="4" />
     */
    public static final String META_CONFIG_LAUNCH_BEFORE_RATE = "de.devmob.launch_till_rate";

    /** 
     * Meta key to configure the amount of days after install, till the rating dialog should be shown for the first time.
     *  
     * <meta-data android:name="de.devmob.days_till_rate" android:value="4" />
     */
    public static final String META_CONFIG_DAYS_BEFORE_RATE = "de.devmob.days_till_rate";

    /** 
     * Meta key to configure the amount of days after install, till the rating dialog should be shown for the first time.
     *  
     * <meta-data android:name="de.devmob.events_till_rate" android:value="2" />
     */
    public static final String META_CONFIG_EVENTS_BEFORE_RATE = "de.devmob.events_till_rate";

    /** 
     * Meta key to configure if app rating should log.
     *  
     * <meta-data android:name="de.devmob.verbose" android:value="true" />
     */
    public static final String META_CONFIG_VERBOSE = "de.devmob.verbose";

    /** Logging tag for the app rater component */
    public static final String LOG_TAG = "devmob_apprater";

    /** Flag to turn of the app rater is wanted */
    private static final boolean ENABLE_APPRATER = true;

    /** Default count before the rating dialog should be shown. */
    private static final int DEFAULT_LAUNCH_BEFORE_RATE = 4;

    /** Default days before the rating dialog should be shown. */
    private static final int DEFAULT_DAYS_BEFORE_RATE = 4;
    
    /** Default count of positive events before the rating dialog should be shown. */
    private static final int DEFAULT_EVENTS_BEFORE_RATE = 2;

    /**
     * Method to call from the onCreate method of the first activity that is shown.
     * 
     * @param context The current activity context
     * @param callbackHandler
     */
    public static void checkToShowRatingOnStart(final Context context, final AppraterCallback callbackHandler)
    {
        if (shouldAppShowRatingOnStart(context) && ENABLE_APPRATER)
        {
            showAppraterDialog(context, callbackHandler);
        }
    }

    /**
     * Method to call from any point during the application when something positive
     * to the user happened.
     * 
     * @param context The current activity context
     * @param callbackHandler
     */
    public static void checkToShowRatingOnEvent(final Context context, final AppraterCallback callbackHandler)
    {
        if (shouldAppShowRatingOnEvent(context) && ENABLE_APPRATER)
        {
            showAppraterDialog(context, callbackHandler);
        }
    }

    /**
     * Get the configured amount of app launches before the rating dialog should be shown.
     * 
     * @param context The current activity context
     * @return
     */
    private static int getConfigLaunchBeforeRateCount(Context context)
    {
        int launchBeforeRate = getConfigurationIntOrDefaultValue(context, META_CONFIG_LAUNCH_BEFORE_RATE, DEFAULT_LAUNCH_BEFORE_RATE);

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Devmob Apprater configured to wait for " + launchBeforeRate + " launches.");
        }

        return launchBeforeRate;
    }

    /**
     * Get the configured amount of days before the rating dialog should be shown.
     * 
     * @param context The current activity context
     * @return
     */
    private static int getConfigDaysBeforeRateCount(Context context)
    {
        int daysBeforeRate = getConfigurationIntOrDefaultValue(context, META_CONFIG_DAYS_BEFORE_RATE, DEFAULT_DAYS_BEFORE_RATE);

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Devmob Apprater configured to wait for " + daysBeforeRate + " days.");
        }

        return daysBeforeRate;
    }

    /**
     * Get the configured amount of positive events before the rating dialog should be shown.
     * 
     * @param context The current activity context
     * @return
     */
    private static int getConfigEventsBeforeRateCount(Context context)
    {
        int daysBeforeRate = getConfigurationIntOrDefaultValue(context, META_CONFIG_EVENTS_BEFORE_RATE, DEFAULT_EVENTS_BEFORE_RATE);

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Devmob Apprater configured to wait for " + daysBeforeRate + " positive events.");
        }

        return daysBeforeRate;
    }

    /**
     * Util method to get a configured int value from the application bundle information defined by the 
     * given key. In case the entry doesn't exist or anyhting goes wrong, the defaultValue is returned.
     * 
     * @param context
     * @param configKey
     * @param defaultValue
     * @return
     */
    private static int getConfigurationIntOrDefaultValue(Context context, String configKey, int defaultValue)
    {
        int returnValue = defaultValue;

        try
        {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle aBundle = ai.metaData;
            returnValue = aBundle.getInt(configKey);
            // Check if available
            if (returnValue == 0)
            {                
                returnValue = defaultValue;
            }
        }
        catch (Exception e)
        {
            // Ignore and reset to default
            returnValue = defaultValue;
        }

        return returnValue;
    }

    /**
     * Check if the app rating should be shown.
     * Checks the status of the app launches and the previous app rating usage.
     * 
     * @param context The current activity context
     * @return Flag if the dialog should be shown.
     */
    private static boolean shouldAppShowRatingOnStart(Context context)
    {
        // No rating case it was already dismissed or rated.
        if (AppraterPreferences.isNeverShowApprater(context))
        {
            Log.i(AppraterUtils.LOG_TAG, "Apprater configured to never request rating via dialog (reset after re-install of the app). Checked on start.");
            return false;
        }

        // Check if enough days gone by
        long currentTime = System.currentTimeMillis();
        long storedTime = AppraterPreferences.getStoredStartDate(context);
        long daysPastSinceStart= ((currentTime - storedTime) / (1000 * 60 * 60 * 24));

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Apprater comparison " + daysPastSinceStart + " past ? >= " + getConfigDaysBeforeRateCount(context));
        }

        if (daysPastSinceStart < getConfigDaysBeforeRateCount(context))
        {
            return false;
        }

        // Check the usage
        int countOpened = AppraterPreferences.getCountOpened(context, true);
        if (countOpened % getConfigLaunchBeforeRateCount(context) == 0)
        {
            return true;
        }

        // Fallback is not to show the dialog
        return false;
    }

    /**
     * Check if the app rating should be shown.
     * Checks the status of the app events and the previous app rating usage.
     * 
     * @param context The current activity context
     * @return Flag if the dialog should be shown.
     */
    private static boolean shouldAppShowRatingOnEvent(Context context)
    {
        // No rating case it was already dismissed or rated.
        if (AppraterPreferences.isNeverShowApprater(context))
        {
            Log.i(AppraterUtils.LOG_TAG, "Apprater configured to never request rating via dialog (reset after re-install of the app). Checked on event.");
            return false;
        }

        // Check the usage
        int countEvents = AppraterPreferences.getCountEvents(context, true);
        if (countEvents % getConfigEventsBeforeRateCount(context) == 0)
        {
            return true;
        }

        // Fallback is not to show the dialog
        return false;
    }

    /**
     * Create and show the app rating dialog. This fetches the package name from the context
     * and uses the texts as given in the locale resources.  
     * 
     * @param context The current activity context
     * @param callbackHandler
     */
    private static void showAppraterDialog(final Context context, final AppraterCallback callbackHandler)
    {
        AlertDialog.Builder builderInvite = new AlertDialog.Builder(context);

        String packageName = "";
        String appName = "";
        try
        {
            // Get the package info manager from the given context
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            // Dynamically read the package name and the application name
            packageName = info.packageName;
            appName = context.getResources().getString(info.applicationInfo.labelRes);
        }
        catch (Exception e)
        {
            // When failing to get the needed information, we ignore the wish to show a rater dialog
            return;
        }

        Log.d("Appirater", "PackageName: " + packageName);

        
        // TODO (fwild): Add other kinds of links when different stores are supported
        
        // Create the link to the google play store detail page
        final String marketLink = "market://details?id=" + packageName;

        if (AppraterUtils.shouldLog(context))
        {            
            Log.i(AppraterUtils.LOG_TAG, "Url to link for rating: " + marketLink);
        }

        String title = context.getString(R.string.dialog_rate_title, appName);
        builderInvite.setTitle(title);

        String message = context.getString(R.string.dialog_rate_message, appName);
        builderInvite.setMessage(message);
        
        String buttonOK = context.getString(R.string.rating_dialog_button_ok);
        String buttonLater = context.getString(R.string.rating_dialog_button_later);
        String buttonNever = context.getString(R.string.rating_dialog_button_never);
        
        builderInvite.setPositiveButton(buttonOK, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                if (callbackHandler != null)
                {
                    callbackHandler.processRate();
                }

                // Mark as never ask for rating again (cause now it was done)
                AppraterPreferences.markNeverRate(context);
                
                // Trigger the rating intent
                Uri uri = Uri.parse(marketLink);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                dialog.dismiss();
            }
        }).setNeutralButton(buttonLater, new OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (callbackHandler != null)
                {
                    callbackHandler.processRemindMe();
                }
                
                // Mark as to ask later again
                AppraterPreferences.resetToRateLater(context);

                dialog.dismiss();
            }
        }).setNegativeButton(buttonNever, new OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                if (callbackHandler != null)
                {
                    callbackHandler.processNever();
                }
                
                // Mark as never ask for rating again
                AppraterPreferences.markNeverRate(context);
                
                dialog.cancel();
            }
        });
        builderInvite.create().show();
    }

    /**
     * Check if the app rater component should verbose its logs.
     * 
     * @param context The current activity context
     * @return Flag if logging is enabled.
     */
    public static boolean shouldLog(Context context)
    {
        boolean shouldLog = false;

        try
        {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle aBundle = ai.metaData;
            shouldLog = aBundle.getBoolean(META_CONFIG_VERBOSE);
        }
        catch (Exception e)
        {
            // Ignore and reset to default
            shouldLog = false;
        }

        return shouldLog;
    }
}
