package com.ka.billingsystem.activities;

import static com.ka.billingsystem.utils.ImageEncodeAndDecode.encodeToBase64;
import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.ka.billingsystem.BuildConfig;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.services.MyBroadcastReceiver;
import com.ka.billingsystem.R;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.services.MaintenanceWorker;
import com.ka.billingsystem.utils.Import;

import java.io.File;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends BaseActivity {

    private static final int ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE = 123;
    private DataBaseHandler db = new DataBaseHandler(this);
    private static final String TAG = "LoginActivity";
    private EditText user_name;
    private EditText password;

    public static DriveServiceHelper driveServiceHelper;
    private Button login;
    private TextView ClickSignUP;
    private TextView ForgotPassword;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    private final String SHARED_PREFS = "shared_prefs";
    private final String USER_KEY = "user_key";
    private static final String WORK_MANAGER_KEY = "work_manager_key";

    private final String ADMIN_LOGIN = "admin_login";
    int invalid_login = 0;
    private static final String SHARED_PREFS_Logo = "logo";
    private SharedPreferences sharedpreferences;
    private MyBroadcastReceiver myBroadcastReceiver;
    private static boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        boolean isWorkManagerRunning = sharedpreferences.getBoolean(WORK_MANAGER_KEY, false);
        if (!isWorkManagerRunning) {

            checkWorkManagerIsRun();
        }

        if (checkStoragePermission()) {
            initializeApp();
        }
    }

    private void initializeApp() {
        Logger.initialize(this);
        checkAndNavigateToTimeSettings();
        setContentView(R.layout.activity_login);
        File cacheDir = new File(getCacheDir(), "Report");

        deleteCache(cacheDir);

        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter("com.ka.billingsystem.Send");
            myBroadcastReceiver = new MyBroadcastReceiver();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(myBroadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(myBroadcastReceiver, intentFilter);
            }
            isReceiverRegistered = true;
        }


