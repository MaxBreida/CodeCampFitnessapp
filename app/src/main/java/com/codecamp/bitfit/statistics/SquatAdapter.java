package com.codecamp.bitfit.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nils_ on 24.02.2018.
 */

public class SquatAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<Squat> mDataSource;

    public SquatAdapter(Context context, List<Squat> items){
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
       View rowView = mInflater.inflate(R.layout.activity_squat_statistics, parent, false);

       TextView dateTextView = (TextView) rowView.findViewById(R.id.textview_squatitem_date);
       TextView durationTextView = (TextView) rowView.findViewById(R.id.textview_squatitem_duration);
       TextView repeatsTextView = (TextView) rowView.findViewById(R.id.textview_squatitem_repeats);
       TextView caloriesTextView = (TextView) rowView.findViewById(R.id.textview_squatitem_calories);
       TextView spmTextView = (TextView) rowView.findViewById(R.id.textview_squatitem_spm);

       Squat squat = (Squat) getItem(i);

       dateTextView.setText(" " + squat.getCurrentDate());
       durationTextView.setText("Dauer: " + Util.getMillisAsTimeString(squat.getDurationInMillis()));
       repeatsTextView.setText("Wiederholungen: " + squat.getRepeats());
       caloriesTextView.setText("Verbrauchte Kalorien: " + squat.getCalories());
       spmTextView.setText("Squats/min: " + squat.getSquatPerMin());

       return rowView;
    }
}
