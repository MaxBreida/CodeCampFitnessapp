package com.codecamp.bitfit;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load user if initialized
        User findUser = DBQueryHelper.findUser();

        // If user is initialized, then skip intro
        if (findUser == null) {
            // start intro on first launch
            startActivity(new Intent(this, IntroActivity.class));
        }

        initBottomNavigation();
    }

    private void initBottomNavigation() {
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);
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
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
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
        });

        // set start tab
        bottomNavigation.setCurrentItem(0);
        HomeFragment homeFragment = HomeFragment.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, homeFragment).commit();
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
