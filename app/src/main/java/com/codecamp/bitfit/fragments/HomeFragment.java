package com.codecamp.bitfit.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.PushUps;
import com.codecamp.bitfit.database.PushUps_Table;
import com.codecamp.bitfit.util.Util;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.List;

import static com.raizlabs.android.dbflow.sql.language.Method.count;
import static com.raizlabs.android.dbflow.sql.language.Method.max;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // highscore pushups views
    private TextView highscorePushupsDuration;
    private TextView highscorePushupsCalories;
    private TextView highscorePushupsPPM;
    private TextView highscorePushupsRepeats;
    private TextView highscorePushupsDate;

    public static HomeFragment getInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        highscorePushups();
    }

    private void highscorePushups() {
        // initialize highscore pushups textviews
        highscorePushupsCalories = getView().findViewById(R.id.textview_highscore_pushup_calories);
        highscorePushupsDate = getView().findViewById(R.id.textview_highscore_pushup_date);
        highscorePushupsDuration = getView().findViewById(R.id.textview_highscore_pushup_duration);
        highscorePushupsPPM = getView().findViewById(R.id.textview_highscore_pushup_ppm);
        highscorePushupsRepeats = getView().findViewById(R.id.textview_highscore_pushup_repeats);

        // find pushup highscore
        PushUps highscorePushups = Util.findHighscorePushup();

        // set text in textviews
        highscorePushupsCalories.setText(
                String.format("Verbrauchte Kalorien: %s", String.valueOf(highscorePushups.getCalories())));
        highscorePushupsRepeats.setText(
                String.format("Wiederholungen: %s", String.valueOf(highscorePushups.getRepeats())));
        highscorePushupsPPM.setText(
                String.format("PushUps/min: %s", String.valueOf(highscorePushups.getPushPerMin())));
        highscorePushupsDate.setText(
                String.format("Datum: %s", highscorePushups.getCurrentDate()));
        highscorePushupsDuration.setText(
                String.format("Dauer: %s", Util.getMillisAsTimeString(highscorePushups.getDuration())));
    }
}
