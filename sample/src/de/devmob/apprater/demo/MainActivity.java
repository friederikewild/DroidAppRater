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
 * Main activity of the demo application
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
        setContentView(R.layout.layout_main);

        // Let the apprater check on each creation if the rating dialog should be shown:
        AppraterUtils.checkToShowRatingOnStart(this, null);
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
        AppraterUtils.checkToShowRatingOnEvent(this, null);

        updateLogging();
    }
    
    
    private void updateLogging()
    {
        if (mBackgroundTask == null || !mBackgroundTask.isRunning)
        {
            mBackgroundTask = new AppRaterLogReader();
            mBackgroundTask.execute();
        }
    }
    

    private class AppRaterLogReader extends AsyncTask<Void, Void, String>
    {
        public boolean isRunning = false;
        
        @Override
        protected String doInBackground(Void... params)
        {
            isRunning = true;
            
            try
            {
                String baseCommand = "logcat -d -v raw";

                baseCommand += " devmob_apprater:I"; // Filter only the used Lib Tag
                baseCommand += " MyApp:D "; // Info for my app
                baseCommand += " *:S "; // Silence others
                
                Process process = Runtime.getRuntime().exec(baseCommand);

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
                TextView textView = (TextView) findViewById(R.id.textLog);
                textView.setText(result);
            }
            
            isRunning = false;
        }
    }
}
