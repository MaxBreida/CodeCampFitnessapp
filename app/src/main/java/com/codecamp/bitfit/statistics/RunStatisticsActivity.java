package com.codecamp.bitfit.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.Run;
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

public class RunStatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
    private LineChart lineChart;
    private List<Run> allRuns;
    private BarChartData currentBarChartData;
    private LineChartData currentLineChartData;

    private enum BarChartData{
        DISTANCE,
        CALORIES,
        DURATION;

        /**
         * https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type#17006263
         * @return next value in enum
         */
        public BarChartData next()
        {
            return values()[(ordinal()+1) % values().length];
        }
    }

    private enum LineChartData{
        DISTANCE,
        CALORIES,
        SPEED,
        DURATION;

        /**
         * https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type#17006263
         * @return next value in enum
         */
        public LineChartData next()
        {
            return values()[(ordinal()+1) % values().length];
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_run_statistics);

        if(DBQueryHelper.findAllRuns().isEmpty()){
            fillTableWithDummies();    //Fill table with dummy runs if the run database is empty (for testing purpose)
        }

        if (!DBQueryHelper.findAllPushUps().isEmpty()) {
            allRuns = DBQueryHelper.findAllRuns();

            barChart = findViewById(R.id.last_month_chart);
            lineChart = findViewById(R.id.last_seven_workouts_chart);
            currentBarChartData = BarChartData.DISTANCE;
            currentLineChartData = LineChartData.DISTANCE;

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
                    currentBarChartData = currentBarChartData.next();
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
                    currentLineChartData = currentLineChartData.next();
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

            Button historyButton = findViewById(R.id.button_run_statistics_history);
            historyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), RunHistoryActivity.class));
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

        for(int i=0; i<allRuns.size(); i++) {
            Run workout = allRuns.get(i);
            Date workoutDate = Util.getStringAsDate(workout.getCurrentDate());
            boolean entryAdded = false;

            float data = 0;
            if(currentBarChartData.equals(BarChartData.DISTANCE))
                data = workout.getDistanceInKm();
            else if (currentBarChartData.equals(BarChartData.CALORIES))
                data = (float) workout.getCalories();
            else if (currentBarChartData.equals(BarChartData.DURATION))
                data = (float) workout.getDurationInMillis();

            if(workoutDate.getMonth() == currentMonth) {
                // if entries is empty add one entry
                if(entries.isEmpty()) {
                    entries.add(new BarEntry(workoutDate.getDate(), data));
                } else {
                    // have to use for loop this way because of ConcurrentModificationException
                    for(int j = 0; j < entries.size(); j++) {
                        BarEntry entry = entries.get(j);
                        // check if there is already an entry on that date
                        if(entry.getX() == workoutDate.getDate()) {
                            // get current value of day
                            float entryValue = entry.getY();
                            // set new value of day
                            entry.setY(entryValue + data);
                            entryAdded = true;
                            break;
                        }
                    }

                    if(!entryAdded) {
                        entries.add(new BarEntry(workoutDate.getDate(), data));
                    }
                }
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Distanz pro Tag");
        dataSet.setValueTextSize(12);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(currentBarChartData.equals(BarChartData.DURATION)) {
                    return Util.getMillisAsTimeString((long) value);
                }
                return String.format("%.2f", value);
            }
        });

        // set bar color
        if(currentBarChartData.equals(BarChartData.DISTANCE)) {
            dataSet.setColor(getResources().getColor(R.color.purple));
            dataSet.setLabel("Distanz pro Tag in km");
        }
        else if (currentBarChartData.equals(BarChartData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Tag");
        }
        else if (currentBarChartData.equals(BarChartData.DURATION)) {
            dataSet.setColor(getResources().getColor(R.color.lightBlue));
            dataSet.setLabel("Gesamte Workoutdauer pro Tag in Minuten");
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

    protected void fillLinechart() {

        List<Entry> entries = new ArrayList<>();

        int fromIndex = 0;
        int toIndex = 0;

        // check for errors (ArrayIndexOutOfBounds etc)
        if(!allRuns.isEmpty()) {
            toIndex = allRuns.size();
            if(allRuns.size() - 7 > 0) {
                fromIndex = allRuns.size() - 7;
            }
        }

        // get last seven elements from allPushUps
        List<Run> lastSevenWorkouts =
                allRuns.subList(fromIndex, toIndex);

        // make entries, counter is value on x axis ( 1 - 7)
        int counter = 1;
        for(Run workout: lastSevenWorkouts) {

            float data = 0;
            if(currentLineChartData.equals(LineChartData.DISTANCE))
                data = workout.getDistanceInKm();
            else if (currentLineChartData.equals(LineChartData.CALORIES))
                data = (float) workout.getCalories();
            else if (currentLineChartData.equals(LineChartData.SPEED))
                data = (float) workout.getAverageKmh();
            else if (currentLineChartData.equals(LineChartData.DURATION))
                data = (float) workout.getDurationInMillis();

            entries.add(new Entry(counter, data));
            counter++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Wiederholungen pro Workout");
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (currentLineChartData.equals(LineChartData.DURATION)) {
                    return Util.getMillisAsTimeString((long) value);
                }

                return String.format("%.2f", value);
            }
        });
        // set line color and label
        if(currentLineChartData.equals(LineChartData.DISTANCE)) {
            dataSet.setColor(getResources().getColor(R.color.purple));
            dataSet.setLabel("Distanz pro Workout in km");
        }
        else if (currentLineChartData.equals(LineChartData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Workout");
        }
        else if (currentLineChartData.equals(LineChartData.SPEED)) {
            dataSet.setColor(getResources().getColor(R.color.green));
            dataSet.setLabel("Durchschnittliche Geschwindigkeit pro Workout in km");
        }
        else if (currentLineChartData.equals(LineChartData.DURATION)) {
            dataSet.setColor(getResources().getColor(R.color.lightBlue));
            dataSet.setLabel("Dauer pro Workout in Minuten");
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

    protected void fillTableWithDummies(){
        createDummyRun(2000, 15*60);
        createDummyRun(4000, 30*60);
        createDummyRun(11000, 100*60);
        createDummyRun(500.555f, 30*60);
    }

    protected  void createDummyRun(float distance, long durationAsSeconds){
        Run dummyRun = new Run();

        dummyRun.setRandomId();
        dummyRun.setCurrentDate();
        dummyRun.setDurationInMillis(1000*durationAsSeconds);
        dummyRun.setCalories(100);
        dummyRun.setDistanceInMeters(distance);

        dummyRun.save();
    }

}
