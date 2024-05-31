package com.ka.billingsystem.crash;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Logger {

    private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024; // 1 MB
    private static final long MAX_DELETE_SIZE_BYTES = 100 * 1024; // 100 KB
    private static SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "shared_prefs";
    private static final String KEY_LINE_COUNT = "lineCount";
    private static final String LOG_FILE_NAME = "KIRTHANA_AGENCIES_STATUS_logs.txt";
    private  static Context contexti;
    public static void initialize(Context context) {
        contexti=context;
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
    }

    public static void log(String message, String callingMethodName) {

             int lineCount;
        if (sharedPreferences != null) {
             lineCount = sharedPreferences.getInt(KEY_LINE_COUNT, 0);
            // Do something with the retrieved value
        } else {
                lineCount=0;
        }
        StackTraceElement callingMethodStack = new Throwable().getStackTrace()[1];
        String callingClassName = callingMethodStack.getClassName();
      //  Log.d(callingClassName, callingMethodName + ":" + message);
        sharedPreferences.edit().putInt(KEY_LINE_COUNT, lineCount + 1).apply();

        new LogToFileTask().execute(callingClassName, callingMethodName, message, String.valueOf(lineCount));
    }

    private static class LogToFileTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String className = params[0];
            String methodName = params[1];
            String message = params[2];
            String lineCount = params[3];
            Date day = new Date();
            SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            // Check if external storage is available
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
              // Get the Downloads directory
                File logFile;
                if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                    File downloadsDir;
                    downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    logFile = new File(downloadsDir, LOG_FILE_NAME);
                }else {
                    logFile  = new File(contexti.getFilesDir(), LOG_FILE_NAME);

                } // Create a new file in the Downloads directory


                if (logFile.exists()) {
             //       System.out.println(logFile.getAbsolutePath());
               //     System.out.println("The file already exists.");
                } else {
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    List<String> existingLines = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        existingLines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
                    } else {
                        existingLines = readLinesFromFileLegacy(logFile);
                    }

                    long currentFileSize = logFile.length();
                  //  System.out.println("curretFileSize" + currentFileSize);
                    if (currentFileSize >= MAX_FILE_SIZE_BYTES) {
                     //   System.out.println("working");
                        int linesToRemove = (int) (MAX_DELETE_SIZE_BYTES / (float) currentFileSize * existingLines.size());

                        existingLines.subList(0, linesToRemove).clear();
                        maxLineUpadte(logFile, existingLines);
                    }
                    try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                        PrintWriter pw = new PrintWriter(fos);
                        pw.println("(" + lineCount + ")" + formatter1.format(day) + "{" + className + " : " + methodName + "() : " + message + "}");
                        pw.flush();
                     //   System.out.println(lineCount);

                    } catch (IOException e) {
                   //     Log.e("CustomLogger", "Error saving log to file", e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
            //    Log.e("CustomLogger", "External storage not available. Unable to save log to file.");
            }

            return null;
        }

        private List<String> readLinesFromFileLegacy(File file) throws IOException {
            List<String> lines = new LinkedList<>();
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            br.close();
            fis.close();

            return lines;
        }

        private void maxLineUpadte(File logFile, List<String> existingLines) {
            try (FileOutputStream fos = new FileOutputStream(logFile, false)) {
                PrintWriter pw = new PrintWriter(fos);
                for (String line : existingLines) {
                    pw.println(line);
                }
                pw.flush();
            } catch (IOException e) {
             //   Log.e("CustomLogger", "Error saving log to file", e);
            }

        }

    }
}