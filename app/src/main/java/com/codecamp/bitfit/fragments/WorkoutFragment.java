package com.codecamp.bitfit.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;


public class WorkoutFragment extends Fragment {

    // callbacks to activity
    WorkoutFragment.OnWorkoutInProgressListener callback;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        this.callback = (MainActivity) getActivity();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * tells the mainactivity if a workout is in progress or not
     */
    public interface OnWorkoutInProgressListener {
        void workoutInProgress(boolean inProgress);
        void setNavigationItem();
    }

    protected void setButtonDesign(boolean active, int color, int resId, FloatingActionButton button){
        button.setActivated(active);
        button.setBackgroundTintList(ColorStateList.valueOf(color));
        button.setImageResource(resId);
    }

    protected void setResumeButtonDesign(FloatingActionButton button){
        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
    }

    protected void moveButtonLeft(boolean go, final FloatingActionButton button){
        button.setClickable(false);
        button.animate().rotationBy((go) ? -360 : 360);
        button.animate().xBy(toDp((go) ? -50 : 50));
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setClickable(true);
            }
        }, 300);
    }

    protected void makeButtonAppear(boolean go, final FloatingActionButton button) {
        button.setClickable(false);
        button.animate().rotationBy((go) ? 360 : -360);
        button.animate().alpha((go) ? 1.0f : 0);
        button.animate().xBy(toDp((go) ? 50 : -50));
        button.postDelayed(new Runnable() {
            @Override
            public void run() {
                button.setClickable(true);
            }
        }, 300);
    }

    protected float toDp(float dp){
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()
        );
    }
}
