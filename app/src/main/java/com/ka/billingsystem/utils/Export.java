package com.ka.billingsystem.utils;

import com.ka.billingsystem.crash.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Export {
    public static ExportResult ExportData(String packageName, String Backup_filename, File zipFile) {
        Logger.log("Started","ExportData");
        File currentSH = null;
        try {
            String currentDBPath = "/data/data/" + packageName + "/databases/BILLING_SYSTEM";
            String currentSHPath = "/data/data/" + packageName + "/shared_prefs/shared_prefs.xml";
            String copyPath = "BILLING_SYSTEM.db";
            String sharedCopyPath = "shared_prefs.xml";

            File currentDB = new File(currentDBPath);
            currentSH = new File(currentSHPath);

            if (currentDB.exists() && currentSH.exists()) {
                try (FileOutputStream fos = new FileOutputStream(zipFile);
                     ZipOutputStream zos = new ZipOutputStream(fos)) {
                    addToZip(currentDB, copyPath, zos);

                }
                Logger.log("Ended","ExportData");
                return new ExportResult("Successfully exported", Backup_filename,zipFile.getPath());

            }
        } catch (Exception e) {
            Logger.log("Crashed","ExportData");
            e.printStackTrace();
        }
        Logger.log("Ended","ExportData");
        return new ExportResult("Unable to export", null,zipFile.getPath());
    }

    /**
     *
     */
    public static class ExportResult {
        private final String message;
        private final String fileName;
        private final String filepath;
        public ExportResult(String message, String fileName, String filepath) {
            this.message = message;
            this.fileName = fileName;
            this.filepath = filepath;
        }

        public String getFilepath() {
            return filepath;
        }

        public String getMessage() {
            return message;
        }

        public String getFileName() {
            return fileName;
        }
    }

    /**
     * Adds a file to a ZipOutputStream with the specified file name.
     *
     * @param file     The file to add to the zip.
     * @param fileName The file name to use in the zip.
     * @param out      The ZipOutputStream to add the file to.
     * @throws IOException If an I/O error occurs.
     */
    private static void addToZip(File file, String fileName, ZipOutputStream out) throws IOException {
        Logger.log("Started","addToZip");
        try {
            FileInputStream fis = new FileInputStream(file);
            ZipEntry entry = new ZipEntry(fileName);
            out.putNextEntry(entry);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            out.closeEntry();
            fis.close();

        } catch (Exception e) {
            Logger.log("Crashed","addToZip");
        }
        Logger.log("Ended","addToZip");
    }
}
