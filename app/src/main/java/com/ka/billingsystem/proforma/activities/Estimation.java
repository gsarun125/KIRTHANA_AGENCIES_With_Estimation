package com.ka.billingsystem.proforma.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ka.billingsystem.R;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;

public class Estimation extends AppCompatActivity {

    private Button button;
    private  EditText customerName;
   private EditText estimatedAmount;
   private ImageView imageView;
    public int selectedOption = 0;
    private RadioButton radioButtonIGST;
    private RadioButton radioButtonCGST;
    private DataBaseHandler db = new DataBaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estimation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        imageView=findViewById(R.id.infoPEstimation);

        button=findViewById(R.id.btnEstimation);
        customerName=findViewById(R.id.cusName);
        estimatedAmount=findViewById(R.id.edtbillno);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioButtonIGST = findViewById(R.id.radioButtonIGST);
        radioButtonCGST = findViewById(R.id.radioButtonCGST);

        selectedOption = 0;
        radioButtonCGST.setChecked(true);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Estimation.this,EstimationHistroyActivity.class);
                startActivity(i);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonSet(checkedId);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(checkAllFields()) {
                  String Customer_Name = customerName.getText().toString();
                  String EstimatedAmount = estimatedAmount.getText().toString();
                  db.insertDataToEstimation(Customer_Name, Integer.parseInt(EstimatedAmount));
                  Intent i = new Intent(Estimation.this, EstimationDocumentViewActivity.class);
                  i.putExtra("cusname", Customer_Name);
                  i.putExtra("estimateAmount", EstimatedAmount);
                  i.putExtra("gstType", String.valueOf(selectedOption));
                  startActivity(i);
              }
            }
        });
    }
    private boolean checkAllFields() {
        Logger.log("Started", "checkAllFields");
        try {
            if (customerName.length() == 0) {
                customerName.setError(getString(R.string.customer_name_is_required));
                customerName.setFocusable(true);
                customerName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(customerName, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (estimatedAmount.length() == 0) {
                estimatedAmount.setError("EstimatedAmount is equired");
                estimatedAmount.setFocusable(true);
                estimatedAmount.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(estimatedAmount, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkAllFields");
        }
        Logger.log("Ended", "checkAllFields");
        return true;
    }

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
}