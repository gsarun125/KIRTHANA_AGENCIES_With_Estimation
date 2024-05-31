package com.ka.billingsystem.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageEncodeAndDecode {
    /**
     * Encode a Bitmap image to a Base64-encoded string.
     *
     * @param image The Bitmap image to be encoded.
     * @param compressFormat The format of the compressed image (e.g., Bitmap.CompressFormat.PNG).
     * @param quality The quality of the compressed image.
     * @return The Base64-encoded string.
     */
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Decode a Base64-encoded string to a Bitmap image.
     *
     * @param encodedString The Base64-encoded string.
     * @return The decoded Bitmap image.
     */
    public static Bitmap decodeBase64ToBitmap(String encodedString) {
        byte[] decodedString = Base64.decode(encodedString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
