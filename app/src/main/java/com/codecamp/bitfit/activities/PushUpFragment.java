package com.codecamp.bitfit.activities;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.codecamp.bitfit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PushUpFragment extends Fragment {

    private Button finishButton;
    private Button pushUpButton;
    private int count;

    public static PushUpFragment getInstance() {
        PushUpFragment fragment = new PushUpFragment();

        return fragment;
    }

    public PushUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_push_up, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        finishButton = getView().findViewById(R.id.button_pushup_quit);
        pushUpButton = getView().findViewById(R.id.button_pushup);

        // set button to start state
        pushUpButton.setText("Start");
        count = 0;

        pushUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show quit button if we started push ups
                if(finishButton.getVisibility() == View.INVISIBLE) {
                    finishButton.setVisibility(View.VISIBLE);
                }
                // increment count and set text
                count++;
                pushUpButton.setText("" + count);
            }
        });

        // set finish button invisible
        finishButton.setVisibility(View.INVISIBLE);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add count to database
            }
        });
    }
}
