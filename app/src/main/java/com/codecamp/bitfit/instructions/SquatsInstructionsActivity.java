package com.codecamp.bitfit.instructions;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codecamp.bitfit.R;

public class SquatsInstructionsActivity extends AppCompatActivity {

    //As there is no interactifity in this Activity (Screen) there is just the
    //onCreate method needed
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squats_instructions);
    }

}
