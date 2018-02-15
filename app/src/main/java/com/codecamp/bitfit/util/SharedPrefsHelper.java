package com.codecamp.bitfit.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by MaxBreida on 15.02.18.
 */

public class SharedPrefsHelper {
    private static final String PREFSFILENAME = "codecampbitfit";
    private static final String LAST_ACTIVITY_TYPE = "LAST_ACTIVITY_TYPE";
    private static final String LAST_ACTIVITY_UUID = "LAST_ACTIVITY_UUID";

    private SharedPreferences sharedPreferences;

    public SharedPrefsHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFSFILENAME, Context.MODE_PRIVATE);
    }

    /**
     * save last activity in shared prefs
     * @param type of the activity (run, pushups, squats)
     * @param uuid of the last activity to query from database
     */
    public void setLastActivity(String type, UUID uuid) {
        this.sharedPreferences.edit().putString(LAST_ACTIVITY_TYPE, type).apply();
        this.sharedPreferences.edit().putString(LAST_ACTIVITY_UUID, uuid.toString()).apply();
    }
    
    public String getLastActivityType() {
        return this.sharedPreferences.getString(LAST_ACTIVITY_TYPE, "");
    }

    public UUID getLastActivityUuid() {
        return UUID.fromString(this.sharedPreferences.getString(LAST_ACTIVITY_UUID, ""));
    }
}
