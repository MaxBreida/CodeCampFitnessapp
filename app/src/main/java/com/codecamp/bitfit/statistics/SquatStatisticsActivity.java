package com.codecamp.bitfit.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

public class SquatStatisticsActivity extends RepetitionsStatisticsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_squat_statistics);

        if(!DBQueryHelper.findAllSquats().isEmpty()) {
            allWorkouts = DBQueryHelper.findAllSquats();

            barChart = findViewById(R.id.last_month_chart);
            lineChart = findViewById(R.id.last_seven_workouts_chart);
            currentBarChartData = WorkoutData.REPETITIONS;
            currentLineChartData = WorkoutData.REPETITIONS;

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
}
