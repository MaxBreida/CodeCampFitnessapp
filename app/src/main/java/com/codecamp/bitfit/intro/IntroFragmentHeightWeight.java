package com.codecamp.bitfit.intro;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codecamp.bitfit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IntroFragmentHeightWeight extends Fragment {

    OnHeightWeightChangedListener callback;

    public IntroFragmentHeightWeight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_intro_fragment_height_weight, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnHeightWeightChangedListener) getActivity();
    }

    public interface OnHeightWeightChangedListener {
        public void onHeightChangedListener(int height);
        public void onWeightChangedListner(double weight);
    }
}
