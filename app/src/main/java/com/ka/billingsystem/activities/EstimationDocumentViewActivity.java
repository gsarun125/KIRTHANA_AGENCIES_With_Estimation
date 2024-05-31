package com.ka.billingsystem.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ka.billingsystem.R;
import com.ka.billingsystem.asynctask.ClassicEstimationTask;
import com.ka.billingsystem.asynctask.ColourPdfGenerationTask;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;

public class EstimationDocumentViewActivity extends AppCompatActivity {
    static PdfRendererView pdfView;
    String  fileName;
    private DataBaseHandler db = new DataBaseHandler(this);
    String customerName;
    String estimatedAmount;
    String gstType;
    private Long CGST = 0L;
    private Long SGST = 0L;
    private Long IGST = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estimation_document_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        if (intent != null) {
            customerName = intent.getStringExtra("cusname");
           estimatedAmount = intent.getStringExtra("estimateAmount"); // 0.0 is the default value
          gstType = intent.getStringExtra("gstType");

            // Now you have the data, you can use it as needed
            // For example, you can set text to TextViews or do whatever you need with the data
        }
        pdfView=findViewById(R.id.EstPDFView);
        File dir = new File(this.getFilesDir(), "DATA");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String qurry = "Select signature,igst,cgst,sgst from user where id='1'";
        Cursor c1 = db.getValue(qurry);
        if (c1.moveToFirst()) {
             @SuppressLint("Range") Long igst = c1.getLong(c1.getColumnIndex("igst"));
            @SuppressLint("Range") Long cgst = c1.getLong(c1.getColumnIndex("cgst"));
            @SuppressLint("Range") Long sgst = c1.getLong(c1.getColumnIndex("sgst"));

            if (gstType.equals("0")) {
                CGST = cgst;
                SGST = sgst;
            } else {
                IGST = igst;
            }
        }

        fileName = "Estimation" + 1 + ".pdf";
        File file = new File(dir, fileName);
        new ClassicEstimationTask(this, pdfView, file,customerName,estimatedAmount,gstType, SGST,CGST, IGST).execute();

    }

    @Override
    public void onBackPressed() {
        Logger.log("Started", "onBackPressed");
        try {
            Intent i = new Intent(this, UserHomePageActivity.class);
            startActivity(i);
            finish();
        } catch (Exception e) {
            Logger.log("Crashed", "onBackPressed");
        }
        Logger.log("Ended", "onBackPressed");
    }
}