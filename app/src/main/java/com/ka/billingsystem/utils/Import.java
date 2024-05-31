package com.ka.billingsystem.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ka.billingsystem.crash.Logger;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Import {

    public static String ImportData(Context context, String packageName, String filepath) {

        Logger.log("Started", "ImportData");
        try {
            File zipFile = new File(filepath);
            File dir;
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
                dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES/Restore");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }else {
                dir=new File(context.getFilesDir().toURI());

            }

            try (FileInputStream fis = new FileInputStream(zipFile);
                 ArchiveInputStream ais = new ArchiveStreamFactory()
                         .createArchiveInputStream(ArchiveStreamFactory.ZIP, fis)) {

                ArchiveEntry entry;
                while ((entry = ais.getNextEntry()) != null) {
                    String fileName = entry.getName();
                    File outFile = new File(dir, fileName);
                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                    } else {
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            IOUtils.copy(ais, fos);
                        }
                    }
                }
            }

            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String copyPath = "BILLING_SYSTEM.db";

            File currentDB = new File(currentDBPath);

            File backupDB = new File(dir, copyPath);
            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                deleteDirectory(dir);
                Logger.log("Ended", "ImportData");
                return "Importing started";

            }
        } catch (Exception e) {
            Logger.log("Crashed", "ImportData");
            Log.e("ImportData()", "ImportData()-An exception occurred", e);
        }
        Logger.log("Ended", "ImportData");
        return "Failed to import data";


    }

    /**
     * Delete a directory and its contents recursively.
     *
     * @param directory The directory to be deleted.
     */
    private static void deleteDirectory(File directory) {
        Logger.log("Started", "ImportData");
        try {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        deleteDirectory(file);
                    }
                }
            }
            directory.delete();
        } catch (Exception e) {
            Logger.log("Crashed", "ImportData");
        }
        Logger.log("Ended", "ImportData");
    }
}
