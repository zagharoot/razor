package com.razorski.razor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * This activity shows the list of recorded sessions. Use can click on each one to see the
 * details of each recorded session.
 */
public class RecordSessionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sessions);

        // Add the back button to the toolbar.
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle arrow click.
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to previous activity.
        }

        return super.onOptionsItemSelected(item);
    }
}
