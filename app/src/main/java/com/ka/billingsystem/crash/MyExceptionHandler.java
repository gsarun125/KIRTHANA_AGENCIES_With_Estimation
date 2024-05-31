package com.ka.billingsystem.crash;

import android.content.Context;
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
import java.util.LinkedList;
import java.util.List;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024; // 1 MB
    private static final long MAX_DELETE_SIZE_BYTES = 100 * 1024; // 100 KB


    public MyExceptionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            // Save the crash logs to a text file
            saveCrashLogs(e);

            // Perform any additional actions like restarting the app, etc.

        } catch (IOException ex) {
            Log.e("ExceptionHandler", "Error saving crash logs", ex);
        }

        // Terminate the app
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    private void saveCrashLogs(Throwable e) throws IOException {
        String fileName = "KIRTHANA_AGENCIES_log.txt";

        // Check if external storage is available
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Get the Downloads directory
            File logFile;
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                File downloadsDir;
                downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                logFile = new File(downloadsDir, fileName);
            }else {
                logFile  = new File(context.getFilesDir(), fileName);

            }

            if (logFile.exists()) {
                System.out.println("The file already exists.");
            } else {
                logFile.createNewFile();
            }


            List<String> existingLines = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                existingLines = Files.readAllLines(logFile.toPath(), StandardCharsets.UTF_8);
            } else {
                existingLines = readLinesFromFileLegacy(logFile);
            }
            long currentFileSize = logFile.length();
            System.out.println("curretFileSize" + currentFileSize);
            if (currentFileSize >= MAX_FILE_SIZE_BYTES) {
                System.out.println("working");
                int linesToRemove = (int) (MAX_DELETE_SIZE_BYTES / (float) currentFileSize * existingLines.size());

                existingLines.subList(0, linesToRemove).clear();
                maxLineUpadte(logFile, existingLines);
            }

            try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
                PrintWriter pw = new PrintWriter(fos);
                e.printStackTrace(pw);
                pw.flush();
            }

            // Optionally, you can show a Toast or log the file path
            Log.d("ExceptionHandler", "Crash log saved to: " + logFile.getAbsolutePath());
        } else {
            Log.e("ExceptionHandler", "External storage not available. Unable to save crash log.");
        }
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
            Log.e("CustomLogger", "Error saving log to file", e);
        }

    }
}