/*        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            // User credentials found, initialize Drive service
            initializeDriveService(account.getEmail());
            // uploadPDFFile();
        }
*/
        user_name = (EditText) findViewById(R.id.txtUserName);
        password = (EditText) findViewById(R.id.txtPassword);
        login = (Button) findViewById(R.id.btnLogin);
        ClickSignUP = (TextView) findViewById(R.id.txtClickSignUP);
        ForgotPassword = findViewById(R.id.ForgotPassword);


        setVisibility();

        ClickSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = user_name.getText().toString();
                String pass = password.getText().toString();
                validateLogin(username, pass);
            }
        });
    }




    /**
     * Sets the visibility of UI elements and saves a logo image to SharedPreferences.
     */
    private void setVisibility() {
        Logger.log("Started", "setVisibility");
        try {
            String qurry = "Select * from user";
            Cursor c1 = db.getValue(qurry);
            if (c1.getCount() > 1)
                ForgotPassword.setVisibility(View.VISIBLE);
            else
                ForgotPassword.setVisibility(View.GONE);

            Drawable d = getResources().getDrawable(R.drawable.logo);
            Bitmap mBitmap = ((BitmapDrawable) d).getBitmap();
            String logo = encodeToBase64(mBitmap, Bitmap.CompressFormat.PNG, 100);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(SHARED_PREFS_Logo, logo);
            editor.apply();
        } catch (Exception e) {
            Logger.log("Crashed", "setVisibility");
            Log.e(TAG, "Error in setVisibility", e);
        }
        Logger.log("Ended", "setVisibility");
    }

    /**
     * Deletes all files within the specified directory.
     *
     * @param cacheDir The directory to delete files from.
     */
    private void deleteCache(File cacheDir) {
        Logger.log("Started", "deleteCache");
        try {
            if (cacheDir.exists()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "deleteCache");

            Log.e(TAG, "Error in deleteCache", e);
        }
        Logger.log("Ended", "deleteCache");
    }

    /**
     * Validates user login credentials.
     *
     * @param username The entered username.
     * @param pass     The entered password.
     */
    private void validateLogin(String username, String pass) {
        Logger.log("Started", "validateLogin");
        try {

            if (username.equals("") || pass.equals("")) {
                Toast.makeText(LoginActivity.this, R.string.please_enter_all_the_fields, Toast.LENGTH_SHORT).show();
            } else {
                String qurry = "Select * from user where user_id='" + username + "' and password='" + pass + "'";
                Cursor c1 = db.getValue(qurry);
                if (c1.getCount() > 0) {
                    if (username.equals("admin")) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(USER_KEY, username);
                        editor.putString(ADMIN_LOGIN, "true");
                        editor.apply();
                        Toast.makeText(LoginActivity.this, R.string.login_successfull, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), UserSelectionActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(USER_KEY, username);
                        editor.putString(ADMIN_LOGIN, "false");
                        editor.apply();
                        Toast.makeText(LoginActivity.this, R.string.login_successfull, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), UserHomePageActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    user_name.setText("");
                    password.setText("");
                    invalid_login++;
                    Toast.makeText(LoginActivity.this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                }

            }

        } catch (Exception e) {
            Logger.log("Crashed", "validateLogin");

            Log.e(TAG, "Error in validateLogin", e);
        }
        Logger.log("Ended", "validateLogin");

    }

    /**
     * Checks the status of a WorkManager task with the tag "one_time_notification" and takes action based on the result.
     */
    private void checkWorkManagerIsRun() {

        try {
            WorkManager.getInstance(this)
                    .getWorkInfosByTagLiveData("one_time_notification")
                    .observe(this, new Observer<List<WorkInfo>>() {
                        @Override
                        public void onChanged(List<WorkInfo> workInfos) {
                            if (workInfos != null && !workInfos.isEmpty()) {
                                WorkInfo latestWorkInfo = workInfos.get(0); // Assuming you want the latest status
                                WorkInfo.State state = latestWorkInfo.getState();
                                if (state.isFinished()) {
                                    if (state == WorkInfo.State.SUCCEEDED) {
                                        showPasswordDialog(LoginActivity.this);
                                    } else if (state == WorkInfo.State.FAILED) {
                                        // Work completed with an error
                                        // Handle failure case
                                    } else if (state == WorkInfo.State.CANCELLED) {
                                    }
                                }
                            } else {
                                showPasswordDialog(LoginActivity.this);
                            }
                        }
                    });
        } catch (Exception e) {

            Log.e(TAG, "Error in checkWorkManagerIsRun", e);
        }
    }

    /**
     * onclick forgot password goto ForgotActivity
     */
    public void onForgotPasswordClick(View view) {
        try {
            Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Logger.log("Crashed", "onForgotPasswordClick");

            Log.e(TAG, "Error in onForgotPasswordClick", e);
            // Handle the exception or log it as needed.
        }
    }

    /**
     * Display Dialog to contact admin .
     */
    private void showPasswordDialog(final Context context) {
        try {
            // Check if the activity is finishing
            if (!((Activity) context).isFinishing()) {
                // Inflate the dialog view
                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setView(dialogView);
                SpannableStringBuilder title = new SpannableStringBuilder("Please Contact Admin");
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
                                String MsPassword = timeStamp();
                                String defaultPassword = BuildConfig.PASWORD_CONERN;
                                if (enteredPassword.equals(MsPassword) || enteredPassword.equals(defaultPassword)) {
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

                // Check if the activity is finishing before showing the dialog
                if (!((Activity) context).isFinishing()) {
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showPasswordDialog", e);
        }
    }

    /**
     * After valid password select number of days  .
     */
    private void maintenance() {
        try {
            Toast.makeText(this, "Maintenance function executed", Toast.LENGTH_SHORT).show();

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
                          //  if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                            //    showDriveSignDialog();
                            //}
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
            alertDialog.show();
        } catch (Exception e) {

            Log.e(TAG, "Error in maintenance", e);
        }
    }

    private void showDriveSignDialog() {
        Logger.log("Started", "logOut");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Press ok to get Store the Data");
            SpannableStringBuilder title = new SpannableStringBuilder("Do you want to Store the in Google Drive");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.you_press_cancel_button, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);


                    if (account != null) {
                        // User credentials found, initialize Drive service
                        initializeDriveService(account.getEmail());
                        // uploadPDFFile();
                    } else {
                        // User credentials not found, request sign-inF
                        requestSignIn();
                    }
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
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "logOut");
        }
        Logger.log("Ended", "logOut");

    }
    private void initializeDriveService(String accountName) {
        GoogleAccountCredential credential = GoogleAccountCredential.
                usingOAuth2(LoginActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccountName(accountName);
        Drive googleDriveService = new Drive.Builder(
                AndroidHttp.newCompatibleTransport(),
                new GsonFactory(),
                credential)
                .setApplicationName("My Drive")
                .build();
        Log.d(TAG, "Drive service created successfully");
        driveServiceHelper = new DriveServiceHelper(googleDriveService);
        Log.d(TAG, "DriveServiceHelper initialized successfully");
    }


    private void requestSignIn() {

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), 400);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Prevent activity from being dismissed on outside touch
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return super.onTouchEvent(event);
    }
    private void hanleSiginIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Google sign-in successful");
                        GoogleAccountCredential credential = GoogleAccountCredential.
                                usingOAuth2(LoginActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Drive")
                                .build();
                        Log.d(TAG, "Drive service created successfully");
                        initializeDriveService(googleSignInAccount.getAccount().name);
                        Toast.makeText(LoginActivity.this, "Sign-in successfully: " +googleSignInAccount.getAccount().name, Toast.LENGTH_SHORT).show();

                        driveServiceHelper = new DriveServiceHelper(googleDriveService);
                        Log.d(TAG, "DriveServiceHelper initialized successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle sign-in failure
                        Log.e(TAG, "Sign-in failed: " + e.getMessage());
                        Toast.makeText(LoginActivity.this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.log("Started", "onActivityResult");
        try {
            Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
            switch (requestCode) {
                case 400:
                    if (resultCode == RESULT_OK) {
                        hanleSiginIntent(data);
                    } else {
                        // Handle unsuccessful sign-in
                        Log.e(TAG, "Sign-in failed with resultCode=" + resultCode);
                        Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    Log.e(TAG, "Unknown requestCode: " + requestCode);
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);

        } catch (Exception e) {
            Logger.log("Crashed", "onActivityResult");
            Log.e(TAG, "onActivityResult()-An exception occurred", e);
        }
        Logger.log("Ended", "onActivityResult");
    }
    /**
     * Schedules a one-time notification worker with a specified initial delay in days.
     *
     * @param numberOfDays The number of days for the  delay.
     */
    private void scheduleWorker(int numberOfDays) {
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

                WorkManager.getInstance(this).enqueueUniqueWork("unique_work_name", ExistingWorkPolicy.REPLACE, notificationWork);
                WorkManager.getInstance(this).cancelUniqueWork("unique_work_name");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(WORK_MANAGER_KEY, true);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "scheduleWorker");

            Log.e(TAG, "Error in scheduleWorker", e);
        }
    }

    /**
     * This is for the currentTimeMillis password generator
     */
    public String timeStamp() {

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
        return String.valueOf(currentHourTimestamp);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Checks if the automatic time setting is enabled on the device. If not, prompts the user to enable it.
     * Navigates to the system settings screen for date and time settings.
     */
    private void checkAndNavigateToTimeSettings() {
        Logger.log("Started", "checkAndNavigateToTimeSettings");
        try {

            ContentResolver resolver = getContentResolver();
            String autoTimeSetting = Settings.Global.getString(resolver, Settings.Global.AUTO_TIME);

            if (autoTimeSetting == null || autoTimeSetting.equals("0")) {
                Toast.makeText(this, "Your device's automatic time setting is disabled. Please enable it in settings", Toast.LENGTH_LONG).show();
                navigateToTimeSettings();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkAndNavigateToTimeSettings");

            Log.e(TAG, "Error in checkAndNavigateToTimeSetting", e);
        }
        Logger.log("Ended", "checkAndNavigateToTimeSettings");
    }

    /**
     * Navigates to the system settings screen for date and time settings.
     */
    private void navigateToTimeSettings() {
        Logger.log("Started", "navigateToTimeSettings");
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        startActivity(intent);
        Logger.log("Ended", "navigateToTimeSettings");
    }

    private void Permission() {
        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, ALL_FILES_ACCESS_PERMISSION_REQUEST_CODE);
                }
            } else {
                checkForPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage", STORAGE_PERMISSION_REQUEST_CODE);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeApp();
            }
        }
    }


    private boolean checkStoragePermission() {
        try {
            System.out.println("1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    System.out.println("2");
                    // Check for MANAGE_EXTERNAL_STORAGE permission on Android 11 and above
                    if (Environment.isExternalStorageManager()) {
                        return true;
                    } else {

                        return false;
                    }
                } else {
                    System.out.println("3");
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("3 true");
                        return true;  // Permission is granted
                    } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            return true;
                        }
                    } else {
                        System.out.println("3 false");
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "checkStoragePermission()", e);
        }
        return true;// Permission is implicitly granted on versions below M
    }

    private void checkForPermission(String permission, String name, int requestCode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                } else if (shouldShowRequestPermissionRationale(permission)) {
                    System.out.println("android 10");
                    showDialog(permission, name, requestCode);
                } else {
                    try {
                        ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void askAndroid10Perm() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        startActivityForResult(intent, 3);
    }

    /**
     * Shows a dialog explaining the need for a specific permission and provides an option to grant it.
     *
     * @param permission  The permission for which the explanation is provided.
     * @param name        A human-readable name for the permission.
     * @param requestCode The request code to identify the permission request.
     */

    private void showDialog(String permission, String name, int requestCode) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Permission to access your " + name + " is required to use this app");
            SpannableStringBuilder title = new SpannableStringBuilder("Permission required !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{permission}, requestCode);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.initialize(this);
        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!checkStoragePermission()) {
            Permission();
        }

        //askAndroid10Perm();

        sharedpreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initializeApp();
            } else {
                Toast.makeText(this, "Storage  permission denied. The app may not work properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}