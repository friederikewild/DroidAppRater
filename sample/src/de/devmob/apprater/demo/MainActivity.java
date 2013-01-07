package de.devmob.apprater.demo;

import de.devmob.androlib.apprater.AppraterUtils;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

/**
 * Main activity of the demo application
 * 
 * @author friederike.wild
 */
public class MainActivity extends Activity
{
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
    }
}
