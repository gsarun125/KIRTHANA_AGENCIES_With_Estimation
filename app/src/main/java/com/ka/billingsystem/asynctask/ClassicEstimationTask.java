package com.ka.billingsystem.asynctask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.utils.EstimationInvoice;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;

public class ClassicEstimationTask extends AsyncTask<Void, Void, File> {

    private File file;

    private PdfRendererView pdfView;

    private Context context;
    private AlertDialog progressDialog;
    String customerName;
    String estimatedAmount;
    String gstType;
    private final Long sgst;
    private final Long CGST;
    private final Long igst;
    public ClassicEstimationTask(Context context, PdfRendererView pdfView, File file, String customerName, String estimatedAmount, String gstType, Long SGST, Long CGST, Long IGST) {

        this.file = file;

        this.pdfView = pdfView;
        this.context = context;
        this.customerName = customerName;
        this.estimatedAmount = estimatedAmount;
        this.gstType = gstType;
        this.sgst = SGST;
        this.CGST=CGST;
        this.igst=IGST;
    }


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
        return EstimationInvoice.estimationPDF(file,customerName,estimatedAmount,gstType,CGST,sgst,igst );
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
