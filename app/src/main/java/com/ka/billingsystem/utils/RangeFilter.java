package com.ka.billingsystem.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class RangeFilter implements InputFilter {
    private final int minValue;
    private final int maxValue;

    public RangeFilter(int minValue, int maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Filter the input to allow only a certain range of values.
     *
     * @param source The source character sequence.
     * @param start The start position in the source.
     * @param end The end position in the source.
     * @param dest The destination character sequence.
     * @param dstart The start position in the destination.
     * @param dend The end position in the destination.
     * @return The filtered character sequence or an empty string if the input is not in the allowed range.
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(input)) {
                return null;
            }
        } catch (NumberFormatException ignored) {
        }
        return "";
    }

    private boolean isInRange(int value) {
        return value >= minValue && value <= maxValue;
    }
}
