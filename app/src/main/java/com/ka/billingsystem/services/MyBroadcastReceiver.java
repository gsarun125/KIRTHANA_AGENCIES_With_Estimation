package com.ka.billingsystem.services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ka.billingsystem.activities.DialogActivity;

import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isLoginActivityVisible(context)) {
            Intent activityIntent = new Intent(context, DialogActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }

    }

    /**
     * Check if the LoginActivity is currently visible or in the foreground.
     *
     * @param context The application context.
     * @return True if the LoginActivity is visible, false otherwise.
     */
    private boolean isLoginActivityVisible(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);

        if (!tasks.isEmpty()) {
            String topActivity = tasks.get(0).topActivity.getClassName();
            return topActivity.equals("com.ka.billingsystem.Activity.LoginActivity");
        }
        return false;
    }

}
