package com.razorski.razor;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.razorski.razor.data.DataContract;

/**
 * This activity shows the list of recorded sessions. Use can click on each one to see the
 * details of each recorded session.
 */
public class RecordSessionsActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor> {

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


        Cursor c = getContentResolver().query(
                DataContract.RecordSessionEntry.CONTENT_URI, null, null, null,
                DataContract.RecordSessionEntry.COL_START_TIMESTAMP_MSEC + " DESC");

        recordAdapter = new RecordSessionsListAdapter(this, c, 0);
        ListView listView = (ListView) findViewById(R.id.record_sessions_list);
        listView.setAdapter(recordAdapter);

        getSupportLoaderManager().initLoader(RECORD_SESSIONS_LOADER, null, this);
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
            getContentResolver().delete(DataContract.RecordSessionEntry.CONTENT_URI, "", null);
            getSupportLoaderManager().restartLoader(RECORD_SESSIONS_LOADER, null, this);
            return true;
        }

        if (id == android.R.id.home) {
            finish(); // close this activity and return to previous activity.
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.

        String sortOrder = DataContract.RecordSessionEntry.COL_START_TIMESTAMP_MSEC + " DESC";
        Uri uri = DataContract.RecordSessionEntry.CONTENT_URI;

        return new CursorLoader(this, uri, null, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recordAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recordAdapter.swapCursor(null);
    }
}
