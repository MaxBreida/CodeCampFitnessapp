package com.codecamp.bitfit;

/**
 * Created by maxib on 10.03.2018.
 */

/**
 * this interface tells our workoutfragments that the user wants to stop the workout and change the
 * fragment through the bottom navigation bar
 */
public interface OnDialogInteractionListener {
    void stopWorkoutOnFragmentChange();
}
