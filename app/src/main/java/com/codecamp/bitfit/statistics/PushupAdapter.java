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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
        view = mInflater.inflate(R.layout.history_pushup_item, parent, false);

        TextView dateTextView = view.findViewById(R.id.textview_pushupitem_date);
        TextView durationTextView = view.findViewById(R.id.textview_pushupitem_duration);
        TextView repeatsTextView = view.findViewById(R.id.textview_pushupitem_repeats);
        TextView caloriesTextView = view.findViewById(R.id.textview_pushupitem_calories);
        TextView ppmTextView = view.findViewById(R.id.textview_pushupitem_ppm);

        PushUps pushUp = (PushUps) getItem(i);

        dateTextView.setText(pushUp.getCurrentDate());
        durationTextView.setText(String.format("%s min", Util.getMillisAsTimeString(pushUp.getDurationInMillis())));
        repeatsTextView.setText(String.format("%d Push-Up(s)", pushUp.getRepeats()));
        caloriesTextView.setText(String.format("%s kcal", pushUp.getCalories()));
        ppmTextView.setText(String.format("%s P/min", pushUp.getPushPerMin()));

        return view;
    }
}
