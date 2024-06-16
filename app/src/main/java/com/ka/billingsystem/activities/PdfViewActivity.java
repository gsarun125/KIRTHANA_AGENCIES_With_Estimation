package com.ka.billingsystem.activities;

import static com.ka.billingsystem.utils.ImageEncodeAndDecode.encodeToBase64;
import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.R;
import com.ka.billingsystem.utils.ImageEncodeAndDecode;
import com.ka.billingsystem.asynctask.ColourPdfGenerationTask;
import com.ka.billingsystem.asynctask.ClassicPdfGenerationTask;
import com.ka.billingsystem.utils.SalesData;
import com.ka.billingsystem.utils.ThumbnailUtils;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;
import java.util.List;

public class PdfViewActivity extends AppCompatActivity {
    private static final String TAG = "PdfViewActivity";
    private DataBaseHandler db = new DataBaseHandler(this);
    static PdfRendererView pdfView;
    private ImageButton share;
    private ImageButton camera;
    private ImageButton textButton;
    private ImageButton delete;
    private ImageButton IBedit;
    private final String SHARED_PREFS = "shared_prefs";
    private final String SHARED_PREFS_Logo = "logo";
    private SharedPreferences sharedpreferences;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private String SPIS_FIRST_TIME;
    private Long IGST = 0L;
    private String SPIS_FIRST_logo;
    private String fileName;
    private Long CGST = 0L;
    private Long SGST = 0L;
    private String salesDataString;
    int Bill_NO;
    private String Customer_Name;
    private String PHone_NO;
    private List<String> mQty;
    private List<String> mProduct_name;
    private List<Long> mTotal;
    private List<String> mCost;
    private int count;
    private Long Net_AMT;

