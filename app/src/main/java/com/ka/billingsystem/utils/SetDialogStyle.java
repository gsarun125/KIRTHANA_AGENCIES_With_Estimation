package com.ka.billingsystem.utils;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.TypedValue;
import android.widget.Button;

public class SetDialogStyle {
    /**
     * Sets the style for the positive and negative buttons in the given AlertDialog.
     */
    public static void setDialogStyle(Button positiveButton, Button negativeButton) {
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
