package com.ka.billingsystem.services;

import android.app.Service;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.ka.billingsystem.database.DataBaseHandler;


public class LogoutService extends Service {
    private DataBaseHandler db = new DataBaseHandler(this);
    private static final String SHARED_PREFS = "shared_prefs";
    private static final String USER_KEY = "user_key";

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        performLogout();
    }

    /**
     * Performs logout when the app is terminated.
     */
    private void performLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String SPuser = sharedPreferences.getString(USER_KEY, null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long time = System.currentTimeMillis();
        db.lastLogout(SPuser, time);
        editor.remove(USER_KEY);
        editor.apply();

        Toast.makeText(this, "Logged out due to app termination", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