    SalesData  OldsalesData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pdfview);
            pdfView = findViewById(R.id.pdfView);
            share = (ImageButton) findViewById(R.id.share);
            textButton = findViewById(R.id.textButton);
            camera = findViewById(R.id.camera);
            delete = findViewById(R.id.pdfViewdelete);
            IBedit = findViewById(R.id.Imgbtnedit);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

            salesDataString = getIntent().getStringExtra("SalesData");

            Gson gson = new Gson();
            OldsalesData = gson.fromJson(getIntent().getStringExtra("SalesData"), SalesData.class);


            Customer_Name = OldsalesData .getCustomerName();
            PHone_NO = OldsalesData .getPhoneNo();
            mQty = OldsalesData .getmQty();
            mProduct_name = OldsalesData .getmProduct_name();
            mTotal = OldsalesData .getmTotal();
            mCost = OldsalesData .getmCost();
            count = OldsalesData .getCount();
            Net_AMT = OldsalesData .getNet_AMT();


            String qurry = "Select signature,igst,cgst,sgst from user where id='1'";
            Cursor c1 = db.getValue(qurry);
            if (c1.moveToFirst()) {
                @SuppressLint("Range") String signature = c1.getString(c1.getColumnIndex("signature"));
                @SuppressLint("Range") Long igst = c1.getLong(c1.getColumnIndex("igst"));
                @SuppressLint("Range") Long cgst = c1.getLong(c1.getColumnIndex("cgst"));
                @SuppressLint("Range") Long sgst = c1.getLong(c1.getColumnIndex("sgst"));

                if (OldsalesData.getSelectedOption() == 0) {
                    CGST = cgst;
                    SGST = sgst;
                } else {
                    IGST = igst;
                }
                SPIS_FIRST_TIME = signature;
            }


            SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);

            Bill_NO = OldsalesData .getBill_NO();
            fileName = "Invoice" + Bill_NO + ".pdf";

            File dir = new File(this.getFilesDir(), "DATA");
            if (!dir.exists()) {
                dir.mkdir();
            }

            File file = new File(dir, fileName);
            db.filePath(Bill_NO, file.getAbsolutePath(), SPIS_FIRST_TIME, IGST.toString(), CGST.toString(), SGST.toString());
            System.out.println("net check"+Net_AMT);
            new ColourPdfGenerationTask(this, pdfView, count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file, IGST, CGST, SGST, 0).execute();

            LinearLayout backbutton = findViewById(R.id.backbutton);

            IBedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PdfViewActivity.this);
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
                            Intent i = new Intent(PdfViewActivity.this, SalesActivity.class);
                            i.putExtra("dateObject", salesDataString);
                            i.putExtra("id", 1);
                            startActivity(i);
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            setDialogStyle(PdfViewActivity.this,positiveButton, negativeButton);
                        }
                    });
                    alertDialog.show();

                }
            });
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor c1;
                    c1 = db.getValue("SELECT printer_img FROM Transation WHERE Bill_No ='" + Bill_NO + "'");
                    String dataimg = null;
                    if (c1.moveToFirst()) {
                        @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("printer_img"));
                        dataimg = data1;
                    }
                    if (dataimg != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PdfViewActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(PdfViewActivity.this);
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
                                setDialogStyle(PdfViewActivity.this,positiveButton, negativeButton);

                            }

                        });
                        alertDialog.show();
                    } else {
                        openCamera();
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(String.valueOf(Bill_NO));
                }
            });
            textButton.setOnClickListener(new View.OnClickListener() {
                boolean isMethod1 = true;

                @Override
                public void onClick(View v) {
                    if (isMethod1) {
                        textButton.setBackgroundResource(R.drawable.ripple_effect_colour);
                        new ClassicPdfGenerationTask(PdfViewActivity.this, pdfView, count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file, IGST, CGST, SGST, 0).execute();
                    } else {
                        textButton.setBackgroundResource(R.drawable.ripple_effect);
                        new ColourPdfGenerationTask(PdfViewActivity.this, pdfView, count, Net_AMT, Bill_NO, Customer_Name, PHone_NO, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file, IGST, CGST, SGST, 0).execute();
                    }
                    isMethod1 = !isMethod1;
                }
            });


            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    File outputFile;
                    outputFile = new File(dir, fileName);

                    if (outputFile.exists()) {
                        Uri uri = FileProvider.getUriForFile(PdfViewActivity.this, PdfViewActivity.this.getPackageName() + ".provider", outputFile);

                        Intent share = new Intent();
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        share.setAction(Intent.ACTION_SEND);
                        share.setAction(Intent.ACTION_SEND);
                        share.setDataAndType(uri, "application/zip");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(share, "Share"));
                    } else {
                        Toast.makeText(PdfViewActivity.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "An exception onCreate", e);
        }
        Logger.log("Ended", "onCreate");
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

                onBackPressed();
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    setDialogStyle(PdfViewActivity.this,positiveButton, negativeButton);
                }

            });
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "delete");
            Log.e(TAG, "An exception occurred Delete", e);
        }
        Logger.log("Ended", "delete");
    }


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

    private void requestCameraPermission() {
        Logger.log("Started", "requestCameraPermission");
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } catch (Exception e) {
            Logger.log("Crashed", "requestCameraPermission");
            Log.e(TAG, "An exception occurred requestCameraPermission:", e);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.log("Started", "onActivityResult");
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    String imageencoded = encodeToBase64(imageBitmap, Bitmap.CompressFormat.JPEG, 100);
                    Bitmap thumbnail = ThumbnailUtils.createThumbnail(this, imageBitmap);
                    camera.setBackground(null);
                    camera.setImageBitmap(thumbnail);
                    db.printerImage(Bill_NO, imageencoded);
                    Toast.makeText(PdfViewActivity.this, "Printer Image is saved!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onActivityResult");
            Log.e(TAG, "An exception occurred onActivityResult:", e);
        }
        Logger.log("Ended", "onActivityResult");
    }


    @Override
    public void onBackPressed() {
        Logger.log("Started", "onBackPressed");
        try {
            Intent i = new Intent(PdfViewActivity.this, UserHomePageProformaAndSaleActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            Logger.log("Crashed", "onBackPressed");
            Log.e(TAG, "An exception occurred onBackPressed", e);
        }
        Logger.log("Ended", "onBackPressed");
    }
}


