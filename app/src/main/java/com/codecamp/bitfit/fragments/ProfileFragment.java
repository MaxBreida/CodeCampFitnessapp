package com.codecamp.bitfit.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.util.Constants;
import com.codecamp.bitfit.util.CustomEditText;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int GENDER_MALE_POSITION = 1;
    private static final int GENDER_FEMALE_POSITION = 2;

    // views
    private CustomEditText nameEditText;
    private EditText birthdayEditText;
    private CustomEditText heightEditText;
    private CustomEditText weightEditText;
    private Spinner genderSpinner;
    private User user;

    public static ProfileFragment getInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_action_bar_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initiate views
        nameEditText = getView().findViewById(R.id.edittext_profile_name);
        genderSpinner = getView().findViewById(R.id.spinner_profile_gender);
        birthdayEditText = getView().findViewById(R.id.edittext_profile_birthday);
        heightEditText = getView().findViewById(R.id.edittext_profile_height);
        weightEditText = getView().findViewById(R.id.edittext_profile_weight);

        // get User data from database
        user = DBQueryHelper.findUser();

        if(user != null) {
            // initialize values
            nameEditText.setText(user.getName());
            birthdayEditText.setText(Util.getDateAsString(user.getBirthday()));
            heightEditText.setText(String.valueOf(user.getSizeInCM()));
            weightEditText.setText(String.valueOf(user.getWeightInKG()));
            setupGenderPicker();
        }

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear focus of edit text to hide keyboard
                nameEditText.clearFocus();
                heightEditText.clearFocus();
                weightEditText.clearFocus();

                Date date = Util.getStringAsDate(birthdayEditText.getText().toString());
                assert date != null;
                final int mYear = date.getYear() + 1900;
                final int mMonth = date.getMonth();
                final int mDay = date.getDate();

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

                                Calendar c = Calendar.getInstance();
                                c.set(year, monthOfYear, dayOfMonth);
                                birthdayEditText.setText(Util.getDateAsString(c.getTime()));

                            }
                        }, mYear, mMonth, mDay);
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
                dpd.show();
            }
        });
    }

    private void setupGenderPicker() {
        List<String> genderList = new ArrayList<>();
        genderList.add(getString(R.string.choose_gender));
        genderList.add(getString(R.string.gender_male));
        genderList.add(getString(R.string.gender_female));

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.profile_gender_picker_text_view, genderList);
        genderSpinner.setAdapter(genderAdapter);

        if(user.isMale()) {
            genderSpinner.setSelection(GENDER_MALE_POSITION);
        } else if(user.isFemale()) {
            genderSpinner.setSelection(GENDER_FEMALE_POSITION);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity())
                .setActionBarTitle(getString(R.string.profile));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_save) {

            if(!checkInputs()) {
                return true;
            } else {
                user.setName(nameEditText.getText().toString());

                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
                try {
                    Date newDate = df.parse(birthdayEditText.getText().toString());
                    user.setBirthday(newDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                user.setSizeInCM(Integer.parseInt(heightEditText.getText().toString()));
                user.setWeightInKG(Double.parseDouble(weightEditText.getText().toString()));
                user.setGender(genderSpinner.getSelectedItem().toString());

                if(user.update()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.saved_successfully,
                            Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkInputs() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.check_these_information));
        int errorCounter = 0;

        if (nameEditText.getText().toString().isEmpty()
                || nameEditText.getText().toString().length() < 3) {
            sb.append(getString(R.string.your_name));
            errorCounter++;
        }
        if(genderSpinner.getSelectedItem().toString().equals(getString(R.string.choose_gender))) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_gender));
            errorCounter++;
        }
        if(weightEditText.getText().toString().isEmpty()) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_weight));
            errorCounter++;
        }
        if (heightEditText.getText().toString().isEmpty()) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_height));
            errorCounter++;
        }
        if(birthdayEditText.getText().toString().isEmpty()) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_age));
            errorCounter++;
        }

        if(errorCounter > 0) {
            Toast.makeText(getContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
