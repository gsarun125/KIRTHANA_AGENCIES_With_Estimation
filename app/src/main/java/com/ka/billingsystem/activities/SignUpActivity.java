package com.ka.billingsystem.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ka.billingsystem.R;
import com.ka.billingsystem.crash.Logger;
import com.ka.billingsystem.database.DataBaseHandler;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private DataBaseHandler db = new DataBaseHandler(this);
    private EditText userid;
    private EditText username;
    private EditText newpassword;
    private EditText retypePassword;
    private Button BtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Logger.log("Started", "onCreate");
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sign_up);

            userid = (EditText) findViewById(R.id.txtuserid);
            newpassword = (EditText) findViewById(R.id.newpassword);
            retypePassword = (EditText) findViewById(R.id.retypepassword);
            BtnSignUp = (Button) findViewById(R.id.btnSignUp);
            username = (EditText) findViewById(R.id.txtUsername);


            BtnSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    signUp();
                }
            });
        } catch (Exception e) {
            Logger.log("Crashed", "onCreate");
        }
        Logger.log("Ended", "onCreate");
    }

    /**
     * Handles the sign-up process.
     */
    private void signUp() {
        Logger.log("Crashed", "signUp");
        try {
            if (CheckAllFields()) {
                String Uid = userid.getText().toString();
                String Uname = username.getText().toString();
                String password = newpassword.getText().toString();
                long time = System.currentTimeMillis();
                db.insertdataToUser(Uname, Uid, password, time, time);
                Toast.makeText(SignUpActivity.this, "Created Successfully", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        } catch (Exception e) {
            Logger.log("Crashed", "signUp");
            Log.e(TAG, "onClick: Error occurred", e);
        }
        Logger.log("Ended", "signUp");
    }

    /**
     * Checks whether all required fields are filled and validates user input.
     *
     * @return True if all fields are valid, false otherwise.
     */
    private boolean CheckAllFields() {
        Logger.log("Crashed", "signUp");
        try {
            if (username.length() == 0) {
                username.setError("User Name is required");
                username.setFocusable(true);
                username.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(username, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (userid.length() == 0) {
                userid.setError("User ID is required");
                userid.setFocusable(true);
                userid.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userid, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            String usercheck = userid.getText().toString();
            String qurry = "Select * from user where user_id='" + usercheck + "'";
            boolean checkuserpass;
            Cursor c1 = db.getValue(qurry);
            if (c1.getCount() > 0)
                checkuserpass = true;
            else
                checkuserpass = false;
            if (checkuserpass) {
                userid.setText("");
                newpassword.setText("");
                retypePassword.setText("");
                userid.setError("User id is already exist");
                userid.setFocusable(true);
                userid.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userid, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (newpassword.length() == 0) {
                newpassword.setError(getString(R.string.new_password_is_required));
                newpassword.setFocusable(true);
                newpassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            if (newpassword.length() < 8) {
                newpassword.setText("");
                retypePassword.setText("");
                newpassword.setError(getString(R.string.new_password_must_8_letters));
                newpassword.setFocusable(true);
                newpassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }

            if (retypePassword.length() == 0) {

                retypePassword.setError(getString(R.string.re_type_password_is_required));
                retypePassword.setFocusable(true);
                retypePassword.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(retypePassword, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
            if (!newpassword.getText().toString().equals(retypePassword.getText().toString())) {
                retypePassword.setText("");
                newpassword.setText("");
                newpassword.setError(getString(R.string.new_password_and_re_type_password_doesn_t_match));
                newpassword.setFocusable(true);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(newpassword, InputMethodManager.SHOW_IMPLICIT);

                return false;
            }
        } catch (Exception e) {
            Logger.log("Crashed", "signUp");
            Log.e(TAG, "CheckAllFields: Error occurred", e);
        }
        Logger.log("Ended", "signUp");
        return true;
    }

}