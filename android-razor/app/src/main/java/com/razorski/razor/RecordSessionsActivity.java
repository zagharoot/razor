package com.razorski.razor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.razorski.razor.data.FirebaseContract;

/**
 * This activity shows the list of recorded sessions. Use can click on each one to see the
 * details of each recorded session.
 */
public class RecordSessionsActivity extends AppCompatActivity {

    private RecordSessionsListAdapter recordAdapter;

    // ID for the loader that loads the data for the list.
    private static final int RECORD_SESSIONS_LOADER = 0;

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

        DatabaseReference allRecordSessions = FirebaseContract.getRecordSessionsRef();
        Query query = allRecordSessions;
        recordAdapter = new RecordSessionsListAdapter(query, this);

        ListView listView = (ListView) findViewById(R.id.record_sessions_list);
        listView.setAdapter(recordAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete_record_sessions) {
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to previous activity.
        }

        return super.onOptionsItemSelected(item);
    }
}
