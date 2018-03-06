package com.codecamp.bitfit.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
import com.codecamp.bitfit.util.Util;

import java.util.List;

/**
 * Created by nils_ on 04.03.2018.
 */

class RunAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Run> mDataSource;

    public RunAdapter(Context context, List<Run> items){
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int i) {
        return mDataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View rowView = mInflater.inflate(R.layout.activity_run_statistics, parent, false);

        // can't resolve all of those ids, so i'm commenting it out for now (did someone forget to push some xmls, or is it a problem on my end?)
        /*TextView dateTextView = (TextView) rowView.findViewById(R.id.textview_runitem_date);
        TextView durationTextView = (TextView) rowView.findViewById(R.id.textview_runitem_duration);
        TextView distanceTextView = (TextView) rowView.findViewById(R.id.textview_runitem_distance);
        TextView caloriesTextView = (TextView) rowView.findViewById(R.id.textview_runitem_calories);
        TextView speedTextView = (TextView) rowView.findViewById(R.id.textview_runitem_speed);*/

        Run run = (Run) getItem(i);

        /*dateTextView.setText(" " + run.getCurrentDate());
        durationTextView.setText("Dauer: " + Util.getMillisAsTimeString(run.getDurationInMillis()));
        distanceTextView.setText("Distanz: " + Util.roundTwoDecimals(run.getDistance()));
        caloriesTextView.setText("Verbrauchte Kalorien: " + run.getCalories());
        speedTextView.setText("Geschwindigkeit: " + Util.roundTwoDecimals(run.getSpeed()));*/

        return rowView;
    }

}
