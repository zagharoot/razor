package com.razorski.razor;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.razorski.razor.data.ProtoConverter;

/**
 * Provides items to be placed in the list view.
 */

public class RecordSessionsListAdapter extends CursorAdapter {

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

    public RecordSessionsListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_record_session,
                parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        RecordSession recordSession = ProtoConverter.recordSessionFromCursor(cursor);
        long startTimeMsec = recordSession.getStartTimestampMsec();
        viewHolder.statTimeView.setText(Utils.NiceTimeFormatFromMillis(startTimeMsec));

        long duration = recordSession.getEndTimestampMsec() - recordSession.getStartTimestampMsec();
        viewHolder.durationView.setText(Utils.NiceDurationFormatFromMillis(duration));
    }
}
