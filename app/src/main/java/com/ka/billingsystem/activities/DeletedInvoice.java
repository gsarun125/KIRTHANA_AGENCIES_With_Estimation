package com.ka.billingsystem.activities;

import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.model.OnpdfDelete;
import com.ka.billingsystem.R;
import com.ka.billingsystem.utils.ImageEncodeAndDecode;
import com.ka.billingsystem.model.DeleteAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeletedInvoice extends AppCompatActivity implements OnpdfDelete {
    private static final String TAG = "DeletedInvoice";
    private DataBaseHandler db = new DataBaseHandler(this);
    private List<String> userName = new ArrayList();
    private List<String> Phoneno = new ArrayList();
    private List<String> billNo = new ArrayList();
    private List<String> tempBillNo = new ArrayList();
    private List<String> amount = new ArrayList();
    private List<String> date = new ArrayList();
    private List<String> time = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> image = new ArrayList();
    private DeleteAdapter pdfAdapter;
    private List<File> pdfList;

    private RecyclerView recyclerView;
    private final String SHARED_PREFS = "shared_prefs";

    private SharedPreferences sharedPreferences;
    private String sharedPrefUser;
    private final String USER_KEY = "user_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", " onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_deleted_invoice);
            sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPrefUser = sharedPreferences.getString(USER_KEY, null);

            LinearLayout backbutton = findViewById(R.id.backbutton_Deleted);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            displayPdf();
        } catch (Exception e) {
            Logger.log("Crashed", " onCreate");
            Log.e(TAG, "An exception occurred onCreate", e);
        }
        Logger.log("Ended", " onCreate");
    }

    /**
     * Display PDFs from the database.
     */
    private void displayPdf() {
        Logger.log("Started", "displayPdf");
        try {
            recyclerView = findViewById(R.id.delrecycler_view);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
            pdfList = new ArrayList<>();
            billNo.clear();
            amount.clear();
            time.clear();
            date.clear();
            mPusername.clear();
            userName.clear();
            Phoneno.clear();
            pdfList.clear();
            tempBillNo.clear();
            image.clear();
            getDeletedData();
            pdfAdapter = new DeleteAdapter(this, pdfList, this, billNo, tempBillNo, amount, date, mPusername, time, userName, Phoneno, image);
            recyclerView.setAdapter(pdfAdapter);
        } catch (Exception e) {
            Logger.log("Crashed", "displayPdf");
            Log.e(TAG, "An exception occurred displayPdf", e);
        }
        Logger.log("Ended", "displayPdf");
    }

    /**
     * Retrieves deleted data from the "Deleted" table in the database and processes it.
     */
    private void getDeletedData() {
        Logger.log("Started", "getDeletedData");
        try {
            Cursor c1;

            c1 = db.getValue("SELECT * FROM (SELECT * FROM Deleted WHERE sales_user='" + sharedPrefUser + "' GROUP BY cus_id ) AS sorted JOIN customer ON sorted.cus_id = customer.cus_id ORDER BY sorted.time DESC");
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String path = c1.getString(c1.getColumnIndex("file_Path"));
                    @SuppressLint("Range") String billNo = c1.getString(c1.getColumnIndex("Bill_No"));
                    @SuppressLint("Range") String tamount = c1.getString(c1.getColumnIndex("tamount"));
                    @SuppressLint("Range") Long time = c1.getLong(c1.getColumnIndex("time"));
                    @SuppressLint("Range") String salesUser = c1.getString(c1.getColumnIndex("sales_user"));
                    @SuppressLint("Range") String cusName = c1.getString(c1.getColumnIndex("cus_name"));
                    @SuppressLint("Range") String cusPhone = c1.getString(c1.getColumnIndex("cus_Phone"));
                    @SuppressLint("Range") String printerImg = c1.getString(c1.getColumnIndex("printer_img"));
                    File file;
                    if (path == null) {
                        file = new File("/storage/emulated/0/DATA/Invoice" + billNo + ".pdf");
                    } else {
                        file = new File(path);
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date res = new Date(time);
                    image.add(printerImg);
                    tempBillNo.add(billNo);
                    this.billNo.add("Bill No: " + billNo);
                    amount.add("Total Amount: " + tamount + " Rs.");
                    this.time.add("Time:" + formatter.format(res));
                    date.add("Generated Date : " + formatter1.format(res));
                    mPusername.add("Generated By: " + salesUser);
                    userName.add("Name: " + cusName);
                    Phoneno.add("Mobile no: " + cusPhone);
                    pdfList.add(file);
                } while (c1.moveToNext());
            }
        } catch (Exception e) {
            Logger.log("Crashed", "getDeletedData");
            Log.e(TAG, "An exception occurred getDeletedData", e);
        }
        Logger.log("Ended", "getDeletedData");
    }

    /**
     * Handle the selection of a PDF file.
     */
    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {
        Logger.log("Started", "onpdfSelected");
        try {
            if (file.exists()) {
                String option = "1";
                Intent i = new Intent(this, DocumentViewActivity.class);
                i.putExtra("billno", mPbillno);
                i.putExtra("option", option);
                i.putExtra("Filepath", file.getAbsolutePath());
                startActivity(i);
            } else {
                showDialogInvoiceNotFound(file, mPbillno);
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onpdfSelected");
            Log.e(TAG, "An exception occurred onpdfSelected", e);
        }
        Logger.log("Ended", "onpdfSelected");
    }

    @Override
    public void Undo(String mPbillno) {

    }


    /**
     * Displays an AlertDialog informing the user about a file being removed from internal storage
     * and provides options to generate the file again or not.
     *
     * @param file   The file that has been removed.
     * @param billNo The associated bill number.
     */
    private void showDialogInvoiceNotFound(File file, String billNo) {
        Logger.log("Started", "showDialogInvoiceNotFound");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
            SpannableStringBuilder title = new SpannableStringBuilder("Alert !");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title);
            builder.setCancelable(true);
            builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
            });
            builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.dismiss();
                String a = "1";
                Intent i = new Intent(this, DocumentViewActivity.class);
                i.putExtra("billno", billNo);
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
                    setDialogStyle(positiveButton, negativeButton);
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            Logger.log("Crashed", "showDialogInvoiceNotFound");
            Log.e(TAG, "An exception occurred showDialogInvoiceNotFound", e);
        }
        Logger.log("Ended", "showDialogInvoiceNotFound");
    }

    /**
     * Display an image from Base64-encoded string.
     */
    @Override
    public void image(String image) {
        Logger.log("Started", "image");
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
            Logger.log("Crashed", "image");
            Log.e(TAG, "An exception occurred image", e);
        }
        Logger.log("Ended", "image");
    }

}