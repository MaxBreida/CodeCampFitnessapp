package com.codecamp.bitfit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.fragments.HomeFragment;
import com.codecamp.bitfit.fragments.ProfileFragment;
import com.codecamp.bitfit.fragments.PushUpFragment;
import com.codecamp.bitfit.fragments.RunFragment;
import com.codecamp.bitfit.fragments.SquatFragment;
import com.codecamp.bitfit.fragments.WorkoutFragment;
import com.codecamp.bitfit.intro.IntroActivity;
import com.codecamp.bitfit.util.DBQueryHelper;
import com.codecamp.bitfit.util.OnDialogInteractionListener;

public class MainActivity extends AppCompatActivity implements WorkoutFragment.OnWorkoutInProgressListener{

    private static final int REQUEST_CODE = 8712;
    private boolean workoutInProgress = false;

    private boolean mWasSelected;
    private int mPosition = -1;

    private OnDialogInteractionListener callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load user if initialized
        User findUser = DBQueryHelper.findUser();

        // If user is initialized, then skip intro
        if (findUser == null) {
            // start intro on first launch, after that call initBottomNavigation in onActivityResult
            startActivityForResult(new Intent(this, IntroActivity.class), REQUEST_CODE);
        } else {
            // user already in database, so we can call initBottomNavigation directly
            initBottomNavigation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE) {
            initBottomNavigation();
        }
    }

    AHBottomNavigation bottomNavigation;

    private void initBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.black));

        // create items
        AHBottomNavigationItem home = new AHBottomNavigationItem(getString(R.string.home),
                getDrawable(R.drawable.icon_home_black));
        AHBottomNavigationItem pushUps = new AHBottomNavigationItem(getString(R.string.pushups),
                getDrawable(R.drawable.icon_pushup_black));
        AHBottomNavigationItem squats = new AHBottomNavigationItem(getString(R.string.squats),
                getDrawable(R.drawable.icon_squat_black));
        AHBottomNavigationItem run = new AHBottomNavigationItem(getString(R.string.run),
                getDrawable(R.drawable.icon_run_black));
        AHBottomNavigationItem profile = new AHBottomNavigationItem(getString(R.string.profile),
                getDrawable(R.drawable.icon_profile_black));

        bottomNavigation.addItem(home);
        bottomNavigation.addItem(pushUps);
        bottomNavigation.addItem(squats);
        bottomNavigation.addItem(run);
        bottomNavigation.addItem(profile);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(tabSelectListener);

        // set start tab
        bottomNavigation.setCurrentItem(0);
        tabSelectListener.onTabSelected(0,false);
    }

    AHBottomNavigation.OnTabSelectedListener tabSelectListener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(final int position, final boolean wasSelected) {
            if(!wasSelected && workoutInProgress) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Workout noch nicht beendet!")
                        .setMessage("Du hast deinen Workout noch nicht beendet. Willst du ihn jetzt beenden und speichern?")
                        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // user wants to stop workout and save, so lets do this here
                                callback.stopWorkoutOnFragmentChange();

                                // needed for callback of workoutCompleteDialog
                                mWasSelected = wasSelected;
                                mPosition = position;

                                // cancel dialog
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // just dismiss this dialog
                                dialog.cancel();
                            }
                        }).create();

                dialog.show();
                return false;
            }

            changeFragment(position, wasSelected);

            return true;
        }
    };

    private void changeFragment(int position, boolean wasSelected) {
        // only select item if it wasn't selected before
        if (!wasSelected) {
            // select the fragment
            switch (position) {
                case 0:
                    HomeFragment homeFragment = HomeFragment.getInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, homeFragment).commit();
                    break;
                case 1:
                    PushUpFragment pushUpFragment = PushUpFragment.getInstance();
                    callback = pushUpFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, pushUpFragment).commit();
                    break;
                case 2:
                    SquatFragment squatFragment = SquatFragment.getInstance();
                    callback = squatFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, squatFragment).commit();
                    break;
                case 3:
                    RunFragment runFragment = RunFragment.getInstance();
                    callback = runFragment;
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, runFragment).commit();
                    break;
                case 4:
                    ProfileFragment profileFragment = ProfileFragment.getInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.content_main, profileFragment).commit();
                    break;
                default:
                    break;
            }
        }
    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void sendToTab(int pos, boolean selected){
        bottomNavigation.setCurrentItem(pos);
        changeFragment(pos, selected);
    }

    public void sendToTab(int pos){
        sendToTab(pos, false);
    }

    @Override
    public void workoutInProgress(boolean inProgress) {
        this.workoutInProgress = inProgress;
    }

    @Override
    public void setNavigationItem() {
        if(!mWasSelected) {
            workoutInProgress = false;

            if (mPosition != -1) {
                // change fragment after saving workout
                sendToTab(mPosition, mWasSelected);
            } else sendToTab(0);

            mPosition = -1;
        }
    }
}
