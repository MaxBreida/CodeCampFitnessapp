package com.codecamp.bitfit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    }
}
