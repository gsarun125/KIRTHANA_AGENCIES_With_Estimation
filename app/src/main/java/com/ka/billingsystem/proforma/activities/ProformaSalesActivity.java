package com.ka.billingsystem.proforma.activities;


import static com.ka.billingsystem.utils.SetDialogStyle.setDialogStyle;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.PdfViewActivity;
import com.ka.billingsystem.activities.Signature;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.databinding.ActivitySalesProformaBinding;
import com.ka.billingsystem.utils.SalesData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kotlin.jvm.internal.Intrinsics;

public class ProformaSalesActivity extends AppCompatActivity {
    private DataBaseHandler db = new DataBaseHandler(this);
    public ActivitySalesProformaBinding activitySalesProformaBinding;
    public static String SPIS_FIRST_TIME = null;
    private int add_count = 0;
    private EditText edtbillno;
    private EditText qty;
    private EditText Printer_Type;
    private EditText Cost;
    public String Customer_Name = "";
    public String PHone_NO = "";

    private int Customer_Id;

    public List<String> mQty = new ArrayList();

    public List<String> mProduct_name = new ArrayList();
    public int Bill_NO;
    public EditText cusEdit;
    public EditText phoneEdit;
    public List<Long> mTotal = new ArrayList();


    public int count;
    public Long Net_AMT = 0L;
    public List<String> mCost = new ArrayList();
    private String SHARED_PREFS = "shared_prefs";
    private String USER_KEY = "user_key";

    private String SPuser;
    public int selectedOption = 0;

    private SharedPreferences sharedpreferences;
    private RadioButton radioButtonIGST;
    private RadioButton radioButtonCGST;
    private ImageView errorIcon;
    int editidIntent = 0;
    private SalesData OldsalesData;
    private int EditBill;
    private SalesData salesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        activitySalesProformaBinding = ActivitySalesProformaBinding.inflate(getLayoutInflater());
        setContentView(activitySalesProformaBinding.getRoot());
        activitySalesProformaBinding.button.setVisibility(View.GONE);
        activitySalesProformaBinding.buttonAdd.setVisibility(View.GONE);

        cusEdit = (EditText) findViewById(R.id.cusName);
        errorIcon = findViewById(R.id.errorIcon);
        edtbillno = findViewById(R.id.edtbillno);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SPuser = sharedpreferences.getString(USER_KEY, null);

        phoneEdit = (EditText) findViewById(R.id.PhoneNo);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioButtonIGST = findViewById(R.id.radioButtonIGST);
        radioButtonCGST = findViewById(R.id.radioButtonCGST);

        selectedOption = 0;
        radioButtonCGST.setChecked(true);

        Cursor cursor = db.getValue("select max(proforma_Bill_No) from proforma_Transation");
        if (cursor != null) {
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            int Bill_id = id + 1;
            edtbillno.setHint("Bill No:" + Bill_id);
        }

