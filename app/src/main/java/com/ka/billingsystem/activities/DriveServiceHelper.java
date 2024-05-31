package com.ka.billingsystem.activities;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    public DriveServiceHelper(Drive mDriveService) {
        this.mDriveService = mDriveService;
    }

    public Task<String> createFileUnique(String filePath) {
        return Tasks.call(mExecutor, () -> {
            String folderName = "Kirthana Backup";
            String folderId = createOrGetFolder(folderName);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh_mm_ssa", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            File fileMetaData = new File();
            String fileName="Kirthana_backup_" + currentDateAndTime;
            fileMetaData.setName(fileName);
            fileMetaData.setParents(Collections.singletonList(folderId));

            java.io.File file = new java.io.File(filePath);

            FileContent mediaContent = new FileContent("application/zip", file);
            File fileUploaded = mDriveService.files().create(fileMetaData, mediaContent)
                    .setFields("id")
                    .execute();

            if (fileUploaded== null) {
                throw new IOException("Null result when requesting file.");
            }
            return fileUploaded.getId();
        });
    }
    public Task<String> createFilePDF(String filePath) {
        return Tasks.call(mExecutor, () -> {
            // Check if a folder with the same name already exists
            String folderName = "Kirthana Backup";
            String folderId = createOrGetFolder(folderName);

            // Check if the file exists in the folder
            String fileId = getFileIdInFolder("Kirthana_backup", folderId);

            // If file exists, delete it
            if (fileId != null) {
                mDriveService.files().delete(fileId).execute();
            }

            // Create file metadata
            File fileMetadata = new File();
            fileMetadata.setName("Kirthana_backup");
            fileMetadata.setParents(Collections.singletonList(folderId));

            // Create file content
            java.io.File file = new java.io.File(filePath);
            FileContent mediaContent = new FileContent("application/zip", file);

            // Upload the file to Drive
            File fileUploaded = mDriveService.files().create(fileMetadata, mediaContent)
                    .setFields("id")
                    .execute();

            if (fileUploaded == null) {
                throw new IOException("Null result when uploading file.");
            }

            return fileUploaded.getId();
        });
    }

    private String createOrGetFolder(String folderName) throws IOException {
        // Check if the folder exists
        FileList fileList = mDriveService.files().list()
                .setQ("mimeType='application/vnd.google-apps.folder' and name='" + folderName + "' and trashed=false")
                .execute();
        List<File> folders = fileList.getFiles();

        if (folders != null && !folders.isEmpty()) {
            // Folder already exists, return its ID
            return folders.get(0).getId();
        } else {
            // Folder doesn't exist, create a new folder
            File folderMetadata = new File();
            folderMetadata.setName(folderName);
            folderMetadata.setMimeType("application/vnd.google-apps.folder");

            File folder = mDriveService.files().create(folderMetadata)
                    .setFields("id")
                    .execute();

            if (folder == null) {
                throw new IOException("Null result when creating folder.");
            }

            return folder.getId();
        }
    }

    private String getFileIdInFolder(String fileName, String folderId) throws IOException {
        // Check if the file exists in the folder
        FileList fileList = mDriveService.files().list()
                .setQ("name='" + fileName + "' and '" + folderId + "' in parents and trashed=false")
                .execute();
        List<File> files = fileList.getFiles();

        if (files != null && !files.isEmpty()) {
            // File exists in the folder, return its ID
            return files.get(0).getId();
        } else {
            // File doesn't exist in the folder
            return null;
        }
    }
}
