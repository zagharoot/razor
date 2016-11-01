package com.razorski.razor;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import com.razorski.razor.data.DataContract;

/**
 * This activity shows the list of recorded sessions. Use can click on each one to see the
 * details of each recorded session.
 */
public class RecordSessionsActivity extends AppCompatActivity {

    private RecordSessionsListAdapter recordAdapter;

    // Pointer to my UI elements.
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sessions);

        // Add the back button to the toolbar.
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        Cursor c = getContentResolver().query(
                DataContract.RecordSessionEntry.CONTENT_URI, null, null, null, null);

        recordAdapter = new RecordSessionsListAdapter(this, c, 0);
        ListView listView = (ListView) findViewById(R.id.record_sessions_list);
        listView.setAdapter(recordAdapter);
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
