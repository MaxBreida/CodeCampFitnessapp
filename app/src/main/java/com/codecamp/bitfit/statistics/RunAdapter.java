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
        view = mInflater.inflate(R.layout.history_run_item, parent, false);

        TextView dateTextView = view.findViewById(R.id.textview_runitem_date);
        TextView durationTextView = view.findViewById(R.id.textview_runitem_duration);
        TextView distanceTextView = view.findViewById(R.id.textview_runitem_distance);
        TextView caloriesTextView = view.findViewById(R.id.textview_runitem_calories);
        TextView speedTextView = view.findViewById(R.id.textview_runitem_speed);

        Run run = (Run) getItem(i);

        dateTextView.setText(run.getCurrentDate());
        durationTextView.setText(String.format("%s min", Util.getMillisAsTimeString(run.getDurationInMillis())));
        distanceTextView.setText(run.getDistanceInKmString());
        caloriesTextView.setText(String.format("%s kcal", run.getCalories()));
        speedTextView.setText(run.getAverageKmhString());

        return view;
    }

}
