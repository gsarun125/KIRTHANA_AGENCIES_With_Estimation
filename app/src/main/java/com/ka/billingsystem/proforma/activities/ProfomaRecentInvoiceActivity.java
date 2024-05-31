package com.ka.billingsystem.proforma.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.DocumentViewActivity;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;
import com.ka.billingsystem.utils.ImageEncodeAndDecode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfomaRecentInvoiceActivity extends AppCompatActivity implements OnPdfFileSelectListener {

    private DataBaseHandler db = new DataBaseHandler(this);
    private static final String TAG = "RecentInvoiceActivity";
    private String SHARED_PREFS = "shared_prefs";
    private String USER_KEY = "user_key";
    private String SPuser;

    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> image = new ArrayList();
    private SharedPreferences sharedpreferences;

    private PdfAdapter pdfAdapter;
    private List<File> pdfList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recent_invoice_proforma);
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            SPuser = sharedpreferences.getString(USER_KEY, null);

            recyclerView = findViewById(R.id.recycler_view);

            LinearLayout backbutton = findViewById(R.id.backbutton_Recent);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
            displayPdf();
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "An exception occurred onCreate", e);
        }
        Logger.log("Ended", "onCreate");

    }


    /**
     * Display all Recent Invoice from db
     */

    private void displayPdf() {

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            pdfList = new ArrayList<>();

            mPbillno.clear();
            mPtamount.clear();
            mPtime.clear();
            mPDate.clear();
            mPusername.clear();
            mPcusname.clear();
            mPcusPhoneno.clear();
            pdfList.clear();
            tempbillno.clear();
            image.clear();

            Cursor c1;
            if (SPuser.equals("admin")) {
                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation GROUP BY proforma_cus_id ) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id ORDER BY sorted.proforma_time DESC LIMIT 10");
            } else {
                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user=" + "'" + SPuser + "' GROUP BY proforma_cus_id) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id ORDER BY sorted.proforma_time DESC LIMIT 10");
            }
        System.out.println(c1.getCount()+"ffff");
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String path = c1.getString(c1.getColumnIndex("proforma_file_Path"));
                    @SuppressLint("Range") String billNo = c1.getString(c1.getColumnIndex("proforma_Bill_No"));
                    @SuppressLint("Range") String tamount = c1.getString(c1.getColumnIndex("proforma_tamount"));

                    @SuppressLint("Range") Long time = c1.getLong(c1.getColumnIndex("proforma_time"));

                    @SuppressLint("Range") String salesUser = c1.getString(c1.getColumnIndex("proforma_sales_user"));
                    @SuppressLint("Range") String cusName = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                    @SuppressLint("Range") String cusPhone = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                    @SuppressLint("Range") String printerImg = c1.getString(c1.getColumnIndex("proforma_printer_img"));

                    File file;
                    if (path == null) {
                        file = new File("/storage/emulated/0/DATA/Profoma_Invoice" + billNo + ".pdf");
                    } else {
                        file = new File(path);
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date res = new Date(time);
                    image.add(printerImg);
                    tempbillno.add(billNo);
                    mPbillno.add("Bill No: " + billNo);
                    mPtamount.add("Total Amount: " + tamount + " Rs.");
                    mPtime.add("Time:" + formatter.format(res));
                    mPDate.add("Generated Date : " + formatter1.format(res));
                    mPusername.add("Generated By: " + salesUser);
                    mPcusname.add("Name: " + cusName);
                    mPcusPhoneno.add("Mobile no: " + cusPhone);
                    pdfList.add(file);

                } while (c1.moveToNext());
            }
            pdfAdapter = new PdfAdapter(this, pdfList, this, mPbillno, tempbillno, mPtamount, mPDate, mPusername, mPtime, mPcusname, mPcusPhoneno, image);
            recyclerView.setAdapter(pdfAdapter);

    }

    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {
        System.out.println(file.getAbsolutePath()+"kjhgf");
        Logger.log("Started", "onpdfSelected");
        try {
            if (file.exists()) {

                String a = "0";
                Intent i = new Intent(this, ProfomaDocumentViewActivity.class);
                i.putExtra("billno", mPbillno);
                i.putExtra("option", a);
                i.putExtra("Filepath", file.getAbsolutePath());
                startActivity(i);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
                SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
                title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setTitle(title);

                builder.setCancelable(true);
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent(ProfomaRecentInvoiceActivity.this,ProfomaDocumentViewActivity.class);

                    String a = "0";
                    i.putExtra("billno", mPbillno);
                    i.putExtra("option", a);
                    i.putExtra("Filepath", file.getAbsolutePath());
                    startActivity(i);

                });
                AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);


                        SpannableString spannableStringPositive = new SpannableString("Ok");
                        spannableStringPositive.setSpan(new AbsoluteSizeSpan(18, true), 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        positiveButton.setText(spannableStringPositive);
                        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);

                        // Set a custom text size for the negative button
                        SpannableString spannableStringNegative = new SpannableString("Cancel");
                        spannableStringNegative.setSpan(new AbsoluteSizeSpan(18, true), 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        negativeButton.setText(spannableStringNegative);

                        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
                    }

                });
                alertDialog.show();

            }
        } catch (Exception e) {
            Logger.log("Crashed", "onpdfSelected");
            Log.e(TAG, "An exception occurred onpdfSelected:", e);
        }
        Logger.log("Ended", "onpdfSelected");
    }

    @Override
    public void image(String image) {
        Logger.log("Started", "onpdfSelected");
        try {
            Bitmap Printerimg = ImageEncodeAndDecode.decodeBase64ToBitmap(image);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.image_dialog_layout, null);
            dialogBuilder.setView(dialogView);
            ImageView imageView = dialogView.findViewById(R.id.dialogImageView);
            imageView.setImageBitmap(Printerimg);
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "onpdfSelected");
            Log.e(TAG, "An exception occurred image:", e);
        }
        Logger.log("Ended", "onpdfSelected");
    }
}