package de.devmob.apprater.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.devmob.androlib.apprater.AppraterCallback;
import de.devmob.androlib.apprater.AppraterUtils;

/**
 * Simple main activity of the demo application to show the usage of the
 * DroidAppRater utils.
 * The application enables logging of the library and fetches the logs
 * to present them inside the app. This way one can directly comprehend all
 * ongoing checks.
 * 
 * @author friederike.wild
 */
public class MainActivity extends Activity
{
    public static final String   LOG = "devmob_apprater_demo";

    /** The instance of the background taks to read the logs */
    private AppRaterLogReader    mBackgroundTask;
    /** The instance of a callback handler */
    private DemoAppraterCallback mDemoAppraterCallback;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_main);

        this.mDemoAppraterCallback = new DemoAppraterCallback();

        // Let the DroidAppRater check on each creation if the rating dialog should be shown:
//        AppraterUtils.checkToShowRatingOnStart(this);
        AppraterUtils.checkToShowRatingOnStart(this, mDemoAppraterCallback);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume()
    {
        super.onResume();

        updateLogging();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
    {
        switch(item.getItemId())
        {
            /*
            case R.id.menu_settings:
            {

                return true;
            }
            */
            case R.id.menu_info:
            {
                showDialog(R.id.dialog_info);
                return true;
            }
        }

        // Default handling
        return super.onOptionsItemSelected(item);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case R.id.dialog_info:
            {
                // Create the requested dialog
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.menu_info)
                        .setNeutralButton(android.R.string.ok, null);

                // Add a custom text view to enable a clickable link
                LayoutInflater layoutInflater = LayoutInflater.from(this);        
                final View textEntryView = layoutInflater.inflate(R.layout.layout_text_dialog, null);
                TextView messageTextView = ((TextView) textEntryView.findViewById(R.id.textMessage));
                messageTextView.setText(Html.fromHtml(this.getResources().getString(R.string.text_info)));
                messageTextView.setMovementMethod(LinkMovementMethod.getInstance());

                dialogBuilder.setView(textEntryView);

                return dialogBuilder.create();
            }
        }

        return null;
    }

    /**
     * Callback-method for button clicks
     * @param view
     */
    public void onButtonClick(View view)
    {
        // Let the DroidAppRater check on each positive event, if the rating dialog should be shown:
//        AppraterUtils.checkToShowRatingOnEvent(this);
        AppraterUtils.checkToShowRatingOnEvent(this, mDemoAppraterCallback);

        updateLogging();
    }

    /**
     * Private method to start the background task to fetch
     * the logging. Trigger when something has changed.
     */
    private void updateLogging()
    {
        // Safety-check if already running
        if (mBackgroundTask == null || !mBackgroundTask.isRunning)
        {
            mBackgroundTask = new AppRaterLogReader();
            mBackgroundTask.execute();
        }
    }

    /**
     * Private util class to read all logs from the DroidAppRater in the background
     * and write the result with latest first in the text view.
     * 
     * @author Friederike Wild
     */
    private class AppRaterLogReader extends AsyncTask<Void, Void, String>
    {
        public boolean isRunning = false;
        
        @Override
        protected String doInBackground(Void... params)
        {
            isRunning = true;
            
            try
            {
                // Put up the logcat filtering command
                StringBuffer baseCommandBuffer = new StringBuffer(); 
                baseCommandBuffer.append("logcat -d -v raw ");
                baseCommandBuffer.append(AppraterUtils.LOG_TAG); // Filter only the used Library Logging Tag
                baseCommandBuffer.append(":I"); // Filter only the info debug level
                baseCommandBuffer.append(" MyApp:D "); // Info for my app
                baseCommandBuffer.append(" *:S "); // Silence others
                
                Process process = Runtime.getRuntime().exec(baseCommandBuffer.toString());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                // Fetch the logs in a list to sort them in the opposite direction
                List<String> list = new ArrayList<String>();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    list.add(line + "\n");
                    
                    Log.d(LOG, "Logcat: " + line);
                }

                // Add the log lines in the opposite direction
                StringBuilder log = new StringBuilder();
                for (int lineIndex = list.size() - 1; lineIndex >= 0; lineIndex--)
                {
                    log.append(list.get(lineIndex));
                }

                return log.toString();
            }
            catch (Throwable t)
            {
                Log.e(LOG, "Exception while reading log: " + t.getMessage());
                isRunning = false;
            }

            return null;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result)
        {
            if (result != null)
            {
                // Update the text view to show the latest log history
                TextView textView = (TextView) findViewById(R.id.textLog);
                textView.setText(result);
            }
            
            isRunning = false;
        }
    }

    /**
     * Callback handler to watch the user interaction with the DroidAppRater.
     * For demo purposes this just triggers a toast after any of the dialog input.
     * 
     * @author Friederike Wild
     */
    public class DemoAppraterCallback implements AppraterCallback
    {
        /* (non-Javadoc)
         * @see de.devmob.androlib.apprater.AppraterCallback#processNever()
         */
        @Override
        public void processNever()
        {
            // Do something useful here. e.g. inform analytics tracker

            Toast.makeText(MainActivity.this, "Callback - User doesn't want to rate.", Toast.LENGTH_SHORT).show();
        }
    
        /* (non-Javadoc)
         * @see de.devmob.androlib.apprater.AppraterCallback#processRate()
         */
        @Override
        public void processRate()
        {
            // Do something useful here. e.g. inform analytics tracker

            Toast.makeText(MainActivity.this, "Callback - User is rating now.", Toast.LENGTH_SHORT).show();
        }
    
        /* (non-Javadoc)
         * @see de.devmob.androlib.apprater.AppraterCallback#processRemindMe()
         */
        @Override
        public void processRemindMe()
        {
            // Do something useful here. e.g. inform analytics tracker

            Toast.makeText(MainActivity.this, "Callback - User is willing to rate later.", Toast.LENGTH_SHORT).show();
        }
    }
}
