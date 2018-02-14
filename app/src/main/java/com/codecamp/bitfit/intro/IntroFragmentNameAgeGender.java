package com.codecamp.bitfit.intro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

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

    public EditText getNameEditText() {
        return nameEditText;
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
        setupListeners();
    }

    private void setupListeners() {
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                callback.onNameChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callback.onDayChanged(newVal);
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callback.onMonthChanged(newVal);
            }
        });

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callback.onYearChanged(newVal);
            }
        });

        genderPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onGenderChangedListener((String) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnNameBirthdayGenderChangedListener) getActivity();
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

    // callbacks to activity
    OnNameBirthdayGenderChangedListener callback;

    public interface OnNameBirthdayGenderChangedListener {
        void onNameChanged(String name);

        void onDayChanged(int day);

        void onMonthChanged(int month);

        void onYearChanged(int year);

        void onGenderChangedListener(String gender);
    }

}
