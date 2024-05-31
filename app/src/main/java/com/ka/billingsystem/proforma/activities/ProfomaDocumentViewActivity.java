package com.ka.billingsystem.proforma.activities;


import static com.ka.billingsystem.utils.ImageEncodeAndDecode.encodeToBase64;
import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.DeletedInvoice;
import com.ka.billingsystem.activities.HistoryActivity;
import com.ka.billingsystem.activities.RecentInvoiceActivity;
import com.ka.billingsystem.activities.SalesActivity;
import com.ka.billingsystem.asynctask.ClassicPdfGenerationTask;
import com.ka.billingsystem.asynctask.ColourPdfGenerationTask;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.proforma.activities.asynctask.ProfomaClassicPdfGenerationTask;
import com.ka.billingsystem.proforma.activities.asynctask.ProfomaColourPdfGenerationTask;
import com.ka.billingsystem.utils.ImageEncodeAndDecode;
import com.ka.billingsystem.utils.SalesData;
import com.ka.billingsystem.utils.ThumbnailUtils;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfomaDocumentViewActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    private static final String TAG = "DocumentViewActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private String filePath = "";
    private ImageButton share;
    private File file;
    private ImageButton delete;
    private ImageButton undo;
    private ImageButton changePdf;
    private  ImageButton editPdf;
    private String billno;
    private String option;

    private final String SHARED_PREFS_Logo = "logo";
    private final String ADMIN_LOGIN = "admin_login";
    private final String SHARED_PREFS = "shared_prefs";
    private SharedPreferences sharedpreferences;
    private PdfRendererView pdfView;
    private ImageButton camera;
    private String dataimg = null;
    private String Admin_login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_docment_profoma);
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            Admin_login = sharedpreferences.getString(ADMIN_LOGIN, null);
            pdfView = findViewById(R.id.pdfView3);
            share = (ImageButton) findViewById(R.id.share1);
            delete = (ImageButton) findViewById(R.id.delete);
            undo = (ImageButton) findViewById(R.id.Undo1);
            changePdf = (ImageButton) findViewById(R.id.ChangePdf);
            camera = findViewById(R.id.Dcamera);

            editPdf=findViewById(R.id.editPdf);

            option = getIntent().getStringExtra("option");
            billno = getIntent().getStringExtra("billno");
            filePath = getIntent().getStringExtra("Filepath");

            file = new File(filePath);

            classicPdf();

            setVisibleIcon();
            LinearLayout backbutton = findViewById(R.id.backbutton_document);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });

            editPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProfomaDocumentViewActivity.this);
                    builder.setMessage("Do you want to go edit ?");
                    SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
                    title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setTitle(title);

                    builder.setCancelable(true);
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.you_press_cancel_button, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editData();
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            setDialogStyle(positiveButton, negativeButton);
                        }
                    });
                    alertDialog.show();
                }
            });
            undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    undo(billno);
                }
            });
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share();
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (option.equals("0") || option.equals("2")) {

                        delete(billno);
                    } else {
                        permanentDeletion(billno);
                    }

                }
            });

            changePdf.setOnClickListener(new View.OnClickListener() {
                boolean isMethod1 = true;

                @Override
                public void onClick(View view) {

                    if (isMethod1) {
                        changePdf.setBackgroundResource(R.drawable.ripple_effect_colour);
                        colourPDF();

                    } else {
                        changePdf.setBackgroundResource(R.drawable.ripple_effect);
                        classicPdf();
                    }
                    isMethod1 = !isMethod1;
                }
            });

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor c1;
                    if (option.equals("1")) {
                        c1 = db.getValue("SELECT proforma_printer_img FROM proforma_Deleted WHERE proforma_Bill_No ='" + billno + "'");

                    } else {
                        c1 = db.getValue("SELECT proforma_printer_img FROM proforma_Transation WHERE proforma_Bill_No ='" + billno + "'");
                    }

                    if (c1.moveToFirst()) {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("proforma_printer_img"));
                        dataimg = data1;
                    }

                    if (dataimg != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ProfomaDocumentViewActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(ProfomaDocumentViewActivity.this);
                        View customLayout = inflater.inflate(R.layout.custom_alert_img, null);
                        Bitmap Printerimg = ImageEncodeAndDecode.decodeBase64ToBitmap(dataimg);

                        ImageView customImageView = customLayout.findViewById(R.id.customImageView);
                        customImageView.setImageBitmap(Printerimg);

                        builder.setView(customLayout);
                        builder.setCancelable(true);
                        builder.setNegativeButton(R.string.no, (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            openCamera();
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                setDialogStyle(positiveButton, negativeButton);
                            }
                        });
                        alertDialog.show();
                    } else {
                        openCamera();
                    }
                }
            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "An exception occurred onCreate:", e);
        }
        Logger.log("Ended", "onCreate");
    }

    private void editData() {
        String cusname = null;

        String phoneno = null;
        Long time = 0l;
        List<String> mQty = new ArrayList();
        List<String> mProduct_name = new ArrayList();
        List<Long> mTotal = new ArrayList();
        List<String> mCost = new ArrayList();
        int count = 0;
        long Net_AMT = 0;

        String SPIS_FIRST_TIME = null;
        Long IGST = null;
        Cursor c1;
        Long CGST = null;
        Long SGST = null;

        if (option.equals("1")) {
            c1 = db.getValue("SELECT * FROM proforma_Deleted INNER JOIN proforma_customer ON  proforma_Deleted.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");

        } else {
            c1 = db.getValue("SELECT * FROM proforma_Transation INNER JOIN proforma_customer ON  proforma_Transation.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");
        }
        if (c1.moveToFirst()) {
            do {
                @SuppressLint("Range") String quantity = c1.getString(c1.getColumnIndex("proforma_quantity"));
                @SuppressLint("Range")  String rate = c1.getString(c1.getColumnIndex("proforma_rate"));

                @SuppressLint("Range") Long amount = c1.getLong(c1.getColumnIndex("proforma_amount"));
                @SuppressLint("Range") String productName = c1.getString(c1.getColumnIndex("proforma_Product_Name"));
                @SuppressLint("Range") String cusName = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                @SuppressLint("Range") String cusPhone = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                @SuppressLint("Range") Long time1 = c1.getLong(c1.getColumnIndex("proforma_time"));
                @SuppressLint("Range") String signature = c1.getString(c1.getColumnIndex("proforma_signature"));
                @SuppressLint("Range") Long igst = c1.getLong(c1.getColumnIndex("proforma_igst"));
                @SuppressLint("Range") Long cgst = c1.getLong(c1.getColumnIndex("proforma_cgst"));
                @SuppressLint("Range") Long sgst = c1.getLong(c1.getColumnIndex("proforma_sgst"));
                mQty.add(quantity);
                mCost.add(rate);
                mTotal.add(amount);
                mProduct_name.add(productName);
                if (cusname == null || phoneno == null || time == 0l) {
                    cusname = cusName;
                    phoneno = cusPhone;
                    SPIS_FIRST_TIME = signature;
                    time = time1;
                    IGST = igst;
                    CGST = cgst;
                    SGST = sgst;
                }
                count++;
            } while (c1.moveToNext());
        }

        Long Net_AMT1 = 0l;
        for (Long total : mTotal) {
            Net_AMT1 = Net_AMT1 + total;
        }
        SalesData salesData = new SalesData();

        salesData.setBill_NO(Integer.parseInt(billno));

        salesData.setCustomerName(cusname);
        salesData.setPhoneNo( phoneno);
        salesData.setmQty(mQty);
        salesData.setmCost(mCost);
        if (IGST != 0L) {
            salesData.setSelectedOption(1);
        }else {
            salesData.setSelectedOption(0);

        }
        salesData.setAdd_count(mProduct_name.size());
        salesData.setmProduct_name(mProduct_name);
        salesData.setNet_AMT(Net_AMT1);
        salesData.setmTotal(mTotal);
        salesData.setCount(count);

        Gson gson = new Gson();
        String jsonStringSalesData = gson.toJson(salesData);
        Intent i = new Intent(ProfomaDocumentViewActivity.this, ProformaSalesActivity.class);
        i.putExtra("dateObject", jsonStringSalesData);
        i.putExtra("id", 1);
        startActivity(i);

    }

    /**
     * Set visible icon based on the option and display a circular thumbnail.
     */
    private void setVisibleIcon() {
        Logger.log("Started", "setVisibleIcon");
        try {
            Cursor c1;
            if (option.equals("1")) {
                c1 = db.getValue("SELECT proforma_printer_img FROM proforma_Deleted WHERE proforma_Bill_No ='" + billno + "'");

            } else {
                c1 = db.getValue("SELECT proforma_printer_img FROM proforma_Transation WHERE proforma_Bill_No ='" + billno + "'");
            }

            if (c1.moveToFirst()) {
                @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("proforma_printer_img"));
                dataimg = data1;
            }

            if (dataimg != null) {
                Bitmap printerimg = ImageEncodeAndDecode.decodeBase64ToBitmap(dataimg);
                Bitmap thumbnail = ThumbnailUtils.createThumbnail(this, printerimg);
                camera.setBackground(null);
                camera.setImageBitmap(thumbnail);
            }
            if (option.equals("0") || option.equals("2")) {
                undo.setVisibility(View.GONE);
            }

            if (Admin_login.equals("true")) {
                undo.setVisibility(View.GONE);
                delete.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.log("Crashed", "setVisibleIcon");
            Log.e(TAG, "An exception occurred setVisibleIcon():", e);
        }
        Logger.log("Ended", "setVisibleIcon");
    }

    @Override
    public void onBackPressed() {
        Logger.log("Started", "onBackPressed");
        try {
            if (option.equals("0")) {
                Intent intent = new Intent(this, ProfomaRecentInvoiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (option.equals("2")) {
                Intent intent = new Intent(this, ProformaHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else if (option.equals("1")) {
                Intent intent = new Intent(this, ProformaHistoryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onBackPressed");
        }
        Logger.log("Ended", "onBackPressed");

    }

    /**
     * open Camera
     */
    private void openCamera() {
        Logger.log("Started", "openCamera");
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            } else {
                requestCameraPermission();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "openCamera");
            Log.e(TAG, "An exception occurred openCamera", e);
        }
        Logger.log("Ended", "openCamera");
    }

    /**
     * Request Camera permission
     */
    private void requestCameraPermission() {
        Logger.log("Started", "requestCameraPermission");
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } catch (Exception e) {
            Logger.log("Crashed", "requestCameraPermission");
            Log.e(TAG, "An exception occurred requestCameraPermission :", e);
        }
        Logger.log("Ended", "requestCameraPermission");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Logger.log("Started", "onRequestPermissionsResult");
        try {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == CAMERA_PERMISSION_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onRequestPermissionsResult");
            Log.e(TAG, "An exception occurred onRequestPermissionsResult:", e);
        }
        Logger.log("Ended", "onRequestPermissionsResult");
    }

    /**
     * onActivityResult the captured image is stored in db
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log("Started", "onActivityResult");
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    String imageencoded = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 100);
                    //Bitmap printerimg = ImageEncodeAndDecode.decodeBase64ToBitmap(dataimg);
                    Bitmap thumbnail = ThumbnailUtils.createThumbnail(this, imageBitmap);
                    camera.setBackground(null);
                    camera.setImageBitmap(thumbnail);

                    if (option.equals("1")) {
                        db.printerImageDEl(Integer.parseInt(billno), imageencoded);
                    } else {
                        db.printerImage(Integer.parseInt(billno), imageencoded);
                    }
                    Toast.makeText(this, "Printer Image is saved!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onActivityResult");
            Log.e(TAG, "An exception occurred onActivityResult:", e);
        }
        Logger.log("Ended", "onActivityResult");
    }

    private void classicPdf() {
        Logger.log("Started", "classicPdf");
        try {
            String cusname = null;

            String phoneno = null;
            Long time = 0l;
            List<String> mQty = new ArrayList();
            List<String> mProduct_name = new ArrayList();
            List<Long> mTotal = new ArrayList();
            List<String> mCost = new ArrayList();
            int count = 0;
            long Net_AMT = 0;

            String SPIS_FIRST_TIME = null;
            Long IGST = null;
            Cursor c1;
            Long CGST = null;
            Long SGST = null;

            if (option.equals("1")) {
                c1 = db.getValue("SELECT * FROM proforma_Deleted INNER JOIN proforma_customer ON  proforma_Deleted.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");

            } else {
                c1 = db.getValue("SELECT * FROM proforma_Transation INNER JOIN proforma_customer ON  proforma_Transation.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");
            }
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String quantity = c1.getString(c1.getColumnIndex("proforma_quantity"));
                    @SuppressLint("Range") String rate = c1.getString(c1.getColumnIndex("proforma_rate"));

                    @SuppressLint("Range") Long amount = c1.getLong(c1.getColumnIndex("proforma_amount"));
                    @SuppressLint("Range") String productName = c1.getString(c1.getColumnIndex("proforma_Product_Name"));
                    @SuppressLint("Range") String cusName = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                    @SuppressLint("Range") String cusPhone = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                    @SuppressLint("Range") Long time1 = c1.getLong(c1.getColumnIndex("proforma_time"));
                    @SuppressLint("Range") String signature = c1.getString(c1.getColumnIndex("proforma_signature"));
                    @SuppressLint("Range") Long igst = c1.getLong(c1.getColumnIndex("proforma_igst"));
                    @SuppressLint("Range") Long cgst = c1.getLong(c1.getColumnIndex("proforma_cgst"));
                    @SuppressLint("Range") Long sgst = c1.getLong(c1.getColumnIndex("proforma_sgst"));
                    mQty.add(quantity);
                    mCost.add(rate);
                    mTotal.add(amount);
                    mProduct_name.add(productName);
                    if (cusname == null || phoneno == null || time == 0l) {
                        cusname = cusName;
                        phoneno = cusPhone;
                        SPIS_FIRST_TIME = signature;
                        time = time1;
                        IGST = igst;
                        CGST = cgst;
                        SGST = sgst;
                    }
                    count++;
                } while (c1.moveToNext());
            }
            for (Long total : mTotal) {

                Net_AMT = Net_AMT + total;

            }
            String fileName = "Invoice" + billno + ".pdf";
            String SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);
            File dir = new File(this.getFilesDir(), "DATA");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file2 = new File(dir, fileName);
            new ProfomaColourPdfGenerationTask(ProfomaDocumentViewActivity.this, pdfView, count, Net_AMT, Integer.parseInt(billno), cusname, phoneno, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file2, IGST, CGST, SGST, time).execute();
        } catch (Exception e) {
            Logger.log("Crashed", "classicPdf");
            Log.e(TAG, "An exception occurred ClassicPdf:", e);

        }
        Logger.log("Ended", "classicPdf");
    }

    private void colourPDF() {
        Logger.log("Started", "colourPDF");
        try {
            String cusname = null;
            String phoneno = null;
            Long time = 0l;
            List<String> mQty = new ArrayList();
            List<String> mProduct_name = new ArrayList();
            List<Long> mTotal = new ArrayList();
            List<String> mCost = new ArrayList();
            int count = 0;
            long Net_AMT = 0;

            String SPIS_FIRST_TIME = null;
            Long IGST = null;
            Long CGST = null;
            Long SGST = null;

            Cursor c1;
            if (option.equals("1")) {
                c1 = db.getValue("SELECT * FROM proforma_Deleted INNER JOIN proforma_customer ON  proforma_Deleted.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");

            } else {
                c1 = db.getValue("SELECT * FROM proforma_Transation INNER JOIN proforma_customer ON  proforma_Transation.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + billno + "'");
            }
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String quantity = c1.getString(c1.getColumnIndex("proforma_quantity"));
                    @SuppressLint("Range") String rate = c1.getString(c1.getColumnIndex("proforma_rate"));

                    @SuppressLint("Range") Long amount = c1.getLong(c1.getColumnIndex("proforma_amount"));
                    @SuppressLint("Range") String productName = c1.getString(c1.getColumnIndex("proforma_Product_Name"));
                    @SuppressLint("Range") String cusName = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                    @SuppressLint("Range") String cusPhone = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                    @SuppressLint("Range") Long time1 = c1.getLong(c1.getColumnIndex("proforma_time"));
                    @SuppressLint("Range") String signature = c1.getString(c1.getColumnIndex("proforma_signature"));
                    @SuppressLint("Range") Long igst = c1.getLong(c1.getColumnIndex("proforma_igst"));
                    @SuppressLint("Range") Long cgst = c1.getLong(c1.getColumnIndex("proforma_cgst"));
                    @SuppressLint("Range") Long sgst = c1.getLong(c1.getColumnIndex("proforma_sgst"));

                    mQty.add(quantity);
                    mCost.add(rate);
                    mTotal.add(amount);
                    mProduct_name.add(productName);
                    if (cusname == null || phoneno == null || time == 0l) {
                        cusname = cusName;
                        phoneno = cusPhone;
                        SPIS_FIRST_TIME = signature;
                        time = time1;
                        IGST = igst;
                        CGST = cgst;
                        SGST = sgst;
                    }
                    count++;
                } while (c1.moveToNext());
            }
            for (Long total : mTotal) {
                Net_AMT = Net_AMT + total;
            }
            String fileName = "Invoice" + billno + ".pdf";
            String SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);
            File dir = new File(this.getFilesDir(), "DATA");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file2 = new File(dir, fileName);
            new ProfomaClassicPdfGenerationTask(ProfomaDocumentViewActivity.this, pdfView, count, Net_AMT, Integer.parseInt(billno), cusname, phoneno, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file2, IGST, CGST, SGST, time).execute();
        } catch (Exception e) {
            Logger.log("Crashed", "colourPDF");
            Log.e(TAG, "An exception occurred colourPDF:", e);
        }
        Logger.log("Ended", "colourPDF");
    }


    /**
     * Handle the sharing of the PDF file.
     */
    private void share() {
        Logger.log("Started", "share");
        try {
            File outputFile;
            outputFile = file;
            if (outputFile.exists()) {
                Uri uri = FileProvider.getUriForFile(ProfomaDocumentViewActivity.this, ProfomaDocumentViewActivity.this.getPackageName() + ".provider", outputFile);
                Intent share = new Intent();
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setAction(Intent.ACTION_SEND);
                share.setAction(Intent.ACTION_SEND);
                share.setDataAndType(uri, "application/zip");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share"));
            } else {
                Toast.makeText(ProfomaDocumentViewActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "share");
            Log.e(TAG, "An exception occurred share:", e);
        }
        Logger.log("Ended", "share");
    }

    /**
     * Handle the deletion of an invoice.
     */
    public void delete(String mPbillno) {
        Logger.log("Started", "delete");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_delete);
            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.moveDataFromTable2ToTable5(mPbillno);
                if (option.equals("0")) {
                    Intent i = new Intent(ProfomaDocumentViewActivity.this, ProfomaRecentInvoiceActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    Intent i = new Intent(ProfomaDocumentViewActivity.this, ProformaHistoryActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(positiveButton, negativeButton);
                }

            });
            alertDialog.show();
        } catch (Exception e) {

            Logger.log("Crashed", "delete");
            Log.e(TAG, "An exception occurred delete:", e);
        }
        Logger.log("Ended", "delete");
    }

    /**
     * Handle the permanent deletion of an invoice.
     */
    private void permanentDeletion(String billno) {
        Logger.log("Started", "permanentDeletion");
        try {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to Delete permanently..?");

            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            SpannableString spannableMessage = new SpannableString(getString(R.string.do_you_want_to_delete));
            spannableMessage.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableMessage.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            SpannableString spannableTitle = new SpannableString(getString(R.string.alert));
            spannableTitle.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setMessage(spannableMessage);
            builder.setTitle(spannableTitle);

            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });


            SpannableString spannableYes = new SpannableString(getString(R.string.yes));
            spannableYes.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableYes.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.setPositiveButton(spannableYes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.permanentDelete(billno);
                Intent i = new Intent(ProfomaDocumentViewActivity.this, DeletedInvoice.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(positiveButton, negativeButton);
                }

            });
            alertDialog.show();

        } catch (Exception e) {
            Logger.log("Crashed", "permanentDeletion");
            Log.e(TAG, "An exception occurred permanentDeletion:", e);
        }
        Logger.log("Ended", "permanentDeletion");
    }

    /**
     * Handle the undo action for an invoice.
     */
    public void undo(String mPbillno) {
        Logger.log("Started", "undo");
        try {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage(R.string.do_you_want_to_undo);

            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);

            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                db.undoMoveDataFromTable2ToTable5(mPbillno);
                Intent i = new Intent(ProfomaDocumentViewActivity.this, ProfomaDeletedInvoice.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);


            });
            androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "undo");
            Log.e(TAG, "An exception occurred undo:", e);
        }
        Logger.log("Ended", "undo");
    }
}
