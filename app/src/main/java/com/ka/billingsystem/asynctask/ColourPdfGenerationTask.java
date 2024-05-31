package com.ka.billingsystem.asynctask;


import static com.ka.billingsystem.activities.LoginActivity.driveServiceHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.ka.billingsystem.activities.NetworkUtils;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.utils.Export;
import com.ka.billingsystem.utils.ColourInvoice;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;
import java.util.List;

public class ColourPdfGenerationTask extends AsyncTask<Void, Void, File> {

    private int count;
    private Long netAmt;
    private int billNo;
    private String customerName;
    private String phoneNo;
    private List<String> mQty;
    private List<String> mCost;
    private List<Long> mTotal;
    private List<String> mProductName;
    private String spIsFirstTime;
    private String spIsFirstLogo;
    private File file;
    private Long GST;
    private Long CGST;
    private Long SGST;
    private PdfRendererView pdfView;
    private long time = 0;

    private Context context;

    public ColourPdfGenerationTask(Context context, PdfRendererView pdfView, int count, Long netAmt, int billNo, String customerName, String phoneNo,
                                   List<String> mQty, List<String> mCost, List<Long> mTotal, List<String> mProductName,
                                   String spIsFirstTime, String spIsFirstLogo, File file, Long GST, Long CGST, Long SGST, long time) {
        this.count = count;
        this.netAmt = netAmt;
        this.billNo = billNo;
        this.customerName = customerName;
        this.phoneNo = phoneNo;
        this.mQty = mQty;
        this.mCost = mCost;
        this.mTotal = mTotal;
        this.mProductName = mProductName;
        this.spIsFirstTime = spIsFirstTime;
        this.file = file;
        this.GST = GST;
        this.spIsFirstLogo = spIsFirstLogo;
        this.CGST = CGST;
        this.SGST = SGST;
        this.pdfView = pdfView;
        this.context = context;
        this.time = time;
    }

    private AlertDialog progressDialog;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logger.log("Started", "onPreExecute");
        try {
            ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
            progressBar.setIndeterminate(true);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            progressBar.setLayoutParams(params);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(progressBar);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "onPreExecute");
        }
        Logger.log("Ended", "onPreExecute");
    }

    @Override
    protected File doInBackground(Void... voids) {
      Export.ExportResult data= Export.ExportData(context.getPackageName(), "Kirthana_backup.zip", create("Kirthana_backup.zip"));
        if (NetworkUtils.isNetworkAvailable(context)) {
            System.out.println("not conected");
            uploadPDFFile(data.getFilepath());
        }
        return ColourInvoice.colourPDF(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime, spIsFirstLogo, file, time, GST, CGST, SGST);
    }

    public File create(String Backup_filename) {
        Logger.log("Started", "create");
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.Q) {
            File dir = new File(Environment.getExternalStorageDirectory(), "KIRTHANA AGENCIES");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File subdir = new File(dir, "Backup");
            if (!subdir.exists()) {
                subdir.mkdirs();
            }

            File zipFile = new File(subdir, Backup_filename);
            Logger.log("Ended", "create");
            return zipFile;
        } else {
            File logFile = new File(context.getFilesDir(), "KIRTHANA AGENCIES");
            return logFile;
        }
    }

    public void uploadPDFFile(String filepath) {
        Log.d("fileGD", "uploadPDFFile() called");

        System.out.println(filepath);

        if (driveServiceHelper != null) {
            Log.d("fileGD", "Uploading file with DriveServiceHelper");
            driveServiceHelper.createFilePDF(filepath).addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Toast.makeText(context, "File uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("fileGD", "Error uploading file: " + e.getMessage());
                            Toast.makeText(context, "Error uploading file", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onPostExecute(File result) {
        Logger.log("Started", "onPostExecute");
        try {
            pdfView.initWithFile(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onPostExecute");
        }
        Logger.log("Ended", "onPostExecute");
    }
}
