package com.codecamp.bitfit.intro;

import android.content.Intent;
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
    private Date birthday;
    private String gender;
    private int height;
    private double weight;
    private boolean shouldAllowBack = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new IntroFragmentStart());
        addSlide(new IntroFragmentNameAgeGender());
        addSlide(new IntroFragmentHeightWeight());
        addSlide(new IntroFragmentFinish());

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

        if(!checkInputs()) {
            return;
        }

        // User initialize
        user.setId(UUID.randomUUID());
        user.setName(name);
        user.setWeight(weight);
        user.setSize(height);
        user.setGender(gender);
        user.setBirthday(birthday);

        //Save user to database
        user.save();

        Intent intent = new Intent();
        intent.putExtra("result", 1);
        setResult(RESULT_OK, intent);

        // close activity after saving the user object
        finish();
    }

    private boolean checkInputs() {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.check_these_information));
        int errorCounter = 0;

        if (name == null || name.length() < 3) {
            sb.append(getString(R.string.your_name));
            errorCounter++;
        }
        if(gender.equals(getString(R.string.choose_gender))) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_gender));
            errorCounter++;
        }
        if(weight == 0.0) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_weight));
            errorCounter++;
        }
        if (height == 0) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_height));
            errorCounter++;
        }
        if(birthday == null) {
            if(errorCounter > 0) {
                sb.append(", ");
            }
            sb.append(getString(R.string.your_age));
            errorCounter++;
        }

        if(errorCounter > 0) {
            Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
        if(newFragment instanceof IntroFragmentStart) {
            shouldAllowBack = false;
        } else {
            shouldAllowBack = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }

    @Override
    public void onNameChanged(String name) {
        this.name = name;
    }

    @Override
    public void onBirthdayChangedListener(Date birthday) {
        this.birthday = birthday;
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
