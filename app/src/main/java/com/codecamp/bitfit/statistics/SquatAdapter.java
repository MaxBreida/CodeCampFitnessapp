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
       view = mInflater.inflate(R.layout.history_squat_item, parent, false);

       TextView dateTextView = view.findViewById(R.id.textview_squatitem_date);
       TextView durationTextView = view.findViewById(R.id.textview_squatitem_duration);
       TextView repeatsTextView = view.findViewById(R.id.textview_squatitem_repeats);
       TextView caloriesTextView = view.findViewById(R.id.textview_squatitem_calories);
       TextView spmTextView = view.findViewById(R.id.textview_squatitem_spm);

       Squat squat = (Squat) getItem(i);

       dateTextView.setText(String.format("%s", squat.getCurrentDate()));
       durationTextView.setText(String.format("%s min", Util.getMillisAsTimeString(squat.getDurationInMillis())));
       repeatsTextView.setText(String.format("%d Squat(s)", squat.getRepeats()));
       caloriesTextView.setText(String.format("%s kcal", squat.getCalories()));
       spmTextView.setText(String.format("%s S/min", squat.getSquatPerMin()));

       return view;
    }
}
