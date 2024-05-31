package com.ka.billingsystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;
import com.ka.billingsystem.R;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity"; // Define TAG for logging
    private DataBaseHandler db = new DataBaseHandler(this);
    private EditText userId;
    private EditText newPassword;
    private EditText retypePassword;
    private Button submit;
    private String UserId;
    private String NewPassword;
    private String ReTypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_forgot_password);
            userId = findViewById(R.id.UserName);
            newPassword = findViewById(R.id.NewPassword);
            submit = findViewById(R.id.btnSubmit);
            retypePassword = findViewById(R.id.RePassword);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        UserId = userId.getText().toString();
                        NewPassword = newPassword.getText().toString();
                        ReTypePassword = retypePassword.getText().toString();
                        if (checkAllFields()) {
                            db.forgotPasswordChange(UserId, NewPassword);
                            Toast.makeText(ForgotPasswordActivity.this, "Your password has been changed", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onClick: " + e.getMessage());
                        Toast.makeText(ForgotPasswordActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
        }
        Logger.log("Ended", "onCreate");
    }

    /**
     * CheckAllFields  all value entered or not
     */

    private boolean checkAllFields() {
        Logger.log("Started", "checkAllFields");
        try {
            if (userId.length() == 0) {
                userId.setError("User Id is required");
                userId.setFocusable(true);
                userId.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userId, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            String useridcheck = userId.getText().toString();
            String query = "Select * from user where user_id='" + useridcheck + "'";
            boolean checkuserpass;
            Cursor c1 = db.getValue(query);
            if (c1.getCount() > 0)
                checkuserpass = true;
            else
                checkuserpass = false;

            if (!checkuserpass) {
                userId.setText("");
                newPassword.setText("");
                userId.setError("User id Not found");
                userId.setFocusable(true);
                userId.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userId, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (NewPassword.length() == 0) {
                newPassword.setError("New Password is required");
                newPassword.setFocusable(true);
                newPassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newPassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (NewPassword.length() < 8) {
                newPassword.setText("");
                newPassword.setText("");
                newPassword.setError(getString(R.string.new_password_must_8_letters));
                newPassword.setFocusable(true);
                newPassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newPassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            if (ReTypePassword.length() == 0) {
                retypePassword.setError(getString(R.string.re_type_password_is_required));
                retypePassword.setFocusable(true);
                retypePassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(retypePassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (!newPassword.getText().toString().equals(retypePassword.getText().toString())) {
                retypePassword.setText("");
                newPassword.setText("");
                newPassword.setError(getString(R.string.new_password_and_re_type_password_doesn_t_match));
                newPassword.setFocusable(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newPassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        } catch (Exception e) {
            Logger.log("Crashed", "checkAllFields");
            Log.e(TAG, "Error in CheckAllFields: " + e.getMessage());
            Toast.makeText(ForgotPasswordActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            return false;
        }
        Logger.log("Ended", "checkAllFields");
        return true;

    }
}
