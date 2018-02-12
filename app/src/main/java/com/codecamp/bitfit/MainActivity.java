package com.codecamp.bitfit;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBottomNavigation();
    }

    private void initBottomNavigation() {
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        // create items
        AHBottomNavigationItem home = new AHBottomNavigationItem(getString(R.string.home_tab),
                MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.HOME).build());
        AHBottomNavigationItem pushUps = new AHBottomNavigationItem(getString(R.string.push_up_tab),
                MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.LIGHTBULB).build());
        AHBottomNavigationItem squats = new AHBottomNavigationItem(getString(R.string.squat_tab),
                MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.BABY).build());
        AHBottomNavigationItem run = new AHBottomNavigationItem(getString(R.string.run_tab),
                MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.RUN).build());
        AHBottomNavigationItem profile = new AHBottomNavigationItem(getString(R.string.profile_tab),
                MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.FACE_PROFILE).build());

        bottomNavigation.addItem(home);
        bottomNavigation.addItem(pushUps);
        bottomNavigation.addItem(squats);
        bottomNavigation.addItem(run);
        bottomNavigation.addItem(profile);

        bottomNavigation.setCurrentItem(0);

        BottomBarViewPager viewPager = new BottomBarViewPager(this, null);
        viewPager.setPagingEnabled(false);

        BottomBarAdapter adapter = new BottomBarAdapter(getSupportFragmentManager());

        adapter.addFragment(HomeFragment.getInstance());

        viewPager.setAdapter(adapter);

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // Do something cool here...
                return true;
            }
        });

    }
}
