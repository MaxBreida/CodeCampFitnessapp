package com.codecamp.bitfit.intro;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.util.CustomEditText;
import com.codecamp.bitfit.util.NoNumbersInputFilter;
import com.codecamp.bitfit.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragmentNameAgeGender extends Fragment {

    private CustomEditText nameEditText;
    private EditText birthdayEditText;
    private Spinner genderPicker;
    private int mYear;
    private int mMonth;
    private int mDay;

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
        birthdayEditText = getView().findViewById(R.id.edittext_birtday);
        genderPicker = getView().findViewById(R.id.genderpicker);

        setupGenderPicker();
        setupListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupGenderPicker() {
        List<String> genderList = new ArrayList<>();
        genderList.add(getString(R.string.choose_gender));
        genderList.add("männlich");
        genderList.add("weiblich");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.intro_gender_picker_text_view, genderList);
        genderPicker.setAdapter(genderAdapter);
    }

    private void setupListeners() {
        nameEditText.setFilters(new InputFilter[] {new NoNumbersInputFilter()});
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

        genderPicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                callback.onGenderChangedListener((String) parent.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // will use default value anyways, so nothing to do here
            }
        });

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear focus of name text to hide keyboard
                nameEditText.clearFocus();

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox

                                if (year < mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (monthOfYear < mMonth && year == mYear)
                                    view.updateDate(mYear,mMonth,mDay);

                                if (dayOfMonth < mDay && year == mYear && monthOfYear == mMonth)
                                    view.updateDate(mYear,mMonth,mDay);

                                c.set(year, monthOfYear, dayOfMonth);
                                callback.onBirthdayChangedListener(c.getTime());
                                birthdayEditText.setText(Util.getDateAsString(c.getTime()));

                            }
                        }, mYear, mMonth, mDay);
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callback = (OnNameBirthdayGenderChangedListener) getActivity();
    }

    // callbacks to activity
    OnNameBirthdayGenderChangedListener callback;

    public interface OnNameBirthdayGenderChangedListener {
        void onNameChanged(String name);

        void onBirthdayChangedListener(Date birthday);

        void onGenderChangedListener(String gender);
    }

}
