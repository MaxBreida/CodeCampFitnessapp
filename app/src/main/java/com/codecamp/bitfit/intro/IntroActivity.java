package com.codecamp.bitfit.intro;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.codecamp.bitfit.R;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by MaxBreida on 12.02.18.
 */

public class IntroActivity extends AppIntro
                           implements IntroFragmentNameAgeGender.OnNameBirthdayGenderChangedListener,
                           IntroFragmentHeightWeight.OnHeightWeightChangedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(new IntroFragmentStart());
        addSlide(new IntroFragmentNameAgeGender());
        addSlide(new IntroFragmentHeightWeight());
        addSlide(new IntroFragmentFinish());

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // TODO persist user data HERE in database
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onNameChanged(String name) {
        // TODO update name in user object
    }

    @Override
    public void onDayChanged(int day) {
        // TODO update day in user object
    }

    @Override
    public void onMonthChanged(int month) {
        // TODO update month in user object
    }

    @Override
    public void onYearChanged(int year) {
        // TODO update year in user object
    }


    @Override
    public void onGenderChangedListener(String gender) {
        // TODO update gender in user object
    }

    @Override
    public void onHeightChangedListener(int height) {
        // TODO update height in user object
    }

    @Override
    public void onWeightChangedListner(double weight) {
        // TODO update weight in user object
    }
}
