package com.codecamp.bitfit;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Witali Schmidt on 13.02.2018.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // database init
        FlowManager.init(this);
    }
}