        if (getIntent().hasExtra("id")) {
            editidIntent = getIntent().getIntExtra("id", 0);
            activitySalesProformaBinding.button.setVisibility(View.VISIBLE);
            activitySalesProformaBinding.buttonAdd.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            OldsalesData = gson.fromJson(getIntent().getStringExtra("dateObject"), SalesData.class);
            cusEdit.setText(OldsalesData.getCustomerName());
            phoneEdit.setText(OldsalesData.getPhoneNo());
            edtbillno.setHint("Bill NO:" + OldsalesData.getBill_NO());
            List<String> mSetCost = OldsalesData.getmCost();
            System.out.println(mSetCost);
            List<String> mSetProductName = OldsalesData.getmProduct_name();
            EditBill = OldsalesData.getBill_NO();
            List<String> mSetQty = OldsalesData.getmQty();
            count = OldsalesData.getCount();

            for (int i = 0; i < count; i++) {
                add_count++;
                View inflater = LayoutInflater.from((Context) this).inflate(R.layout.row_add_language, null);
                LinearLayout layout = activitySalesProformaBinding.parentLinearLayout;
                Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");
                layout.addView(inflater, layout.getChildCount());
                qty = (EditText) inflater.findViewById(R.id.et_Qty);
                Printer_Type = (EditText) inflater.findViewById(R.id.et_Typep);
                Cost = (EditText) inflater.findViewById(R.id.et_Cost);
                qty.setText(mSetQty.get(i));
                Printer_Type.setText(mSetProductName.get(i));
                Cost.setText(mSetCost.get(i));

            }

            selectedOption = OldsalesData.getSelectedOption();
            if (selectedOption == 0) {
                System.out.println("fkfjhfg");
                // Deselect IGST
                radioButtonCGST.setChecked(true);
                //  radioButtonCGST.setChecked(false);
            }
            if (selectedOption == 1) {
                System.out.println("ffdkljghdjdsffd");
                // Deselect CGST

                radioButtonIGST.setChecked(true);
            }


        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonSet(checkedId);
            }
        });

        phoneEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkIfBothFieldsHaveText();
                String phone = phoneEdit.getText().toString().trim();
                String cusName = cusEdit.getText().toString().trim();


                if (phone.length() < 10) {
                    errorIcon.setVisibility(View.VISIBLE);
                } else {
                    errorIcon.setVisibility(View.INVISIBLE);
                }

                if (cusName.length() != 0 & phone.length() == 10) {
                    getInputRequest();
                } else if (phone.length() == 10 & cusName.length() == 0) {
                    getInputRequestCusName();
                }
            }
        });
        cusEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phone = phoneEdit.getText().toString().trim();

                if (phone.length() != 10) {
                    checkIfBothFieldsHaveText();
                } else {
                    updateButtonVisibility();
                }
            }
        });

        activitySalesProformaBinding.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPerform();
            }
        });
        activitySalesProformaBinding.button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                generateInvoice();
            }
        });
        activitySalesProformaBinding.backbuttonSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getInputRequestCusName() {
        Logger.log("Started", "getInputRequestCusName");
        cusEdit.setFocusable(true);
        cusEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(cusEdit, InputMethodManager.SHOW_IMPLICIT);
        Logger.log("Ended", "getInputRequestCusName");
    }

    private void getInputRequest() {
        Logger.log("Started", "getInputRequest");
        try {
            View v = null;
            v = this.activitySalesProformaBinding.parentLinearLayout.getChildAt(0);
            Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
            Printer_Type.setFocusable(true);
            Printer_Type.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Logger.log("Crashed", "getInputRequest");
        }
        Logger.log("Ended", "getInputRequest");
    }

    /**
     * Generates an invoice based on the fields in the sales activity.
     */
    private void generateInvoice() {

        Logger.log("Started", "generateInvoice");
        try {
            if (checkAllFields()) {
                LinearLayout layout = activitySalesProformaBinding.parentLinearLayout;
                int count = layout.getChildCount();

                if (count != 0) {
                    getAllDetail();
                    if (check_all(count)) {
                        if (OldsalesData != null) {
                            if (OldsalesData.equals(salesData)) {
                                Toast.makeText(this, "Please Edit the values", Toast.LENGTH_LONG).show();
                            } else {
                                displayGenerate(count);
                            }

                        } else {
                            displayGenerate(count);
                        }

                    }
                } else {
                    Toast.makeText(ProformaSalesActivity.this, getString(R.string.add_printer_information), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "generateInvoice");
        }
        Logger.log("Ended", "generateInvoice");
    }

    /**
     * Adds a new view to the layout, considering certain conditions.
     */
    private void addPerform() {
        Logger.log("Started", "addPerform");
        try {
            if (add_count < 10) {
                if (add_count != 0) {
                    if (check_all_value(add_count - 1)) {
                        addNewView();
                        add_count++;
                    }
                } else {
                    addNewView();
                    add_count++;
                }
            } else {
                Toast.makeText(ProformaSalesActivity.this, R.string.you_cannot_add_more_then_10_value, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "addPerform");
        }
        Logger.log("Ended", "addPerform");
    }

    /**
     * Handles radio button selections for IGST and CGST options.
     *
     * @param checkedId The ID of the selected radio button.
     */
    private void radioButtonSet(int checkedId) {
        Logger.log("Started", "radioButtonSet");
        try {
            if (checkedId == R.id.radioButtonIGST) {
                if (selectedOption == 0) {
                    selectedOption = 1; // Deselect IGST
                    radioButtonIGST.setChecked(true);
                }
            } else if (checkedId == R.id.radioButtonCGST) {
                if (selectedOption == 1) {
                    selectedOption = 0; // Deselect CGST
                    radioButtonCGST.setChecked(true);
                }

            }
        } catch (Exception e) {
            Logger.log("Crashed", "radioButtonSet");
        }
        Logger.log("Ended", "radioButtonSet");
    }

    /**
     * @param count
     */
    private void displayGenerate(int count) {
        Logger.log("Started", "displayGenerate");
        try {

            // Create a ScrollView
            ScrollView scrollView = new ScrollView(ProformaSalesActivity.this);

            // Create a LinearLayout to hold the custom layout
            LinearLayout customLayout = new LinearLayout(ProformaSalesActivity.this);
            customLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            customLayout.setOrientation(LinearLayout.VERTICAL);
            customLayout.setBackgroundResource(R.drawable.background);
            //  customLayout.setPadding(16, 16, 16, 16);


            // Add customer details to custom layout
            View customerDetailsView = LayoutInflater.from(ProformaSalesActivity.this).inflate(R.layout.custom_info_gen, null);
            customLayout.addView(customerDetailsView);

            TextView Customer_name = customerDetailsView.findViewById(R.id.txtCusname);
            TextView Cusphone_no = customerDetailsView.findViewById(R.id.txtPhoneno);
            TextView GSt = customerDetailsView.findViewById(R.id.GSTradio);


            if (selectedOption == 0) {
                GSt.setText("SGST & CGST");
            } else {
                GSt.setText("IGST");
            }

            // Set text for customer details
            Customer_name.setText(activitySalesProformaBinding.cusName.getText().toString());
            Cusphone_no.setText(" " + activitySalesProformaBinding.PhoneNo.getText().toString());
            Customer_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Customer_name.setSelected(true);
                }
            });
            // Loop through products and add them to the custom layout
            for (int i = 0; i < count; i++) {
                View v = activitySalesProformaBinding.parentLinearLayout.getChildAt(i);
                qty = v.findViewById(R.id.et_Qty);
                Printer_Type = v.findViewById(R.id.et_Typep);
                Cost = v.findViewById(R.id.et_Cost);


                // Inflate the custom layout for each product
                View productView = LayoutInflater.from(this).inflate(R.layout.customer_product_gen, null);

                // Find TextViews in the custom layout
                TextView productNameTextView = productView.findViewById(R.id.productNameTextView);
                TextView quantityTextView = productView.findViewById(R.id.quantityTextView);
                TextView costTextView = productView.findViewById(R.id.costTextView);
                productNameTextView.setMovementMethod(new ScrollingMovementMethod());

                // Set text for each TextView
                productNameTextView.setText(Printer_Type.getText().toString());
                quantityTextView.setText(qty.getText().toString());
                costTextView.setText(Cost.getText().toString());

                // Add the custom layout for the product to the main custom layout
                customLayout.addView(productView);

                // Add a separator between products
                View separator = new View(this);
                separator.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1)); // 1-pixel height for the separator
                separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                customLayout.addView(separator);
            }

            // Add the custom layout to the ScrollView
            scrollView.addView(customLayout);

            // Create an AlertDialog with the ScrollView
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.RoundedDialog);
            builder.setView(scrollView);
            builder.setCancelable(false);

            View productView = LayoutInflater.from(this).inflate(R.layout.button, null);
            TextView OKButton = productView.findViewById(R.id.buttonok);
            TextView CancelButton = productView.findViewById(R.id.buttoncancel);

            final AlertDialog alertDialog = builder.create();
            OKButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveData();

                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            });

            CancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }

                }
            });

            customLayout.addView(productView);
            alertDialog.show();

        } catch (Exception e) {
            Logger.log("Crashed", "displayGenerate");
            Log.e("saveData", "An exception occurred", e);
        }
        Logger.log("Ended", "displayGenerate");
    }

    /**
     * Checks if both phoneEdit and cusEdit fields have non-empty and valid values.
     * If conditions are met, it calls updateButtonVisibility to make a button visible.
     */

    private void checkIfBothFieldsHaveText() {
        try {
            String phone = phoneEdit.getText().toString().trim();
            String cusName = cusEdit.getText().toString().trim();
            if (!phone.isEmpty() && !cusName.isEmpty() && phone.length() == 10) {
                updateButtonVisibility();
            } else {
                activitySalesProformaBinding.button.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkIfBothFieldsHaveText");
            Log.e(" checkIfBothFieldsHaveText", "An exception occurred", e);
        }
    }

    /**
     * Updates the visibility of buttons.
     */
    private void updateButtonVisibility() {
        Logger.log("Started", "updateButtonVisibility");
        try {
            activitySalesProformaBinding.button.setVisibility(View.VISIBLE);
            activitySalesProformaBinding.buttonAdd.setVisibility(View.VISIBLE);
            if (add_count == 0) {
                addNewView();
                add_count++;
            }
        } catch (Exception e) {
            Logger.log("Crashed", "updateButtonVisibility");
            Log.e("updateButtonVisibility", "An exception occurred", e);
        }
        Logger.log("Ended", "updateButtonVisibility");
    }

    /**
     * Handles the deletion of a view.
     *
     * @param v The View representing the delete button.
     */
    public void onDelete(View v) {
        Logger.log("Started", "onDelete");
        try {
            add_count--;
            activitySalesProformaBinding.parentLinearLayout.removeView((View) v.getParent());
        } catch (Exception e) {
            Logger.log("Crashed", "onDelete");
            Log.e("onDelete", "An exception occurred", e);
        }
        Logger.log("Ended", "onDelete");
    }

    /**
     * Adds a new view dynamically to the parent linear layout.
     */
    private void addNewView() {
        Logger.log("Started", "addNewView");
        try {
            View inflater = LayoutInflater.from((Context) this).inflate(R.layout.row_add_language, null);
            LinearLayout layout = activitySalesProformaBinding.parentLinearLayout;
            Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");
            layout.addView(inflater, layout.getChildCount());
            qty = (EditText) inflater.findViewById(R.id.et_Qty);
            Printer_Type = (EditText) inflater.findViewById(R.id.et_Typep);
            Cost = (EditText) inflater.findViewById(R.id.et_Cost);
            Printer_Type.setFocusable(true);
            Printer_Type.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
        } catch (Exception e) {
            Logger.log("Crashed", "addNewView");
            Log.e("addNewView", "An exception occurred", e);
        }
        Logger.log("Ended", "addNewView");
    }

    /**
     * Checks if the values in the Printer_Type, Cost, and qty fields of a specific view are valid.
     *
     * @param i Index of the view in the parent linear layout.
     * @return True if the values are valid, false otherwise.
     */
    public boolean check_all_value(int i) {
        Logger.log("Started", "check_all_value");
        View v = null;
        v = this.activitySalesProformaBinding.parentLinearLayout.getChildAt(i);
        qty = (EditText) v.findViewById(R.id.et_Qty);
        Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
        Cost = (EditText) v.findViewById(R.id.et_Cost);

        if (Printer_Type.getText().toString().length() == 0) {
            Printer_Type.setError(getString(R.string.printer_type_is_required1));
            Printer_Type.setFocusable(true);
            Printer_Type.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (Cost.getText().toString().length() == 0) {
            Cost.setError(getString(R.string.cost_is_required));
            Cost.setFocusable(true);
            Cost.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(Cost, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }

        if (qty.getText().toString().length() == 0) {
            qty.setError(getString(R.string.net_quantity_is_required));
            qty.setFocusable(true);
            qty.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
            return false;
        }
        Logger.log("Ended", "check_all_value");
        return true;
    }

    /**
     * Checks if the values in the Printer_Type, Cost, and qty fields of all views are valid.
     *
     * @param count The number of views to check.
     * @return True if all values are valid, false otherwise.
     */
    public boolean check_all(int count) {
        Logger.log("Started", " check_all");
        View v = null;
        for (int i = 0; i < count; i++) {
            v = this.activitySalesProformaBinding.parentLinearLayout.getChildAt(i);
            qty = (EditText) v.findViewById(R.id.et_Qty);
            Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
            Cost = (EditText) v.findViewById(R.id.et_Cost);
            if (Printer_Type.getText().toString().length() == 0) {
                Printer_Type.setError(getString(R.string.printer_type_is_required1));
                Printer_Type.setFocusable(true);
                Printer_Type.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Printer_Type, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (Cost.getText().toString().length() == 0) {

                Cost.setError(getString(R.string.net_quantity_is_required));
                Cost.setFocusable(true);
                Cost.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(Cost, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (qty.getText().toString().length() == 0) {

                qty.setError(getString(R.string.net_quantity_is_required));
                qty.setFocusable(true);
                qty.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(qty, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        }
        Logger.log("Ended", " check_all");
        return true;
    }

    /**
     * Saves data by performing the following steps:
     * 1. Retrieves all details (presumably from UI elements or some other source).
     * 2. Initiates an asynchronous task using the asyncTask method.
     * 3. Navigates to the PdfviewActivity using the intentToPdfview method.
     */
    void saveData() {
        Logger.log("Started", "saveData");
        try {
            getAllDetail();
            asyncTask(mQty, mCost, mTotal);
            intentToPdfview();
        } catch (Exception e) {
            Logger.log("Crashed", "saveData");
            Log.e("saveData", "An exception occurred", e);
        }
        Logger.log("Ended", "saveData");
    }

    /**
     * Collects details from UI elements and stores them in corresponding lists.
     */
    private void getAllDetail() {
        Logger.log("Started", "getAllDetail");
        mQty.clear();
        mProduct_name.clear();
        mCost.clear();
        mTotal.clear();
        LinearLayout layout = activitySalesProformaBinding.parentLinearLayout;
        count = layout.getChildCount();
        View v = null;
        for (int i = 0; i < count; i++) {
            v = this.activitySalesProformaBinding.parentLinearLayout.getChildAt(i);
            qty = (EditText) v.findViewById(R.id.et_Qty);
            Printer_Type = (EditText) v.findViewById(R.id.et_Typep);
            Cost = (EditText) v.findViewById(R.id.et_Cost);
            String QTY = qty.getText().toString();
            String PrinterType = Printer_Type.getText().toString();
            Long cost;
            String costString = Cost.getText().toString().replaceAll(",", "");
            if (!costString.isEmpty()) {
                cost = Long.valueOf(costString);
            } else {
                cost = 0L;
            }
            mProduct_name.add(PrinterType);
            mCost.add(String.valueOf(cost));
            mQty.add(QTY);
            Long addTotal = cost * Long.parseLong(QTY);
            mTotal.add(addTotal);
            System.out.println(mTotal);
        }

        Customer_Name = cusEdit.getText().toString();
        PHone_NO = phoneEdit.getText().toString();
        add_count = 0;
        if (edtbillno.length() != 0) {
            Customer_Id = Integer.parseInt(edtbillno.getText().toString());
            Bill_NO = Integer.parseInt(edtbillno.getText().toString());
        } else {
            Cursor cursor = db.getValue("select max(proforma_Bill_No) from proforma_Transation");
            if (cursor != null) {
                cursor.moveToFirst();
                int id = cursor.getInt(0);
                Customer_Id = id + 1;
                Bill_NO = id + 1;
            }
        }

        for (Long total : mTotal) {
            Net_AMT = Net_AMT + total;
        }
        Long Net_AMT1 = 0l;
        for (Long total : mTotal) {
            Net_AMT1 = Net_AMT1 + total;
        }

        salesData = new SalesData();

        if (editidIntent == 0) {
            salesData.setBill_NO(Bill_NO);
        } else {
            salesData.setBill_NO(EditBill);
        }
        salesData.setCustomerName(Customer_Name);
        salesData.setPhoneNo(PHone_NO);
        salesData.setmQty(mQty);
        salesData.setmCost(mCost);
        salesData.setSelectedOption(selectedOption);
        salesData.setAdd_count(add_count);
        salesData.setmProduct_name(mProduct_name);
        salesData.setNet_AMT(Net_AMT1);
        salesData.setmTotal(mTotal);
        salesData.setCount(count);


        Logger.log("Ended", "getAllDetail");
    }

    /**
     * Navigate to PdfViewActivity or Signature activity based on conditions.
     */
    private void intentToPdfview() {


        String qurry = "Select signature from user where id='1'";
        Cursor c1 = db.getValue(qurry);
        if (c1.moveToFirst()) {
            @SuppressLint("Range") String data1 = c1.getString(c1.getColumnIndex("signature"));
            SPIS_FIRST_TIME = data1;
        }
        Gson gson = new Gson();

        String jsonStringSalesData = gson.toJson(salesData);
        Log.d("check", "OldsalesData is null");
        if (SPIS_FIRST_TIME != null) {
            Intent intent = new Intent(ProformaSalesActivity.this, ProfomaPdfViewActivity.class);
            intent.putExtra("SalesData", jsonStringSalesData);
            startActivity(intent);
            finish();
        } else {

            Intent intent = new Intent(ProformaSalesActivity.this, Signature.class);
            intent.putExtra("SalesData", jsonStringSalesData);
            startActivity(intent);
            finish();
        }


        Logger.log("Ended", "intentToPdfview");
    }

    /**
     * Asynchronously perform tasks, including calculating total amounts and inserting data into the database.
     */
    private void asyncTask(List<String> mQty, List<String> mCost, List<Long> mTotal) {
        System.out.println("djkhhdcgjkl");
        ExecutorService executorService = Executors.newFixedThreadPool(2);  // Create a thread pool with 2 threads

        if (editidIntent == 1) {
            System.out.println("kjkjkj");
            db.profomapermanentDeleteTrancation(String.valueOf(EditBill));
            db.profomapermanentCusDetails(String.valueOf(EditBill));

            Future<?> future = executorService.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    for (Long total : mTotal) {
                        Net_AMT = Net_AMT + total;
                    }


                    long time = System.currentTimeMillis();
                    for (int i = 0; i < count; i++) {
                        System.out.println("fkjfghjfk");
                        db.proformaInsertdataToTrancation(EditBill, EditBill, mProduct_name.get(i), mQty.get(i), mCost.get(i), mTotal.get(i), Net_AMT, time, SPuser);
                        System.out.println("end");
                    }
                    db.profomainsertdataToCustomer(EditBill, Customer_Name, PHone_NO);
                    return null;
                }
            });
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("khjgfhjkl");
            Future<?> future = executorService.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    for (Long total : mTotal) {
                        Net_AMT = Net_AMT + total;
                    }
                    long time = System.currentTimeMillis();
                    for (int i = 0; i < count; i++) {
                        db.proformaInsertdataToTrancation(Customer_Id, Bill_NO, mProduct_name.get(i), mQty.get(i), mCost.get(i), mTotal.get(i), Net_AMT, time, SPuser);
                    }
                    db.profomainsertdataToCustomer(Customer_Id, Customer_Name, PHone_NO);
                    return null;
                }
            });
        }

    }


    /**
     * Remove all child views from the parentLinearLayout.
     */
    public void removeView() {
        Logger.log("Started", "removeView");
        try {
            LinearLayout layout = activitySalesProformaBinding.parentLinearLayout;
            Intrinsics.checkNotNullExpressionValue(layout, "binding.parentLinearLayout");
            layout.removeAllViews();
        } catch (Exception e) {
            Log.e("removeView", "An exception occurred", e);
        }
        Logger.log("Ended", "removeView");
    }

    /**
     * Check if all required fields are filled out.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private boolean checkAllFields() {
        Logger.log("Started", "checkAllFields");
        try {
            if (cusEdit.length() == 0) {
                cusEdit.setError(getString(R.string.customer_name_is_required));
                cusEdit.setFocusable(true);
                cusEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(cusEdit, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (phoneEdit.length() == 0) {
                phoneEdit.setError(getString(R.string.customer_phone_no_is_required));
                phoneEdit.setFocusable(true);
                phoneEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneEdit, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            if (phoneEdit.length() != 10) {
                phoneEdit.setError(getString(R.string.mobile_number_must_be_10_digits));
                phoneEdit.setFocusable(true);
                phoneEdit.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(phoneEdit, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            if (edtbillno.length() != 0) {
                Cursor cursor = db.getValue("SELECT * FROM proforma_Transation WHERE proforma_Bill_No = '" + edtbillno.getText().toString() + "'");
                if (cursor.getCount() != 0) {
                    edtbillno.setError("Bill No Already Available ");
                    edtbillno.setFocusable(true);
                    edtbillno.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(edtbillno, InputMethodManager.SHOW_IMPLICIT);

                    Toast.makeText(this, "Bill No is Already Available", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkAllFields");
        }
        Logger.log("Ended", "checkAllFields");
        return true;
    }

    /**
     * Handle the back button press.
     */
    @Override
    public void onBackPressed() {
        Logger.log("Started", "onBackPressed");
        try {
            if (cusEdit.getText().toString().length() != 0 && phoneEdit.getText().toString().length() != 0) {
                dialogBack();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "onBackPressed");
            Log.e("onBackPressed()", "An exception occurred", e);
        }
        Logger.log("Ended", "onBackPressed");
    }

    /**
     * Display a dialog asking the user if they want to go back.
     */
    private void dialogBack() {
        Logger.log("Started", "dialogBack");
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to go back ?");
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
        } catch (Exception e) {
            Logger.log("Crashed", " dialogBack");
        }
        Logger.log("Ended", "dialogBack");
    }

}