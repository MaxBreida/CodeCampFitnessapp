package com.codecamp.bitfit.statistics;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.util.Util;

import java.util.List;

/**
 * Created by nils_ on 02.03.2018.
 */

class PushupAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<PushUps> mDataSource;

    public PushupAdapter(Context context, List<PushUps> items){
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
        View rowView = mInflater.inflate(R.layout.activity_pushup_statistics, parent, false);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.textview_pushupitem_date);
        TextView durationTextView = (TextView) rowView.findViewById(R.id.textview_pushup_duration);
        TextView repeatsTextView = (TextView) rowView.findViewById(R.id.textview_pushup_repeats);
        TextView caloriesTextView = (TextView) rowView.findViewById(R.id.textview_pushup_calories);
        TextView ppmTextView = (TextView) rowView.findViewById(R.id.textview_pushup_ppm);

        PushUps pushUp = (PushUps) getItem(i);

        dateTextView.setText(" " + pushUp.getCurrentDate());
        durationTextView.setText("Dauer: " + Util.getMillisAsTimeString(pushUp.getDurationInMillis()));
        repeatsTextView.setText("Wiederholungen: " + pushUp.getRepeats());
        caloriesTextView.setText("Verbrauchte Kalorien: " + pushUp.getCalories());
        ppmTextView.setText("Pushups/min: " + pushUp.getPushPerMin());

        return rowView;
    }
}
