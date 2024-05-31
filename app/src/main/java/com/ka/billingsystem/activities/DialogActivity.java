package com.ka.billingsystem.activities;

import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.ka.billingsystem.BuildConfig;
import com.ka.billingsystem.R;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.services.MaintenanceWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class DialogActivity extends AppCompatActivity {
    private static final String TAG = "DialogActivity";
    private SharedPreferences sharedpreferences;
    private final String SHARED_PREFS = "shared_prefs";
    private static final String WORK_MANAGER_KEY = "work_manager_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            sharedpreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_dialog);
            showPasswordDialog(DialogActivity.this);
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "Error in onClick:", e);
        }
        Logger.log("Ended", "onCreate");

    }

    /**
     * Display Dialog to contact admin .
     */
    private void showPasswordDialog(final Context context) {
        Logger.log("Started", "showPasswordDialog");
        try {
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setCancelable(false);
            builder.setView(dialogView);
            SpannableStringBuilder title = new SpannableStringBuilder("Developer Concern");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            builder.setPositiveButton("OK", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    final EditText editTextPassword = dialogView.findViewById(R.id.dialog_password);
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(positiveButton, negativeButton);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String enteredPassword = editTextPassword.getText().toString();
                            String msPassword = timeStamp();
                            String defaultPassword = BuildConfig.PASWORD_CONERN;
                            if (enteredPassword.equals(msPassword) || enteredPassword.equals(defaultPassword)) {
                                maintenance();
                                alertDialog.dismiss();
                            } else {
                                editTextPassword.setText("");
                                showToast(context, "Wrong Password");
                            }
                        }
                    });

                }

            });
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "showPasswordDialog");
            Log.e(TAG, "Error in showPasswordDialog:", e);
        }
        Logger.log("Ended", "showPasswordDialog");
    }

    /**
     * After valid password select number of days  .
     */
    private void maintenance() {
        Logger.log("Started", "maintenance");
        try {
            NumberPicker numberPicker = new NumberPicker(this);
            String[] displayedValues = new String[361];  // 360 numeric values + "Disable"
            displayedValues[0] = "Disable";  // Set the first element as "Disable"

            for (int i = 1; i <= 360; i++) {
                displayedValues[i] = String.valueOf(i);
            }

            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(360);
            numberPicker.setDisplayedValues(displayedValues);

            // Set the default value as "Disable"
            numberPicker.setValue(0);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select a Number of Days")
                    .setCancelable(false)
                    .setView(numberPicker)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int selectedNumber = numberPicker.getValue();
                            scheduleWorker(selectedNumber);
                            finish();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(positiveButton, negativeButton);
                }

            });
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "maintenance");
            Log.e(TAG, "Error in maintenance:", e);
        }
        Logger.log("Ended", "maintenance");
    }

    /**
     * Schedules a one-time notification worker with a specified initial delay in days.
     *
     * @param numberOfDays The number of days for the  delay.
     */
    private void scheduleWorker(int numberOfDays) {
        Logger.log("Started", "scheduleWorker");
        try {
            String timeUnitString = BuildConfig.TIME_UNIT;
            TimeUnit timeUnit = TimeUnit.valueOf(timeUnitString);
            if (numberOfDays != 0) {
                OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(MaintenanceWorker.class)
                        .setInitialDelay(numberOfDays, timeUnit)
                        .addTag("one_time_notification")
                        .build();
                WorkManager.getInstance(this).enqueueUniqueWork("unique_work_name", ExistingWorkPolicy.REPLACE, notificationWork);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(WORK_MANAGER_KEY, false);
                editor.apply();
                Toast.makeText(getApplicationContext(), numberOfDays + " Days", Toast.LENGTH_LONG).show();
            } else {
                OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(MaintenanceWorker.class)
                        .setInitialDelay(2, TimeUnit.MINUTES)
                        .addTag("one_time_notification")
                        .build();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(WORK_MANAGER_KEY, true);
                editor.apply();

                WorkManager.getInstance(this).enqueueUniqueWork("unique_work_name", ExistingWorkPolicy.REPLACE, notificationWork);
                WorkManager.getInstance(this).cancelUniqueWork("unique_work_name");
                Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "scheduleWorker");
            Log.e(TAG, "Error in scheduleWorker:", e);
        }
        Logger.log("Ended", "scheduleWorker");
    }

    /**
     * This is for the currentTimeMillis password generator
     */
    public String timeStamp() {
        Logger.log("Started", "timeStamp");
        long currentTimeMillis = System.currentTimeMillis();
        // Get a Calendar instance and set it to the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        // Truncate to the beginning of the current hour
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        // Get the timestamp for the beginning of the current hour
        long currentHourTimestamp = calendar.getTimeInMillis();
        Logger.log("Ended", "timeStamp");
        return String.valueOf(currentHourTimestamp);

    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}