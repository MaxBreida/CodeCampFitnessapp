package com.codecamp.bitfit.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.codecamp.bitfit.MainActivity;
import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private static final int GENDER_MALE_POSITION = 0;
    private static final int GENDER_FEMALE_POSITION = 1;

    // views
    private EditText nameEditText;
    private Spinner genderSpinner;
    private EditText birthdayEditText;
    private EditText heightEditText;
    private EditText weightEditText;
    private Button saveButton;
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
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
        saveButton = getView().findViewById(R.id.button_profile_save);

        // get User data from database
        user = DBQueryHelper.findUser();

        if(user != null) {
            // initialize values
            nameEditText.setHint(user.getName());
            birthdayEditText.setHint(user.getBirthday().toString());
            heightEditText.setHint(String.valueOf(user.getSize()));
            weightEditText.setHint(String.valueOf(user.getWeight()));
            setupGenderPicker();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!nameEditText.getText().toString().isEmpty()) {
                        user.setName(nameEditText.getText().toString());
                    }

                    if(!birthdayEditText.getText().toString().isEmpty()) {
                       //TODO  user.setBirthday(birthdayEditText.getText().toString());
                    }

                    user.setGender(genderSpinner.getSelectedItem().toString());

                    if(!heightEditText.getText().toString().isEmpty()) {
                        user.setSize(Integer.parseInt(heightEditText.getText().toString()));
                    }

                    if(!weightEditText.getText().toString().isEmpty()) {
                        user.setWeight(Double.parseDouble(weightEditText.getText().toString()));
                    }

                    user.update();

                    // TODO show toast that user got successfully saved
                }
            });
        }
    }

    private void setupGenderPicker() {
        List<String> genderList = new ArrayList<>();
        genderList.add("männlich");
        genderList.add("weiblich");

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getContext(), R.layout.gender_picker_text_view, genderList);
        genderSpinner.setAdapter(genderAdapter);

        if(user.getGender().equals("männlich")) {
            genderSpinner.setSelection(GENDER_MALE_POSITION);
        } else {
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
}
