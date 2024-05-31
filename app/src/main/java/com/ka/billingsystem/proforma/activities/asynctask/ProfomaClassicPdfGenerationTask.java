package com.ka.billingsystem.proforma.activities.asynctask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.proforma.activities.utils.ProfomaClassicInvoice;
import com.ka.billingsystem.utils.ClassicInvoice;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;
import java.util.List;

public class ProfomaClassicPdfGenerationTask extends AsyncTask<Void, Void, File> {

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
    private File file;
    private String spIsFirstLogo;
    private Long IGST;
    private Long CGST;
    private Long SGST;
    private  PdfRendererView  pdfView;

    private Context context;
    private AlertDialog progressDialog;
    private  long time=0;

    public ProfomaClassicPdfGenerationTask(Context context, PdfRendererView pdfView, int count, Long netAmt, int billNo, String customerName, String phoneNo,
                                           List<String> mQty, List<String> mCost, List<Long> mTotal, List<String> mProductName,
                                           String spIsFirstTime, String spIsFirstLogo, File file, Long IGST, Long CGST, Long SGST, long time) {
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
        this.IGST = IGST;
        this.spIsFirstLogo = spIsFirstLogo;
        this.CGST = CGST;
        this.SGST = SGST;
        this.pdfView = pdfView;
        this.context = context;
        this.time=time;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Logger.log("Started","onPreExecute");
        try{
        ProgressBar progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(progressBar);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();}
        catch (Exception e){
            Logger.log("Crashed","onPreExecute");
        }
        Logger.log("Ended","onPreExecute");
    }

    @Override
    protected File doInBackground(Void... voids) {
        return ProfomaClassicInvoice.classicPDF(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime, spIsFirstLogo, file,time, IGST, CGST, SGST);
    }

    @Override
    protected void onPostExecute(File result) {
        Logger.log("Started","onPostExecute");
        try {
            pdfView.initWithFile(result);
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }catch (Exception e){
            Logger.log("Crashed","onPostExecute");
        }
        Logger.log("Ended","onPostExecute");
    }
}
