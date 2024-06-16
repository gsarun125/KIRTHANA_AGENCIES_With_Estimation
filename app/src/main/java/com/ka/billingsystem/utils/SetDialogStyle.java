package com.ka.billingsystem.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.widget.Button;

import androidx.core.content.ContextCompat;

import com.ka.billingsystem.R;
import com.ka.billingsystem.activities.BaseActivity;
import com.ka.billingsystem.activities.UserSelectionActivity;

public class SetDialogStyle {
    /**
     * Sets the style for the positive and negative buttons in the given AlertDialog.
     */
    public static void setDialogStyle(Context context,Button positiveButton, Button negativeButton) {

        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.black));
        SpannableString spannableStringPositive = new SpannableString("Ok");
        spannableStringPositive.setSpan(new AbsoluteSizeSpan(18, true), 0, spannableStringPositive.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        positiveButton.setText(spannableStringPositive);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
        SpannableString spannableStringNegative = new SpannableString("Cancel");
        spannableStringNegative.setSpan(new AbsoluteSizeSpan(18, true), 0, spannableStringNegative.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        negativeButton.setText(spannableStringNegative);
        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 38);
    }
}
