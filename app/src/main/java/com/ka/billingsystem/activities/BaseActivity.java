package com.ka.billingsystem.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class BaseActivity extends AppCompatActivity {
    private static Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyDarkMode();
        BaseActivity.context = getApplicationContext();
    }
    public static Context getAppContext() {
        return BaseActivity.context;
    }
    private void applyDarkMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedMode = sharedPreferences.getString("selected_mode", "system");

        switch (selectedMode) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                boolean isNightModeEnabled = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
                PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("dark_mode", isNightModeEnabled).apply();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
