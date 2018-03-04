package com.codecamp.bitfit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.codecamp.bitfit.database.User;
import com.codecamp.bitfit.fragments.HomeFragment;
import com.codecamp.bitfit.fragments.ProfileFragment;
import com.codecamp.bitfit.fragments.PushUpFragment;
import com.codecamp.bitfit.fragments.RunFragment;
import com.codecamp.bitfit.fragments.SquatFragment;
import com.codecamp.bitfit.intro.IntroActivity;
import com.codecamp.bitfit.util.DBQueryHelper;

public class MainActivity extends AppCompatActivity {

    private static final int requestCode = 871228713;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load user if initialized
        User findUser = DBQueryHelper.findUser();

        // If user is initialized, then skip intro
        if (findUser == null) {
            // start intro on first launch, after that call initBottomNavigation in onActivityResult
            startActivityForResult(new Intent(this, IntroActivity.class), requestCode);
        } else {
            // user already in database, so we can call initBottomNavigation directly
            initBottomNavigation();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_OK) {
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
        bottomNavigation.setOnTabSelectedListener(tabSelectLstener);

        // set start tab
        bottomNavigation.setCurrentItem(0);
        tabSelectLstener.onTabSelected(0,false);
    }

    AHBottomNavigation.OnTabSelectedListener tabSelectLstener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, pushUpFragment).commit();
                        break;
                    case 2:
                        SquatFragment squatFragment = SquatFragment.getInstance();
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, squatFragment).commit();
                        break;
                    case 3:
                        RunFragment runFragment = RunFragment.getInstance();
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
            return true;
        }
    };

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(permissions[0].equals("android.permission.ACCESS_FINE_LOCATION")) {
            if (grantResults[0] == -1) { // access location permission denied
                bottomNavigation.setCurrentItem(0); // return to home screen
                tabSelectLstener.onTabSelected(0,false);
                // TODO: tell this greedy boy that "no location permission = no run workouts"!
            }
            else { // access location permission granted
                /* refresh run fragment, since asking for permissions happens asynchronously,
                *  hence, this is the simplest way of restoring all functions to their initial state.
                *  Furthermore, the user couldn't do anything without location permissions anyways,
                *  so nothing gets discarded and the user shouldn't even notice a change in the app. */
                tabSelectLstener.onTabSelected(3,false);
            }
        }
    }
}
