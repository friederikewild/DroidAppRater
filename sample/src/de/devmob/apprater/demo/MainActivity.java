package de.devmob.apprater.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
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
    public static final String LOG = "devmob_apprater_demo";

    private AppRaterLogReader mBackgroundTask;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_main);

        // Let the apprater check on each creation if the rating dialog should be shown:
        AppraterUtils.checkToShowRatingOnStart(this);
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
        getMenuInflater().inflate(R.menu.layout_main, menu);
        return true;
    }

    /**
     * Callback-method for button clicks
     * @param view
     */
    public void onButtonClick(View view)
    {
        // Let the apprater check on each positive event, if the rating dialog should be shown:
        AppraterUtils.checkToShowRatingOnEvent(this);

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
}
