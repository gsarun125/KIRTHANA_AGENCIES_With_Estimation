package com.ka.billingsystem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ka.billingsystem.R;
import com.ka.billingsystem.crash.Logger;
import com.rajat.pdfviewer.PdfRendererView;

import java.io.File;

public class ReportPDFView extends AppCompatActivity {
    private static final String TAG = "ReportPDFView";
    private PdfRendererView pdfView;
    private ImageButton share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started","onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_report_pdfview);
            pdfView = findViewById(R.id.pdfView);
            share = (ImageButton) findViewById(R.id.share);
            Intent intent = getIntent();
            File reportFile = (File) intent.getSerializableExtra("ReportFile");
            pdfView.initWithFile(reportFile);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    share(reportFile);
                }

            });
            LinearLayout backbutton = findViewById(R.id.backbutton);
            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();
                }
            });
        } catch (Exception e) {
            Logger.log("Crashed","onCreate");
            Log.e(TAG, "error in onCreate", e);
        }
        Logger.log("Ended","onCreate");
    }

    /**
     * Handle the sharing of the PDF file.
     */
    private void share(File file) {
        Logger.log("Started","share");
        try {
            File outputFile;
            outputFile = file;
            if (outputFile.exists()) {
                Uri uri = FileProvider.getUriForFile(ReportPDFView.this, ReportPDFView.this.getPackageName() + ".provider", outputFile);
                Intent share = new Intent();
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.setAction(Intent.ACTION_SEND);
                share.setAction(Intent.ACTION_SEND);
                share.setDataAndType(uri, "application/zip");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, "Share"));
            } else {
                Toast.makeText(ReportPDFView.this, R.string.file_not_found, Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Logger.log("Crashed","share");
            Log.e(TAG, "An exception occurred Share", e);
        }
        Logger.log("Ended","share");
    }

}