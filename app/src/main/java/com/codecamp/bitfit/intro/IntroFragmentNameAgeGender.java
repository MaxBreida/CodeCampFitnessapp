package com.codecamp.bitfit.intro;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.codecamp.bitfit.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragmentNameAgeGender extends Fragment {

    private EditText nameEditText;
    private NumberPicker dayPicker;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;
    private Spinner genderPicker;

    public IntroFragmentNameAgeGender() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_fragment_name_age_gender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initiate view stuff
        nameEditText = getView().findViewById(R.id.edittext_name);
        dayPicker = getView().findViewById(R.id.number_picker_day);
        monthPicker = getView().findViewById(R.id.number_picker_month);
        yearPicker = getView().findViewById(R.id.number_picker_year);
        genderPicker = getView().findViewById(R.id.genderpicker);

        setupBirthdayPickers();
        setupGenderPicker();
    }

    private void setupBirthdayPickers() {
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);

        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
    }

    private void setupGenderPicker() {
        List<String> genderList = new ArrayList<>();
        genderList.add("männlich");
        genderList.add("weiblich");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.gender_picker_text_view, genderList);
        genderPicker.setAdapter(genderAdapter);
    }
}
