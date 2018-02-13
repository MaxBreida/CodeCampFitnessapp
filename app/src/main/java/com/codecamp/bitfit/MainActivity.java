package com.codecamp.bitfit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.codecamp.bitfit.fragments.HomeFragment;
import com.codecamp.bitfit.fragments.ProfileFragment;
import com.codecamp.bitfit.fragments.PushUpFragment;
import com.codecamp.bitfit.fragments.RunFragment;
import com.codecamp.bitfit.fragments.SquatFragment;
import com.codecamp.bitfit.intro.IntroActivity;
import com.raizlabs.android.dbflow.config.FlowManager;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // database init
        FlowManager.init(this);

        // start intro on first launch
        startActivity(new Intent(this, IntroActivity.class));

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

        // Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                // only select item if it wasn't selected before
                if(!wasSelected) {
                    // select the fragment
                    switch(position) {
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
                        default: break;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // fill action bar with buttons
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_statistics:
                // TODO start statistics activity
                return true;
            case R.id.action_share:
                // TODO start share intent
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
