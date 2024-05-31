package com.ka.billingsystem.proforma.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.DocumentViewActivity;
import com.ka.billingsystem.activities.ReportPDFView;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.databinding.ActivityHistoryProformaBinding;
import com.ka.billingsystem.model.OnPdfFileSelectListener;
import com.ka.billingsystem.model.PdfAdapter;
import com.ka.billingsystem.proforma.activities.utils.ProfomaColourInvoice;
import com.ka.billingsystem.utils.ColourInvoice;
import com.ka.billingsystem.utils.Export;
import com.ka.billingsystem.utils.ImageEncodeAndDecode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProformaHistoryActivity extends AppCompatActivity implements OnPdfFileSelectListener {
    private ActivityHistoryProformaBinding activityHistoryProformaBinding;
    private static final String TAG = "HistoryActivity";
    private List<String> mPbillno = new ArrayList();
    private List<String> tempbillno = new ArrayList();
    private List<String> mPtamount = new ArrayList();
    private List<String> mPDate = new ArrayList();
    private List<String> mPtime = new ArrayList();
    private List<String> mPusername = new ArrayList();
    private List<String> mPcusname = new ArrayList();
    private List<String> mPcusPhoneno = new ArrayList();
    private List<String> image = new ArrayList();
    private PdfAdapter pdfAdapter;
    private List<File> pdfList;
    private Long fromDate = 0l;
    private Long toDate = 0l;
    private RecyclerView recyclerView;
    private HashSet<String> autoCompletion = new HashSet<>();
    private EditText editTextDatePickerFrom;
    private EditText editTextDatePickerTo;
    private Calendar calendar;
    private SharedPreferences sharedpreferences;
    private final String SHARED_PREFS = "shared_prefs";
    private final String USER_KEY = "user_key";
    private final String SHARED_PREFS_Logo = "logo";
    private String sharedPrefUser;
    private Cursor c1;
    private DataBaseHandler db = new DataBaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            activityHistoryProformaBinding = ActivityHistoryProformaBinding.inflate(getLayoutInflater());
            setContentView(activityHistoryProformaBinding.getRoot());
            sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            sharedPrefUser = sharedpreferences.getString(USER_KEY, null);
            c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user='" + sharedPrefUser + "' GROUP BY proforma_cus_id) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id ORDER BY sorted.proforma_time DESC");
            displayPdf(c1);
            editTextDatePickerFrom = findViewById(R.id.editTextDatePickrFrom);
            editTextDatePickerTo = findViewById(R.id.editTextDatePickerTO);
            calendar = Calendar.getInstance();

            autoCompletion();

            editTextDatePickerFrom.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    checkDatesAndExecuteMethod();
                }
            });

            editTextDatePickerTo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    checkDatesAndExecuteMethod();
                }
            });
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(autoCompletion));
            activityHistoryProformaBinding.Hsearchbox.setAdapter(adapter);
            activityHistoryProformaBinding.Hsearchbox.setThreshold(3);
            activityHistoryProformaBinding.Hsearchbox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    search();
                }
            });

            activityHistoryProformaBinding.backbuttonHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            activityHistoryProformaBinding.reportbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fromDate != 0l && toDate != 0l) {

                        if (fromDate <= toDate) {
                            dialogReport();

                        } else {
                            editTextDatePickerFrom.setText("");
                            Toast.makeText(ProformaHistoryActivity.this, R.string.invalid_date, Toast.LENGTH_LONG).show();
                            editTextDatePickerTo.setText("");
                        }
                    } else {
                        if (editTextDatePickerFrom.getText().toString().length() == 0) {

                            Toast.makeText(ProformaHistoryActivity.this, "Enter From Date", Toast.LENGTH_LONG).show();
                        }
                        if (editTextDatePickerTo.getText().toString().length() == 0) {

                            Toast.makeText(ProformaHistoryActivity.this, "Enter To Date", Toast.LENGTH_LONG).show();

                        }
                    }

                }

            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
            Log.e(TAG, "An exception occurred onCreate:", e);
        }
        Logger.log("Ended", "onCreate");
    }

    /**
     * This will show the dialog  for report generation
     */
    private void dialogReport() {
        Logger.log("Started", "dialogReport");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProformaHistoryActivity.this);
            SpannableStringBuilder title = new SpannableStringBuilder("Report");
            title.setSpan(new AbsoluteSizeSpan(20, true), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setTitle(title)
                    .setMessage("Are you sure you want to fetch all the invoice between the Dates?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (sharedPrefUser.equals("admin")) {
                                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation GROUP BY proforma_cus_id ) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = customer.proforma_cus_id AND sorted.proforma_time BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY sorted.proforma_time ASC");
                            } else {
                                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user='" + sharedPrefUser + "' GROUP BY proforma_cus_id ) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id AND sorted.proforma_time BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY sorted.proforma_time ASC");
                            }
                            if (c1.getCount() > 0) {
                                new mergePDF().execute();
                            } else {
                                Toast.makeText(ProformaHistoryActivity.this, "No Invoice Found between the Dates", Toast.LENGTH_LONG).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ProformaHistoryActivity.this, "canceled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
        } catch (Exception e) {
            Logger.log("Crashed", "dialogReport");
            Log.e(TAG, "An exception occurred dialogReport:", e);
        }
        Logger.log("Ended", "dialogReport");
    }

    /**
     * check for the From AND To date is validate
     */
    private void checkDatesAndExecuteMethod() {
        Logger.log("Started", "checkDatesAndExecuteMethod");
        try {
            String fromDate = editTextDatePickerFrom.getText().toString().trim();
            String toDate = editTextDatePickerTo.getText().toString().trim();
            if (!fromDate.isEmpty() && !toDate.isEmpty()) {
                search();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkDatesAndExecuteMethod");
            Log.e(TAG, "An exception occurred checkDatesAndExecuteMethod:", e);
        }
        Logger.log("Ended", "checkDatesAndExecuteMethod");
    }

    /**
     * This will show the Search
     */
    public void search() {
        Logger.log("Started", "search");
        try {
            if (fromDate != 0l && toDate != 0l) {
                if (fromDate <= toDate) {
                    filterWithDate(sharedPrefUser, activityHistoryProformaBinding.Hsearchbox.getText().toString(), Long.toString(fromDate), Long.toString(toDate));
                } else {
                    editTextDatePickerFrom.setText("");
                    Toast.makeText(ProformaHistoryActivity.this, R.string.invalid_date, Toast.LENGTH_LONG).show();
                    editTextDatePickerTo.setText("");
                }
            } else {
                filterWithoutDate(sharedPrefUser, activityHistoryProformaBinding.Hsearchbox.getText().toString());
            }
        } catch (Exception e) {
            Logger.log("Crashed", "search");
            Log.e(TAG, "An exception occurred search():", e);
        }
        Logger.log("Ended", "search");
    }

    /**
     * generate pdf and merge pdf it
     */
    private class mergePDF extends AsyncTask<Void, Void, List<String>> {
        private List<PdfGenerationTask> taskList = new ArrayList<>();
        private ProgressDialog progressDialog;
        private final Handler handler = new Handler();
        private int totalTasks;
        private int currentTask = 0;

        public mergePDF() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                Logger.log("Started", "onPreExecute");
                progressDialog = new ProgressDialog(ProformaHistoryActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("Generating Report ...");
                progressDialog.setCancelable(false);
                progressDialog.setMax(100);
                progressDialog.show();
            } catch (Exception e) {
                Logger.log("Crashed", "onPreExecute");
                Log.e(TAG, "An exception occurred onPreExecute:", e);
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected List<String> doInBackground(Void... voids) {
            Logger.log("Started", "doInBackground");
            List<String> reportPdfList = new ArrayList();
            Cursor c1;
            if (sharedPrefUser.equals("admin")) {
                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation GROUP BY proforma_cus_id ) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = customer.proforma_cus_id AND sorted.proforma_time BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY sorted.proforma_time ASC");
            } else {
                c1 = db.getValue("SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user='" + sharedPrefUser + "' GROUP BY proforma_cus_id ) AS sorted JOIN proforma_customer ON sorted.proforma_cus_id = customer.proforma_cus_id AND sorted.proforma_time BETWEEN '" + fromDate + "' AND '" + toDate + "' ORDER BY sorted.proforma_time ASC");
            }
            totalTasks = c1.getCount() + 1;
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("file_Path"));
                    @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("Bill_No"));
                    File file;
                    if (data1 != null) {
                        file = new File(data1);
                    } else {
                        file = new File("/storage/emulated/0/DATA/Invoice" + data2 + ".pdf");
                    }
                    if (file.exists()) {
                        reportPdfList.add(data1);
                    } else {
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
                        Cursor c2;
                        Long CGST = null;
                        Long SGST = null;
                        c2 = db.getValue("SELECT * FROM proforma_Transation INNER JOIN proforma_customer ON  proforma_Transation.proforma_cus_id= proforma_customer.proforma_cus_id WHERE proforma_Bill_No ='" + data2 + "'");
                        if (c2.moveToFirst()) {
                            do {
                                @SuppressLint("Range") String data12 = c1.getString(c1.getColumnIndex("proforma_quantity"));
                                @SuppressLint("Range") String data13 = c1.getString(c1.getColumnIndex("proforma_rate"));

                                @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("proforma_amount"));
                                @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("proforma_Product_Name"));
                                @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                                @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                                @SuppressLint("Range") Long data7 = c1.getLong(c1.getColumnIndex("proforma_time"));
                                @SuppressLint("Range") String data8 = c1.getString(c1.getColumnIndex("proforma_signature"));
                                @SuppressLint("Range") Long data9 = c1.getLong(c1.getColumnIndex("proforma_igst"));
                                @SuppressLint("Range") Long data10 = c1.getLong(c1.getColumnIndex("proforma_cgst"));
                                @SuppressLint("Range") Long data11 = c1.getLong(c1.getColumnIndex("proforma_sgst"));
                                mQty.add(data12);
                                mCost.add(data13);
                                mTotal.add(data3);
                                mProduct_name.add(data4);
                                if (cusname == null || phoneno == null || time == 0l) {
                                    cusname = data5;
                                    phoneno = data6;
                                    SPIS_FIRST_TIME = data8;
                                    time = data7;
                                    IGST = data9;
                                    CGST = data10;
                                    SGST = data11;
                                }
                                count++;
                            } while (c2.moveToNext());
                        }
                        for (Long total : mTotal) {
                            Net_AMT = Net_AMT + total;
                        }

                        String fileName = "Invoice" + data2 + ".pdf";
                        String SPIS_FIRST_logo = sharedpreferences.getString(SHARED_PREFS_Logo, null);
                        File dir = Filedir();
                        if (!dir.exists()) {
                            dir.mkdir();
                        }

                        File file2 = new File(dir, fileName);
                        PdfGenerationTask pdfGenerationTask = new PdfGenerationTask(count, Net_AMT, Integer.parseInt(data2), cusname, phoneno, mQty, mCost, mTotal, mProduct_name, SPIS_FIRST_TIME, SPIS_FIRST_logo, file2, IGST, CGST, SGST, time);
                        taskList.add(pdfGenerationTask);
                        waitForTasks(currentTask, pdfGenerationTask);
                        reportPdfList.add(file2.getAbsolutePath());

                    }
                    currentTask++;
                    final int progress = (int) ((currentTask * 100) / (float) totalTasks);
                    handler.post(() -> progressDialog.setProgress(progress));
                    if (currentTask < 25) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } while (c1.moveToNext());
            }
            Logger.log("Ended", "doInBackground");
            return reportPdfList;
        }


        private void waitForTasks(int currentTask, PdfGenerationTask pdfGenerationTask) {
            Logger.log("Started", "waitForTasks");
            pdfGenerationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            boolean allTasksCompleted = false;

            while (!allTasksCompleted) {
                allTasksCompleted = true;
                try {
                    pdfGenerationTask.get(); // Wait for the task to complete
                } catch (InterruptedException | ExecutionException e) {
                    Logger.log("Crashed", "waitForTasks");
                    e.printStackTrace();
                }

                if (!pdfGenerationTask.getStatus().equals(Status.FINISHED)) {
                    allTasksCompleted = false;
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logger.log("Crashed", "waitForTasks");
                    e.printStackTrace();
                }
                Logger.log("Ended", "waitForTasks");
            }
        }

        /**
         * Executing tasks in the background
         */
        private Boolean waitForTasks() {
            Logger.log("Started", "Started");
            boolean anyTaskRunning;
            do {
                anyTaskRunning = false;
                for (PdfGenerationTask task : taskList) {
                    if (task.getStatus() == Status.RUNNING) {
                        anyTaskRunning = true;
                        break;
                    }
                }

                if (anyTaskRunning) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            } while (anyTaskRunning);
            Logger.log("Ended", "Started");
            return true;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            Logger.log("Started", "onPostExecute");
            if (waitForTasks()) {
                try {
                    String fileName = "Report_kirthana.pdf";
                    File dir = new File(getCacheDir(), "Report");

                    if (!dir.exists()) {
                        dir.mkdir();
                    }

                    File mergedPdfFile = new File(dir, fileName);
                    FileOutputStream outputStream = new FileOutputStream(mergedPdfFile);

                    Document document = new Document();
                    PdfCopy copy = new PdfCopy(document, outputStream);
                    document.open();

                    for (String pdfFilePath : result) {
                        PdfReader reader = new PdfReader(pdfFilePath);
                        copy.addDocument(reader);
                        reader.close();
                    }
                    currentTask++;
                    final int progress = (int) ((currentTask * 100) / (float) totalTasks);
                    handler.post(() -> progressDialog.setProgress(progress));
                    document.close();
                    outputStream.close();

                    Intent i = new Intent(ProformaHistoryActivity.this, ReportPDFView.class);
                    i.putExtra("ReportFile", mergedPdfFile);
                    startActivity(i);
                    Toast.makeText(ProformaHistoryActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                } catch (Exception e) {
                    Logger.log("Crashed", "onPostExecute");
                    e.printStackTrace();
                }
            }
            Logger.log("Ended", "onPostExecute");
            cancel(true);
        }


    }

    private File Filedir() {
        return new File(this.getFilesDir(), "DATA");
    }

    private class PdfGenerationTask extends AsyncTask<Void, Void, File> {

        private int count;
        private long netAmt;
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

        private Long IGST;
        private Long CGST;
        private Long SGST;
        private long time = 0;

        public PdfGenerationTask(int count, long netAmt, int billNo, String customerName, String phoneNo,
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
            this.time = time;

        }

        private android.app.AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected File doInBackground(Void... voids) {
            Logger.log("Started", "doInBackground");
            Export.ExportData(getPackageName(), "Kirthana_backup.zip", create("Kirthana_backup.zip"));
            Logger.log("Ended", "doInBackground");
            return ProfomaColourInvoice.colourPDF(count, netAmt, billNo, customerName, phoneNo, mQty, mCost, mTotal, mProductName, spIsFirstTime, spIsFirstLogo, file, time, IGST, CGST, SGST);
        }

        public File create(String Backup_filename) {
            Logger.log("Started", "create");
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
        }

        @Override
        protected void onPostExecute(File result) {

        }
    }

    /**
     * Date Picker Dialog
     */
    private DatePickerDialog createDatePickerDialog(boolean isFromDate) {
        Logger.log("Started", "createDatePickerDialog");
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(selectedYear, selectedMonth, selectedDay, isFromDate ? 0 : 23, isFromDate ? 0 : 59);

            Calendar currentDate = Calendar.getInstance();
            if (calendar1.after(currentDate)) {
                calendar1 = currentDate;
            }

            if (isFromDate) {
                fromDate = calendar1.getTimeInMillis();
                editTextDatePickerFrom.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);

            } else {
                toDate = calendar1.getTimeInMillis();
                editTextDatePickerTo.setText(selectedDay + "-" + (selectedMonth + 1) + "-" + selectedYear);

            }
        }, year, month, day);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                if (isFromDate) {
                    editTextDatePickerFrom.setText("");
                    fromDate = 0L;
                } else {
                    editTextDatePickerTo.setText("");
                    toDate = 0L;
                }
            }
        });

        if (isFromDate && toDate != 0L) {
            datePickerDialog.getDatePicker().setMaxDate(toDate);
        } else if (!isFromDate && fromDate != 0L) {
            datePickerDialog.getDatePicker().setMinDate(fromDate);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        } else {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        Logger.log("Ended", "createDatePickerDialog");
        return datePickerDialog;
    }

    public void showDatePickerDialogFrom(View v) {
        createDatePickerDialog(true).show();
    }

    public void showDatePickerDialogTo(View v) {
        createDatePickerDialog(false).show();
    }

    /**
     * filterWithDate
     */

    private void filterWithDate(String SPuser, String search, String fromDate, String toDate) {
        Logger.log("Started", "filterWithDate");
        try {
            String query = "SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user=? GROUP BY proforma_cus_id) AS sorted " +
                    "JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id ";

            List<String> params = new ArrayList<>();
            params.add(SPuser);

            if (!TextUtils.isEmpty(search) || (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate))) {
                query += "WHERE ";
                boolean hasConditions = false;

                if (!TextUtils.isEmpty(search)) {
                    query += "(proforma_customer.proforma_cus_id LIKE ? OR proforma_customer.proforma_cus_name LIKE ? OR proforma_customer.proforma_cus_Phone LIKE ?) ";
                    params.add(search + "%"); // for cus_id
                    params.add(search + "%"); // for cus_name
                    params.add(search + "%"); // for cus_Phone
                    hasConditions = true;
                }

                if (!TextUtils.isEmpty(fromDate) && !TextUtils.isEmpty(toDate)) {
                    if (hasConditions) {
                        query += "AND ";
                    }
                    query += "sorted.proforma_time BETWEEN ? AND ? ";
                    params.add(fromDate);
                    params.add(toDate);
                }

                // Add dynamic ORDER BY clause based on the search value type
                if (isNumeric(search)) {
                    query += "ORDER BY CASE WHEN proforma_customer.proforma_cus_id LIKE ? THEN 1 ELSE 2 END, proforma_customer.proforma_cus_id, sorted.proforma_time DESC";
                    params.add(search + "%"); // for cus_id
                } else if (!TextUtils.isEmpty(search)) {
                    query += "ORDER BY proforma_customer.proforma_cus_id, sorted.proforma_time DESC";
                } else {
                    query += "ORDER BY sorted.proforma_time DESC";
                }
            }

            Cursor c1 = db.getValue1(query, params.toArray(new String[0]));
            displayPdf(c1);
        } catch (Exception e) {
            Logger.log("Crashed", "filterWithDate");
            Log.e(TAG, "An exception occurred filterWithDate:", e);
        }
        Logger.log("Ended", "filterWithDate");
    }


    /**
     * Helper method to check if a string is numeric
     */
    private boolean isNumeric(String str) {
        return str.matches("\\d+");
    }

    /**
     * Filter Without Date
     */
    private void filterWithoutDate(String SPuser, String search) {
        Logger.log("Started", "filterWithoutDate");
        try {
            String query = "SELECT * FROM (SELECT * FROM proforma_Transation WHERE proforma_sales_user=? GROUP BY proforma_cus_id) AS sorted " +
                    "JOIN proforma_customer ON sorted.proforma_cus_id = proforma_customer.proforma_cus_id ";

            List<String> params = new ArrayList<>();
            params.add(SPuser);

            if (!TextUtils.isEmpty(search)) {
                query += "WHERE ";
                query += "(proforma_customer.proforma_cus_id LIKE ? OR proforma_customer.proforma_cus_name LIKE ? OR proforma_customer.proforma_cus_Phone LIKE ?) ";
                params.add(search + "%"); // for cus_id
                params.add(search + "%"); // for cus_name
                params.add(search + "%"); // for cus_Phone
            }

            // Add dynamic ORDER BY clause based on the search value type
            if (isNumeric(search)) {
                query += "ORDER BY CASE WHEN proforma_customer.proforma_cus_id LIKE ? THEN 1 ELSE 2 END, proforma_customer.proforma_cus_id, sorted.proforma_time DESC";
                params.add(search + "%"); // for cus_id
            } else if (!TextUtils.isEmpty(search)) {
                query += "ORDER BY proforma_customer.proforma_cus_id, sorted.proforma_time DESC";
            } else {
                query += "ORDER BY sorted.proforma_time DESC";
            }
            Cursor c1 = db.getValue1(query, params.toArray(new String[0]));
            displayPdf(c1);
        } catch (Exception e) {
            Logger.log("Crashed", "filterWithoutDate");
            Log.e(TAG, "An exception occurred filterWithoutDate:", e);
        }
        Logger.log("Ended", "filterWithoutDate");
    }

    private void displayPdf(Cursor c1) {
        Logger.log("Started", "displayPdf");
        try {
            recyclerView = findViewById(R.id.hisrecyle);
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
            image.clear();

            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String path = c1.getString(c1.getColumnIndex("proforma_file_Path"));
                    @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("proforma_Bill_No"));
                    @SuppressLint("Range") String data2 = c1.getString(c1.getColumnIndex("proforma_tamount"));

                    @SuppressLint("Range") Long data3 = c1.getLong(c1.getColumnIndex("proforma_time"));

                    @SuppressLint("Range") String data4 = c1.getString(c1.getColumnIndex("proforma_sales_user"));
                    @SuppressLint("Range") String data5 = c1.getString(c1.getColumnIndex("proforma_cus_name"));
                    @SuppressLint("Range") String data6 = c1.getString(c1.getColumnIndex("proforma_cus_Phone"));
                    @SuppressLint("Range") String data7 = c1.getString(c1.getColumnIndex("proforma_printer_img"));

                    File file;
                    if (path == null) {
                        file = new File("/storage/emulated/0/DATA/Profoma_Invoice" + data1 + ".pdf");
                    } else {
                        file = new File(path);
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");


                    SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");

                    image.add(data7);
                    Date res = new Date(data3);
                    tempbillno.add(data1);

                    mPbillno.add("Bill No: " + data1);
                    mPtamount.add("Total Amount: " + data2 + " Rs.");
                    mPtime.add("Time:" + formatter.format(res));
                    mPDate.add("Generated Date : " + formatter1.format(res));
                    mPusername.add("Generated BY: " + data4);
                    mPcusname.add("Name: " + data5);
                    mPcusPhoneno.add("Mobile no: " + data6);
                    pdfList.add(file);
                } while (c1.moveToNext());
            }
            System.out.println(image);
            pdfAdapter = new PdfAdapter(this, pdfList, this, mPbillno, tempbillno, mPtamount, mPDate, mPusername, mPtime, mPcusname, mPcusPhoneno, image);
            recyclerView.setAdapter(pdfAdapter);
        } catch (Exception e) {
            Logger.log("Crashed", "displayPdf");
            Log.e(TAG, "An exception occurred displayPdf:", e);
        }
        Logger.log("Ended", "displayPdf");
    }

    /**
     * Auto Completion
     */
    private void autoCompletion() {
        Logger.log("Started", "autoCompletion");
        try {
            Cursor c1 = db.getValue("select proforma_Bill_No from proforma_transation");
            if (c1.moveToFirst()) {
                do {
                    @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("proforma_Bill_No"));
                    autoCompletion.add(data1);
                } while (c1.moveToNext());
            }
            Cursor c2 = db.getValue("select proforma_cus_name,proforma_cus_Phone from proforma_customer");
            if (c2.moveToFirst()) {
                do {
                    @SuppressLint("Range") String data1 = c2.getString(c2.getColumnIndex("proforma_cus_name"));
                    @SuppressLint("Range") String data2 = c2.getString(c2.getColumnIndex("proforma_cus_Phone"));
                    autoCompletion.add(data1);
                    autoCompletion.add(data2);
                } while (c2.moveToNext());
            }
        } catch (Exception e) {
            Logger.log("Crashed", "autoCompletion");
            Log.e(TAG, "An exception occurred autoCompletion:", e);
        }
        Logger.log("Ended", "autoCompletion");
    }

    /**
     * Interface method
     *
     * @param file
     * @param mPbillno
     * @param filename
     */
    @Override
    public void onpdfSelected(File file, String mPbillno, String filename) {
        Logger.log("Started", "onpdfSelected");
        try {

            if (file.exists()) {
                String a = "2";
                Intent i = new Intent(this, ProfomaDocumentViewActivity.class);
                i.putExtra("billno", mPbillno);
                i.putExtra("option", a);
                i.putExtra("Filepath", file.getAbsolutePath());
                startActivity(i);

            } else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.file_is_removed_form_internal_storage_do_you_want_to_generate_again);
                builder.setTitle(R.string.alert);
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.no, (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.setPositiveButton(R.string.yes, (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                    String a = "2";
                    Intent i = new Intent(this, DocumentViewActivity.class);
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
        Logger.log("Started", " image");
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
            Logger.log("Crashed", " image");
            Log.e(TAG, "An exception occurred image:", e);
        }
        Logger.log("Ended", " image");
    }
}