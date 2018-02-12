package com.codecamp.bitfit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MaxBreida on 12.02.18.
 */

public class BottomBarAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> fragments = new ArrayList<>();

    public BottomBarAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addFragment(Fragment fragment) {
        fragments.add(fragment);
    }
}
