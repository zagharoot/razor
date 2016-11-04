package com.razorski.razor;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.Query;
import com.razorski.razor.data.FirebaseDataProtos;

/**
 * Provides items to be placed in the list view.
 */

public class RecordSessionsListAdapter extends FirebaseListAdapter<FirebaseDataProtos.RecordSessionFB> {

    public RecordSessionsListAdapter(Query mRef, Activity activity) {
        super(mRef, FirebaseDataProtos.RecordSessionFB.class, R.layout.list_item_record_session,
                activity);
    }

    @Override
    protected void populateView(View v, FirebaseDataProtos.RecordSessionFB model) {
        ViewHolder viewHolder = new ViewHolder(v);

        long startTimeMsec = model.startTimestampMsec;
        viewHolder.statTimeView.setText(Utils.NiceTimeFormatFromMillis(startTimeMsec));

        long duration = model.endTimestampMsec - model.startTimestampMsec;
        viewHolder.durationView.setText(Utils.NiceDurationFormatFromMillis(duration));
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final TextView statTimeView;
        public final TextView durationView;

        public ViewHolder(View view) {
            statTimeView = (TextView) view.findViewById(R.id.list_item_start_time_textview);
            durationView = (TextView) view.findViewById(R.id.list_item_duration_textview);
        }
    }
}
