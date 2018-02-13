package com.codecamp.bitfit.intro;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.codecamp.bitfit.R;
import com.codecamp.bitfit.database.User;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.Date;
import java.util.UUID;

/**
 * Created by MaxBreida on 12.02.18.
 */

public class IntroActivity extends AppIntro
        implements IntroFragmentNameAgeGender.OnNameBirthdayGenderChangedListener,
        IntroFragmentHeightWeight.OnHeightWeightChangedListener {
    private User user = new User();
    private String name;
    private int day;
    private int month;
    private int year;
    private String gender;
    private int height;
    private double weight;

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

        user.setId(UUID.randomUUID());
        // TODO comment out
        // Check user input, for missing or incorrect entries
       /* if (name == null || name.length() < 1 || weight == 0.0 || height == 0) {
            Context context = getApplicationContext();
            CharSequence text = "Prüfen auf fehlende oder fehlerhafte Eingaben!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
*/
        user.setName(name);
        user.setWeight(weight);
        user.setSize(height);

        user.setGender(gender);

        // If not select the Date, set default
        if (day != 0 && month != 0) {
            user.setBirthday(new Date(year, month, day));
        } else {
            user.setBirthday(new Date(0, 0, 1));
        }

        user.getName();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    @Override
    public void onNameChanged(String name) {
        this.name = name;
    }

    @Override
    public void onDayChanged(int day) {
        this.day = day;
    }

    @Override
    public void onMonthChanged(int month) {
        this.month = month;
    }

    @Override
    public void onYearChanged(int year) {
        this.year = year;
    }


    @Override
    public void onGenderChangedListener(String gender) {
        this.gender = gender;
    }

    @Override
    public void onHeightChangedListener(int height) {
        this.height = height;
    }

    @Override
    public void onWeightChangedListener(double weight) {
        this.weight = weight;
    }
}
