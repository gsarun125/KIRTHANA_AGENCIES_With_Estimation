package com.ka.billingsystem.activities;

import static com.google.android.material.internal.ContextUtils.getActivity;
import static com.ka.billingsystem.utils.Export.ExportData;
import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.ka.billingsystem.BuildConfig;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.utils.Export;
import com.ka.billingsystem.utils.Import;
import com.ka.billingsystem.utils.RangeFilter;
import com.ka.billingsystem.services.MaintenanceWorker;
import com.ka.billingsystem.model.UserseclectionAdapter;
import com.ka.billingsystem.model.SelectionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UserSelectionActivity extends BaseActivity implements SelectionListener {
    private DataBaseHandler db = new DataBaseHandler(this);
    private static final String TAG = "UserSelectionActivity";
    private ProgressDialog progressDialog;
    private int progressStatus = 0;
    private Handler handler;

    private RecyclerView recyclerView;
    int storage_RQ = 101;
    private UserseclectionAdapter userseclectionAdapter;
    private List<String> mUsername = new ArrayList();
    private List<String> mUserid = new ArrayList();
    private List<String> mGen_Date = new ArrayList();
    private static final int PICK_FILE_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private SharedPreferences sharedpreferences;
    private String SHARED_PREFS = "shared_prefs";
    private  ImageButton backup;
    private static final String WORK_MANAGER_KEY = "work_manager_key";
    private ImageButton menu;
    private LinearLayout emptystate;
    DriveServiceHelper driveServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_selection);
            recyclerView = findViewById(R.id.userSelection_RV);
            sharedpreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

            menu = findViewById(R.id.btnmenu);
            emptystate = findViewById(R.id.USEmpty1);
            displayPdf();

            backup=findViewById(R.id.backup);


            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                   backup.setImageResource(R.drawable.baseline_cloud_done_24);
                   initializeDriveService(account.getEmail());
            }

            backup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(UserSelectionActivity.this);
                    if (account != null) {
                        showDriveConectionDialog(account);
                    } else {
                        if (NetworkUtils.isNetworkAvailable(UserSelectionActivity.this)) {
                            System.out.println("kjhgfdssdfghj");
                            requestSignIn();
                        }else {
                            Toast.makeText(UserSelectionActivity.this,"No Network Connection",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });



            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(UserSelectionActivity.this, view);
                    popupMenu.getMenuInflater().inflate(R.menu.threedotadmin, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int ch = menuItem.getItemId();
                            if (ch == R.id.Maintenance) {
                                showPasswordDialog(UserSelectionActivity.this);
                            } else if (ch == R.id.GST) {
                                AddGst();
                            } else if (ch == R.id.Logout) {
                                logOut();
                            } else if (ch == R.id.Export) {
                                export();
                            } else if (ch == R.id.Import) {
                                importDB();
                            } else if (ch==R.id.menuabout) {
                                about();
                            }
                            else if (ch == R.id.AppTheme) {
                            showThemeSelectionDialog();

                            }
                            return true;
                        }
                    });
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        popupMenu.setForceShowIcon(true);
                    }
                    popupMenu.show();

                }
            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
        }
        Logger.log("Ended", "onCreate");
    }
    private void showThemeSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.theme_popup, null);
        builder.setView(dialogView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedMode = sharedPreferences.getString("selected_mode", "system");

        RadioButton lightMode = dialogView.findViewById(R.id.light_mode);
        RadioButton darkMode = dialogView.findViewById(R.id.dark_mode);
        RadioButton systemDefault = dialogView.findViewById(R.id.system_default);

        switch (selectedMode) {
            case "light":
                lightMode.setChecked(true);
                break;
            case "dark":
                darkMode.setChecked(true);
                break;
            case "system":
                systemDefault.setChecked(true);
                break;
        }

        builder.setPositiveButton("OK", (dialog, which) -> {
            int checkedId = ((RadioGroup) dialogView.findViewById(R.id.groupradio2)).getCheckedRadioButtonId();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            switch (checkedId) {
                case R.id.light_mode:
                    editor.putString("selected_mode", "light");
                    editor.putBoolean("dark_mode", false);
                    break;
                case R.id.dark_mode:
                    editor.putString("selected_mode", "dark");
                    editor.putBoolean("dark_mode", true);
                    break;
                case R.id.system_default:
                    editor.putString("selected_mode", "system");
                    int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                    editor.putBoolean("dark_mode", nightModeFlags == Configuration.UI_MODE_NIGHT_YES);
                    break;
            }
            editor.apply();
            Intent intent = new Intent(this,UserSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
         dialog.show();
    }



    private void showDriveConectionDialog(GoogleSignInAccount account) {
        Logger.log("Started", "showPasswordDialog");
        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_driveconection, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setView(dialogView);
            SpannableStringBuilder title = new SpannableStringBuilder("Backup Connection Info");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            builder.setPositiveButton("Cancel", null);
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    TextView gmail = dialogView.findViewById(R.id.Gmail);
                    TextView gmailID = dialogView.findViewById(R.id.id);

                    Button disconect=dialogView.findViewById(R.id.disconnectButton);

                    Button backupNow=dialogView.findViewById(R.id.Backupnow);

                    backupNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetworkUtils.isNetworkAvailable(UserSelectionActivity.this)) {
                                Export.ExportResult data= Export.ExportData(UserSelectionActivity.this.getPackageName(), "Kirthana_backup.zip", create("Kirthana_backup.zip"));

                                System.out.println("not conected");
                                uploadPDFFile(data.getFilepath());
                                alertDialog.dismiss();
                            }else {
                                Toast.makeText(UserSelectionActivity.this, "No Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    disconect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            disconnect();
                            backup.setImageResource(R.drawable.baseline_cloud_off_24);
                            alertDialog.dismiss();
                        }
                    });
                    gmailID.setText("Gmail ID: "+account.getEmail());
                    gmail.setText("Account Name: "+account.getDisplayName());
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
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

    public void uploadPDFFile(String filepath) {
        Log.d(TAG, "uploadPDFFile() called");
        ProgressDialog progressDialog = new ProgressDialog(UserSelectionActivity.this);
        progressDialog.setTitle("Uploading to Google Drive");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        // Check if driveServiceHelper is null
        if (driveServiceHelper != null) {
            Log.d(TAG, "Uploading file with DriveServiceHelper");
            driveServiceHelper.createFileUnique(filepath).addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserSelectionActivity.this, "File uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Log.e(TAG, "Error uploading file: " + e.getMessage());
                            Toast.makeText(UserSelectionActivity.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Log.e(TAG, "Error: Drive service helper is null");
            Toast.makeText(UserSelectionActivity.this, "Error: Drive service helper is null", Toast.LENGTH_SHORT).show();
        }
    }
    private void initializeDriveService(String accountName) {
        GoogleAccountCredential credential = GoogleAccountCredential.
                usingOAuth2(UserSelectionActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
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

    private void disconnect() {
        GoogleSignInClient client = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        client.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Handle sign-out completion, if needed
                if (task.isSuccessful()) {
                    // Sign-out successful
                    Toast.makeText(UserSelectionActivity.this, "Disconnected successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Sign-out failed
                    Toast.makeText(UserSelectionActivity.this, "Failed to disconnect", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private void hanleSiginIntent(Intent data) {
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        Log.d(TAG, "Google sign-in successful");
                        GoogleAccountCredential credential = GoogleAccountCredential.
                                usingOAuth2(UserSelectionActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        Drive googleDriveService = new Drive.Builder(
                                AndroidHttp.newCompatibleTransport(),
                                new GsonFactory(),
                                credential)
                                .setApplicationName("My Drive")
                                .build();
                        Log.d(TAG, "Drive service created successfully");
                        initializeDriveService(googleSignInAccount.getAccount().name);

                        driveServiceHelper = new DriveServiceHelper(googleDriveService);
                        Log.d(TAG, "DriveServiceHelper initialized successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle sign-in failure
                        Log.e(TAG, "Sign-in failed: " + e.getMessage());
                        Toast.makeText(UserSelectionActivity.this, "Sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void about() {
        try {
            // Inflate the dialog view
            View dialogView = LayoutInflater.from(this).inflate(R.layout.about_dilog, null);

            System.out.println("fkjfjh");

            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            SpannableStringBuilder title = new SpannableStringBuilder("About");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            builder.setCancelable(true);
            builder.setView(dialogView);
           // builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            String versionName = BuildConfig.VERSION_NAME;

            TextView appName=dialogView.findViewById(R.id.tvappname);
            appName.setText("Kirthana Agencies");

            TextView tvVersion=dialogView.findViewById(R.id.tvversion);
            tvVersion.setText(versionName);

            String versionCode= String.valueOf(BuildConfig.VERSION_CODE);
            TextView tvVersionCode=dialogView.findViewById(R.id.tvversioncode);
            tvVersionCode.setText(versionCode);

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                }

            });

            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "showPasswordDialog");
        }
        Logger.log("Ended", "showPasswordDialog");

    }

    /**
     * Initiates the process of importing a database.
     */
    private void importDB() {
        Logger.log("Started", "importDB");
        try {
            selectFile();

        } catch (Exception e) {
            Logger.log("Crashed", "importDB");
            Log.e(TAG, "Import-Start exception occurred", e);
        }
        Logger.log("Ended", "importDB");
    }

    /**
     * Initiates the process of exporting data from the "Transation" table.
     */
    private void export() {
        Logger.log("Started", "export");
        try {
            String qurry = "Select * from Transation";
            Cursor c1 = db.getValue(qurry);
            if (c1.getCount() > 1) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh_mm_ssa", Locale.getDefault());
                String currentDateAndTime = sdf.format(new Date());
                String Backup_filename = "Kirthana_backup_" + currentDateAndTime + ".zip";
                String packagesname = getPackageName();
                Export.ExportResult result = ExportData(packagesname, Backup_filename, create(Backup_filename));
                String message = result.getMessage();
                String filename = result.getFileName();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (filename != null) {
                    share(filename);
                }

            } else {
                alertDialog();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "export");
            Log.e(TAG, "error in export");
        }
        Logger.log("Ended", "export");
    }

    /**
     * This will show when there is no data to export
     */
    private void alertDialog() {
        Logger.log("Started", "alertDialog");
        try {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.no_invoice_found_to_export);
            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setPositiveButton("ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();

            });
            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "alertDialog");
        }
        Logger.log("Ended", "alertDialog");
    }

    /**
     * Creates a file with the specified filename in the "Backup" directory within internal storage.
     *
     * @param Backup_filename The desired filename for the created file.
     * @return The created File object.
     */
    private File create(String Backup_filename) {
        Logger.log("Started", "create");

        File subdir = new File(this.getFilesDir(), "Backup");
        if (!subdir.exists()) {
            subdir.mkdir();
        }
        File zipFile = new File(subdir, Backup_filename);
        Logger.log("Ended", "create");
        Logger.log("Ended", "create");
        return zipFile;
    }


    /**
     * Initiates the file selection process using the system file picker.
     */
    private void selectFile() {
        Logger.log("Started", "selectFile");
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/zip");
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        } catch (Exception e) {
            Logger.log("Crashed", "selectFile");
            Log.e(TAG, "selectFile()-An exception occurred", e);
        }
        Logger.log("Ended", "selectFile");
    }

    /**
     * Called when an activity that was started with startActivityForResult completes.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Logger.log("Started", "onActivityResult");
        try {
            Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
            switch (requestCode) {
                case 400:
                    if (resultCode == RESULT_OK) {
                        hanleSiginIntent(data);
                        backup.setImageResource(R.drawable.baseline_cloud_done_24);
                        Toast.makeText(this, "Sign-in Successfully", Toast.LENGTH_SHORT).show();
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

            if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                Uri uri = data.getData();
                String filePath = getFilePathFromUri(uri);
                String packagesname = getPackageName();
                String status1 = Import.ImportData(this,packagesname, filePath);
                Toast.makeText(getApplicationContext(), status1, Toast.LENGTH_SHORT).show();
                showProgressDialog();
            }
            super.onActivityResult(requestCode, resultCode, data);

        } catch (Exception e) {
            Logger.log("Crashed", "onActivityResult");
            Log.e(TAG, "onActivityResult()-An exception occurred", e);
        }
        Logger.log("Ended", "onActivityResult");
    }

    /**
     * Shows a progress dialog while importing data and simulates progress with a horizontal progress bar.
     */
    private void showProgressDialog() {
        Logger.log("Started", "showProgressDialog");
        try {
            progressStatus = 0; // Reset the progressStatus to 0
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Importing your data...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.show();
            handler = new Handler();
            new Thread(() -> {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    handler.post(() -> progressDialog.setProgress(progressStatus));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (progressStatus == 100) {
                        progressDialog.dismiss();
                        File dir = new File(this.getFilesDir(), "DATA");
                        // Check if the cache directory exists
                        if (dir.exists()) {
                            // Get the list of files in the cache directory
                            File[] files = dir.listFiles();

                            // Delete each file in the cache directory
                            if (files != null) {
                                for (File file : files) {
                                    file.delete();
                                }
                            }
                        }

                        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }).start();
        } catch (Exception e) {
            Logger.log("Crashed", "showProgressDialog");
        }
        Logger.log("Ended", "showProgressDialog");
    }

    /**
     * Gets the file path from a given Uri.
     *
     * @param uri The Uri of the file.
     * @return The file path corresponding to the Uri.
     */
    private String getFilePathFromUri(Uri uri) {
        Logger.log("Started", "getFilePathFromUri");
        String filePath = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    File file = new File(getCacheDir(), "temp");
                    try (OutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[4 * 1024]; // or other buffer size
                        int read;
                        while ((read = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, read);
                        }
                        outputStream.flush();
                    }
                    filePath = file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            filePath = uri.getPath();
        }
        Logger.log("Ended", "getFilePathFromUri");
        return filePath;
    }


    private void displayPdf() {
        Logger.log("Started", "getFilePathFromUri");
        try {
            recyclerView.setHasFixedSize(true);
            mGen_Date.clear();
            mUserid.clear();
            mUsername.clear();
            Cursor c1;
            c1 = db.getValue("SELECT * FROM user WHERE user_id <> 'admin'");
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("user_name"));
                    @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("user_id"));
                    @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("Last_Logout"));
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy : HH:mm");
                    Date res = new Date(data3);
                    mUsername.add(data1);
                    mUserid.add(data2);
                    if (data3 != 0) {
                        mGen_Date.add(formatter1.format(res));
                    } else {
                        mGen_Date.add("0");
                    }

                } while (c1.moveToNext());
            }
            if (mUsername.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptystate.setVisibility(View.VISIBLE);

            } else {
                emptystate.setVisibility(View.GONE);

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            }
            userseclectionAdapter = new UserseclectionAdapter(this, this, mUsername, mUserid, mGen_Date);
            recyclerView.setAdapter(userseclectionAdapter);
        } catch (Exception e) {
            Logger.log("Crashed", "getFilePathFromUri");
        }
        Logger.log("Ended", "getFilePathFromUri");
    }

    /**
     * Callback method invoked when a PDF is selected.
     *
     * @param userid The user ID associated with the selected PDF.
     */
    @Override
    public void onpdfSelected(String userid) {
        Logger.log("Started", "onpdfSelected");
        try {
            SharedPreferences sharedpreferences;
            String USER_KEY = "user_key";
            String SHARED_PREFS = "shared_prefs";
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(USER_KEY, userid);
            editor.apply();
            Intent i = new Intent(this, AdminHomeActivity.class);
            startActivity(i);
        } catch (Exception e) {
            Logger.log("Crashed", "onpdfSelected");
        }
        Logger.log("Ended", "onpdfSelected");
    }


    @Override
    public void onBackPressed() {
        logOut();
    }

    /**
     * Displays an alert dialog when the user chooses to log out.
     */
    private void logOut() {
        Logger.log("Started", "logOut");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.press_ok_to_logout);
            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
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
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.remove("user_key");
                    editor.remove("password_key");
                    editor.apply();

                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    positiveButton.setTextColor(ContextCompat.getColor(UserSelectionActivity.this, R.color.black));
                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                }

            });
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "logOut");
        }
        Logger.log("Ended", "logOut");
    }

    /**
     * Shares a file using a content URI generated by FileProvider.
     *
     * @param filename The name of the file to share.
     */
    private void share(String filename) {
        Logger.log("Started", "share");
        try {
            File subdir = new File(getFilesDir(), "Backup");
            File zipFile = new File(subdir, filename);

            if (zipFile.exists()) {
                try {
                    Uri uri = FileProvider.getUriForFile(this, "com.ka.billingsystem.provider", zipFile);
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setDataAndType(uri, "application/zip");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    this.startActivity(Intent.createChooser(share, "Share"));
                } catch (Exception e) {
                    Log.e("ShareError", "Error sharing file", e);
                }
            } else {
                Toast.makeText(UserSelectionActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "share");
        }
    }

    /**
     * Initiates the process of adding GST by showing a custom alert dialog.
     */
    private void AddGst() {
        Logger.log("Started", "AddGst");
        try {
            showCustomAlertDialog();
        } catch (Exception e) {
            Logger.log("Crashed", "AddGst");
        }
        Logger.log("Ended", "AddGst");

    }

    /**
     * Shows a custom alert dialog with options related to GST.
     */
    private void showCustomAlertDialog() {
        Logger.log("Started", "showCustomAlertDialog");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_gst, null);
            builder.setView(dialogView);
            SpannableStringBuilder title = new SpannableStringBuilder("Choose Edit option");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            CardView gstCardView = dialogView.findViewById(R.id.gstCardView);
            CardView igstCardView = dialogView.findViewById(R.id.igstCardView);


            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            gstCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CGST_or_SGSTEDIT(alertDialog);
                }
            });

            igstCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IGSTEDIT(alertDialog);
                }
            });

            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                }
            });

            // Set window animations for enter and exit
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "showCustomAlertDialog");
        }
        Logger.log("Ended", "showCustomAlertDialog");
    }

    /**
     * Shows a custom AlertDialog for choosing between CGST/SGST and IGST options.
     */
    public void IGSTEDIT(AlertDialog firstAlertDialog) {
        Logger.log("Started", "IGSTEDIT");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_addgst, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            SpannableStringBuilder title = new SpannableStringBuilder("Change IGST");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            EditText gstValue = dialogView.findViewById(R.id.editGst);
            String query = "Select * from user where id='1'";
            Cursor c1 = db.getValue(query);
            if (c1.moveToFirst()) {
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("igst"));
                gstValue.setHint("Current IGST : " + data1);
            }
            CheckBox agreeCheckbox = dialogView.findViewById(R.id.agree_checkbox);
            gstValue.setFilters(new InputFilter[]{new RangeFilter(1, 100)});
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton("OK", null);  // Set to null initially
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (agreeCheckbox.isChecked() && gstValue.getText().length() != 0) {
                                db.Add_GST(gstValue.getText().toString());
                                if (gstValue.getText().length() != 0) {
                                    Toast.makeText(UserSelectionActivity.this, "IGST Updated Successfully ", Toast.LENGTH_SHORT).show();
                                }
                                firstAlertDialog.dismiss();
                                alertDialog.dismiss();  // Dismiss the dialog when conditions are met
                            } else {
                                if (gstValue.getText().length() == 0) {
                                    gstValue.setError("IGST is required");
                                    gstValue.setFocusable(true);
                                    gstValue.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.showSoftInput(gstValue, InputMethodManager.SHOW_IMPLICIT);
                                } else if (!agreeCheckbox.isChecked()) {
                                    agreeCheckbox.setError(getString(R.string.please_agree_to_continue));
                                }
                            }
                        }
                    });
                }
            });

            // Set window animations for enter and exit
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "IGSTEDIT");
        }
        Logger.log("Ended", "IGSTEDIT");
    }

    /**
     * Shows a custom AlertDialog for editing CGST and SGST.
     */
    public void CGST_or_SGSTEDIT(AlertDialog firstAlertDialog) {
        Logger.log("Started", "CGST_or_SGSTEDIT");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_addcgst_sgst, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            SpannableStringBuilder title = new SpannableStringBuilder("Change CGST/SGST");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            EditText CgstValue = dialogView.findViewById(R.id.editCGST);
            EditText SgstValue = dialogView.findViewById(R.id.editSGST);

            String query = "Select * from user where id='1'";
            Cursor c1 = db.getValue(query);
            String oldCGST=null;
            String oldSGST = null;
            if (c1.moveToFirst()) {
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("cgst"));
                @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("sgst"));
                oldCGST=data1;
                oldSGST=data2;
                CgstValue.setHint("Current CGST : " + data1);
                SgstValue.setHint("Current SGST : " + data2);
            }


            CheckBox agreeCheckbox = dialogView.findViewById(R.id.agree_checkbox);
            CgstValue.setFilters(new InputFilter[]{new RangeFilter(1, 100)});
            SgstValue.setFilters(new InputFilter[]{new RangeFilter(1, 100)});

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.setPositiveButton("OK", null);  // Set to null initially

            AlertDialog alertDialog = builder.create();

            String finalOldSGST = oldSGST;
            String finalOldCGST = oldCGST;
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (agreeCheckbox.isChecked() && (CgstValue.getText().length() != 0 || SgstValue.getText().length() != 0)) {
                                if (CgstValue.getText().length() != 0 && SgstValue.getText().length() != 0) {
                                    db.ADD_CGST_SGST(CgstValue.getText().toString(), SgstValue.getText().toString());
                                    Toast.makeText(UserSelectionActivity.this, "CGST and SGST Updated Successfully ", Toast.LENGTH_SHORT).show();
                                } else if (CgstValue.getText().length() != 0) {
                                    db.ADD_CGST_SGST(CgstValue.getText().toString(), finalOldSGST);
                                    Toast.makeText(UserSelectionActivity.this, "CGST Updated Successfully ", Toast.LENGTH_SHORT).show();
                                } else {
                                    db.ADD_CGST_SGST(finalOldCGST, SgstValue.getText().toString());
                                    Toast.makeText(UserSelectionActivity.this, "SGST Updated Successfully ", Toast.LENGTH_SHORT).show();
                                }
                                firstAlertDialog.dismiss();
                                alertDialog.dismiss();  // Dismiss the dialog when conditions are met
                            } else {
                                if(agreeCheckbox.isChecked()){
                                    if (CgstValue.getText().length() == 0 && SgstValue.getText().length() == 0) {
                                        Toast.makeText(UserSelectionActivity.this, "Please Enter the CGST or SGST ", Toast.LENGTH_SHORT).show();

                                    }

                                }else {
                                    if (CgstValue.getText().length() != 0 || SgstValue.getText().length() != 0) {
                                        agreeCheckbox.setError(getString(R.string.please_agree_to_continue));

                                    }else {
                                        Toast.makeText(UserSelectionActivity.this, "Please Enter the CGST or SGST ", Toast.LENGTH_SHORT).show();
                                    }

                                }

                            }
                        }

                    });
                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                }
            });

            // Set window animations for enter and exit
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "CGST_or_SGSTEDIT");
        }
        Logger.log("Ended", "CGST_or_SGSTEDIT");
    }

    /**
     * Initiates the maintenance function, allowing the user to select a number of days for scheduling maintenance.
     */
    private void maintenance() {
        Logger.log("Started", "maintenance");
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
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);
                }

            });

            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "maintenance");
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
        }
        Logger.log("Ended", "scheduleWorker");
    }

    /**
     * Display Dialog to contact admin .
     */
    private void showPasswordDialog(final Context context) {
        Logger.log("Started", "showPasswordDialog");
        try {
            // Inflate the dialog view
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_password, null);

            final EditText editTextPassword = dialogView.findViewById(R.id.dialog_password);

            // Create the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            SpannableStringBuilder title = new SpannableStringBuilder("please contact admin");
            title.setSpan(new AbsoluteSizeSpan(23, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            builder.setCancelable(true);
            builder.setView(dialogView);
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(UserSelectionActivity.this,positiveButton, negativeButton);


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

            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "showPasswordDialog");
        }
        Logger.log("Ended", "showPasswordDialog");
    }

    /**
     * Generates a timestamp representing the beginning of the current hour.
     *
     * @return A string representing the timestamp for the beginning of the current hour.
     */
    public String timeStamp() {
        Logger.log("Started", "showPasswordDialog");

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
        Logger.log("Ended", "showPasswordDialog");
        return String.valueOf(currentHourTimestamp);
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
