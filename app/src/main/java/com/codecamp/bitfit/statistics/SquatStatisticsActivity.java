package com.codecamp.bitfit.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.Squat;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SquatStatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private LineChart lineChart;
    private SquatData currentData;
    private List<Squat> allSquats;

    private enum SquatData{
        REPETITIONS,
        CALORIES;

        /**
         * https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type#17006263
         * @return next value in enum
         */
        public SquatData next()
        {
            return values()[(ordinal()+1) % values().length];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_squat_statistics);


        if(!DBQueryHelper.findAllSquats().isEmpty()) {
            allSquats = DBQueryHelper.findAllSquats();

            barChart = findViewById(R.id.last_month_chart);
            lineChart = findViewById(R.id.last_seven_workouts_chart);
            currentData = SquatData.REPETITIONS;

            fillBarchart();
            fillLinechart();

            // on click listeners to set chart data (repetitions or calories)
            barChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {
                    currentData = currentData.next();
                    fillBarchart();
                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });
            lineChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                }

                @Override
                public void onChartLongPressed(MotionEvent me) {

                }

                @Override
                public void onChartDoubleTapped(MotionEvent me) {

                }

                @Override
                public void onChartSingleTapped(MotionEvent me) {
                    currentData = currentData.next();
                    fillLinechart();
                }

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

                }

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

                }

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {

                }
            });


            Button historyButton = findViewById(R.id.button_squat_statistics_history);
            historyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), SquatHistoryActivity.class));
                }
            });
        } else {
            Toast.makeText(this.getApplicationContext(), "Noch keine Workouts zum Anzeigen vorhanden", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fillBarchart() {
        List<BarEntry> entries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH);

        for(int i=0; i<allSquats.size(); i++) {
            Squat squat = allSquats.get(i);
            Date squatDate = Util.getStringAsDate(squat.getCurrentDate());
            boolean entryAdded = false;

            float data = 0;
            if(currentData.equals(SquatData.REPETITIONS))
                data = squat.getRepeats();
            else if (currentData.equals(SquatData.CALORIES))
                data = (float) squat.getCalories();

            if(squatDate.getMonth() == currentMonth) {
                // if entries is empty add one entry
                if(entries.isEmpty()) {
                    entries.add(new BarEntry(squatDate.getDate(), data));
                } else {
                    // have to use for loop this way because of ConcurrentModificationException
                    for(int j = 0; j < entries.size(); j++) {
                        BarEntry entry = entries.get(j);
                        // check if there is already an entry on that date
                        if(entry.getX() == squatDate.getDate()) {
                            // get current value of day
                            float entryValue = entry.getY();
                            // set new value of day
                            entry.setY(entryValue + data);
                            entryAdded = true;
                            break;
                        }
                    }
                    if(!entryAdded) {
                        entries.add(new BarEntry(squatDate.getDate(), data));
                    }
                }
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Wiederholungen pro Tag");
        dataSet.setValueTextSize(12);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(currentData.equals(SquatData.REPETITIONS)) {
                    return String.valueOf((int) value);
                }
                else if (currentData.equals(SquatData.CALORIES)) {
                    return String.format("%.2f", value);
                }
                // shouldnt come this far
                return null;
            }
        });

        // set bar color
        if(currentData.equals(SquatData.REPETITIONS)) {
            dataSet.setColor(getResources().getColor(R.color.lightBlue));
            dataSet.setLabel("Wiederholungen pro Tag");
        }
        else if (currentData.equals(SquatData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Tag");
        }

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // set x axis min and max values
        barChart.getXAxis().setAxisMinimum(1);
        barChart.getXAxis().setAxisMaximum(c.getActualMaximum(Calendar.DAY_OF_MONTH));

        // get max value of entries to set as max value on y axis
        List<Float> entryValues = new ArrayList<>();
        for(BarEntry entry: entries) {
            entryValues.add(entry.getY());
        }
        float maxValue = Collections.max(entryValues);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisLeft().setAxisMaximum(maxValue + (float) (maxValue * 0.1));
        barChart.getAxisRight().setAxisMinimum(0);
        barChart.getAxisRight().setAxisMaximum(maxValue + (float) (maxValue * 0.1));

        // remove description label
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        //chart styling
        barChart.setNoDataText("Keine Workouts vorhanden.");
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisRight().setDrawAxisLine(false);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisLeft().setDrawLabels(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisLeft().setDrawGridLines(false);

        // refresh chart
        barChart.invalidate();
    }

    private void fillLinechart() {

        List<Entry> entries = new ArrayList<>();

        int fromIndex = 0;
        int toIndex = 0;

        // check for errors (ArrayIndexOutOfBounds etc)
        if(!allSquats.isEmpty()) {
            toIndex = allSquats.size();
            if(allSquats.size() - 7 > 0) {
                fromIndex = allSquats.size() - 7;
            }
        }

        // get last seven elements from allPushUps
        List<Squat> lastSevenSquats =
                allSquats.subList(fromIndex, toIndex);

        // make entries, counter is value on x axis ( 1 - 7)
        int counter = 1;
        for(Squat squat: lastSevenSquats) {

            float data = 0;
            if(currentData.equals(SquatData.REPETITIONS))
                data = squat.getRepeats();
            else if (currentData.equals(SquatData.CALORIES))
                data = (float) squat.getCalories();

            entries.add(new Entry(counter, data));
            counter++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Wiederholungen pro Workout");
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(currentData.equals(SquatData.REPETITIONS)) {
                    return String.valueOf((int) value);
                }
                else if (currentData.equals(SquatData.CALORIES)) {
                    return String.format("%.2f", value);
                }
                // shouldnt come this far
                return null;
            }
        });
        // set line color and label
        if(currentData.equals(SquatData.REPETITIONS)) {
            dataSet.setColor(getResources().getColor(R.color.lightBlue));
            dataSet.setLabel("Wiederholungen pro Workout");
        }
        else if (currentData.equals(SquatData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Workout");
        }
        dataSet.setLineWidth(3);
        dataSet.setValueTextSize(12);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // set x axis min and max values
        lineChart.getXAxis().setAxisMinimum(1);
        lineChart.getXAxis().setAxisMaximum(7);

        // chart styling
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawAxisLine(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        // remove description label
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        lineChart.setNoDataText("Keine Workouts vorhanden.");

        // refresh chart
        lineChart.invalidate();
    }

}
