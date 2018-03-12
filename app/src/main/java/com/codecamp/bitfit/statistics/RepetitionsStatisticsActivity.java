package com.codecamp.bitfit.statistics;

import android.support.v7.app.AppCompatActivity;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.WorkoutWithRepetitions;
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
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by maxib on 11.03.2018.
 */

/**
 * parent class for squat and pushup statistics to reduce code repetition
 */
public class RepetitionsStatisticsActivity extends AppCompatActivity {
    protected BarChart barChart;
    protected LineChart lineChart;
    protected WorkoutData currentBarChartData;
    protected WorkoutData currentLineChartData;
    protected List<? extends WorkoutWithRepetitions> allWorkouts;

    protected enum WorkoutData{
        REPETITIONS,
        CALORIES,
        DURATION;

        /**
         * https://stackoverflow.com/questions/17006239/whats-the-best-way-to-implement-next-and-previous-on-an-enum-type#17006263
         * @return next value in enum
         */
        public WorkoutData next()
        {
            return values()[(ordinal()+1) % values().length];
        }
    }

    protected void fillBarchart() {
        List<BarEntry> entries = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH);

        for(int i=0; i<allWorkouts.size(); i++) {
            WorkoutWithRepetitions workout = allWorkouts.get(i);
            Date workoutDate = Util.getStringAsDate(workout.getCurrentDate());
            boolean entryAdded = false;

            float data = 0;
            if(currentBarChartData.equals(WorkoutData.REPETITIONS))
                data = workout.getRepeats();
            else if (currentBarChartData.equals(WorkoutData.CALORIES))
                data = (float) workout.getCalories();
            else if (currentBarChartData.equals(WorkoutData.DURATION))
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

        BarDataSet dataSet = new BarDataSet(entries, "Wiederholungen pro Tag");
        dataSet.setValueTextSize(12);
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(currentBarChartData.equals(WorkoutData.REPETITIONS)) {
                    return String.valueOf((int) value);
                }
                else if (currentBarChartData.equals(WorkoutData.CALORIES)) {
                    return String.format("%.2f", value);
                }
                else if (currentBarChartData.equals(WorkoutData.DURATION)) {
                    return Util.getMillisAsTimeString((long) value);
                }
                // shouldnt come this far
                return null;
            }
        });

        // set bar color
        if(currentBarChartData.equals(WorkoutData.REPETITIONS)) {
            dataSet.setColor(getResources().getColor(R.color.colorPrimary));
            dataSet.setLabel("Wiederholungen pro Tag");
        }
        else if (currentBarChartData.equals(WorkoutData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Tag in kcal");
        }
        else if (currentBarChartData.equals(WorkoutData.DURATION)) {
            dataSet.setColor(getResources().getColor(R.color.lightBlue));
            dataSet.setLabel("Dauer pro Tag in Minuten");
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
        if(!allWorkouts.isEmpty()) {
            toIndex = allWorkouts.size();
            if(allWorkouts.size() - 7 > 0) {
                fromIndex = allWorkouts.size() - 7;
            }
        }

        // get last seven elements from allPushUps
        List<? extends WorkoutWithRepetitions> lastSevenWorkouts =
                allWorkouts.subList(fromIndex, toIndex);

        // make entries, counter is value on x axis ( 1 - 7)
        int counter = 1;
        for(WorkoutWithRepetitions workout: lastSevenWorkouts) {

            float data = 0;
            if(currentLineChartData.equals(WorkoutData.REPETITIONS))
                data = workout.getRepeats();
            else if (currentLineChartData.equals(WorkoutData.CALORIES))
                data = (float) workout.getCalories();
            else if (currentLineChartData.equals(WorkoutData.DURATION))
                data = (float) workout.getDurationInMillis();


            entries.add(new Entry(counter, data));
            counter++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Wiederholungen pro Workout");
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if(currentLineChartData.equals(WorkoutData.REPETITIONS)) {
                    return String.valueOf((int) value);
                }
                else if (currentLineChartData.equals(WorkoutData.CALORIES)) {
                    return String.format("%.2f", value);
                }
                else if (currentLineChartData.equals(WorkoutData.DURATION)) {
                    return Util.getMillisAsTimeString((long) value);
                }
                // shouldnt come this far
                return null;
            }
        });
        // set line color and label
        if(currentLineChartData.equals(WorkoutData.REPETITIONS)) {
            dataSet.setColor(getResources().getColor(R.color.colorPrimary));
            dataSet.setLabel("Wiederholungen pro Workout");
        }
        else if (currentLineChartData.equals(WorkoutData.CALORIES)) {
            dataSet.setColor(getResources().getColor(R.color.orange));
            dataSet.setLabel("Verbrauchte Kalorien pro Workout in kcal");
        }
        else if (currentLineChartData.equals(WorkoutData.DURATION)) {
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

}
