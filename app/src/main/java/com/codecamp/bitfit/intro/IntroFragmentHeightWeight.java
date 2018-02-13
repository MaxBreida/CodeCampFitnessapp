package com.codecamp.bitfit.intro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.codecamp.bitfit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragmentHeightWeight extends Fragment {

    OnHeightWeightChangedListener callback;

    private EditText weightEditText;
    private EditText heigthEditText;

    public IntroFragmentHeightWeight() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initiate view stuff
        weightEditText = getView().findViewById(R.id.edittext_weight);
        heigthEditText = getView().findViewById(R.id.edittext_height);
        setupListeners();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_intro_fragment_height_weight, container, false);
    }

    private void setupListeners() {
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                callback.onWeightChangedListener(Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });

        heigthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                callback.onHeightChangedListener(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {
                // nothing
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnHeightWeightChangedListener) getActivity();
    }

    public interface OnHeightWeightChangedListener {
        void onHeightChangedListener(int height);

        void onWeightChangedListener(double weight);
    }
}
