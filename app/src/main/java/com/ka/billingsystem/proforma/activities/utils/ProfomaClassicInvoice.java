package com.ka.billingsystem.proforma.activities.utils;

import static com.ka.billingsystem.utils.Currency.convertToIndianCurrency;
import static com.ka.billingsystem.utils.ImageEncodeAndDecode.decodeBase64ToBitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.ThumbnailUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfomaClassicInvoice {
    public static File classicPDF(int count, Long Net_AMT, int Bill_NO, String CusName, String CusPhone, List<String> mQty, List<String> mCost, List<Long> mTotal, List<String> mProduct_name, String SPIS_FIRST_TIME, String SPIS_FIRST_LOGO, File file, long time, Long IGST, Long CGST, Long SGST) {
        NumberFormat indianCurrencyFormat = NumberFormat.getInstance(new Locale("en", "IN"));
        indianCurrencyFormat.setMinimumFractionDigits(2); // If you want to show decimal places

        int end_item = 500;
        int pageWidth = 1200;


        PdfDocument document = new PdfDocument();
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = document.startPage(myPageInfo1);
        Canvas canvas = page.getCanvas();


        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(40);
        canvas.drawText("TAX INVOICE", pageWidth / 2, 50, titlePaint);


        //title
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        titlePaint.setTextSize(50);
        canvas.drawText("KIRTHANA AGENCIES", 90, 150, titlePaint);

        //Address
        String Address = "#6,Alikhan Street, Alandur, Chennai-600 016,";
        String Adddress1 = "Tamil Nadu,India.";
        String Tel = "+91 44 2231 4628";
        String Email = "kirthana.agencies@outlook.com";
        String GSTIN = "33AEFPJ5208Q1ZB";

        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(20);
        canvas.drawText(Address, 20, 200, titlePaint);
        canvas.drawText(Adddress1, 20, 225, titlePaint);
        canvas.drawText("Tel: " + Tel, 20, 250, titlePaint);
        canvas.drawText("Email: " + Email, 20, 275, titlePaint);
        canvas.drawText("GSTIN: " + GSTIN, 20, 300, titlePaint);


        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(25f);
        myPaint.setColor(Color.BLACK);
        canvas.drawText("Invoice No : " + Bill_NO, 20, 350, myPaint);
        // canvas.drawText("Customer Id: "+Customer_Id,20,590,myPaint);
        //canvas.drawText("Customer Name: "+Customer_Name,20,650,myPaint);
        //canvas.drawText("Phone No: "+PHone_NO,20,710,myPaint);


        Date day;
        if (time != 0) {
            day = new Date(time);
        } else {
            day = new Date();
        }
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat formatter2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        canvas.drawText("Invoice Date : " + formatter1.format(day), 20, 400, myPaint);

        String[] lines = CusName.split("\n");

        float yPosition = 225;
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(30f);
        canvas.drawText("Customer Details:-", pageWidth - 320, 200,titlePaint);


// Draw each line of the formatted text on the canvas
        for (String line : lines) {
            canvas.drawText( line.trim()+",", pageWidth - 300, yPosition, myPaint);
            yPosition += myPaint.descent() - myPaint.ascent()+1; // Move to the next line
        }

        //canvas.drawText("Customer Name : "+CusName, pageWidth - 400, 350, myPaint);
        //  int mobileStart = printNextLine(canvas, myPaint, pageWidth - 200, 350, 198, CusName, end_item);

        canvas.drawText(CusPhone+".", pageWidth - 300, yPosition, myPaint);

        //canvas.
        // drawText("Time : "+formatter.format(date),pageWidth-350,710,myPaint);

        //box

        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(2);


        canvas.drawRect(20, 420, pageWidth - 20, 1260, myPaint);
        canvas.drawRect(20, 420, pageWidth - 20, 470, myPaint);
        canvas.drawRect(20, 420, 100, 1260, myPaint);
        canvas.drawRect(100, 420, 670, 1260, myPaint);
        canvas.drawRect(670, 420, 880, 1260, myPaint);
        canvas.drawRect(20, 420, 520, 1260, myPaint);

        canvas.drawRect(20, 1260, pageWidth - 20, 1400, myPaint);
        canvas.drawRect(20, 1400, pageWidth - 20, 1460, myPaint);
        canvas.drawLine(670, 1260, 670, 1460, myPaint);


        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("S.No.", pageWidth - 1168, 460, myPaint);
        canvas.drawText("Description", pageWidth - 950, 460, myPaint);
        canvas.drawText("Quantity", pageWidth - 650, 460, myPaint);
        canvas.drawText("Rate", 750, 460, myPaint);
        canvas.drawText("Amount", 970, 460, myPaint);


        int start_item1 = 50;
        int start_item2 = 200;
        int start_item3 = 110;
        int start_item4 = 600;
        int start_item5 = 690;
        int start_item6 = 950;


        for (int i = 0; i < count; i++) {


            canvas.drawText(String.valueOf(i + 1), start_item1, end_item, myPaint);

            canvas.drawText(mQty.get(i), start_item4, end_item, myPaint);
            canvas.drawText(indianCurrencyFormat.format(Double.parseDouble( mCost.get(i))), start_item5, end_item, myPaint);
            canvas.drawText(indianCurrencyFormat.format(mTotal.get(i)), start_item6, end_item, myPaint);
            end_item = printNextLine(canvas, myPaint, start_item3, end_item, 410, mProduct_name.get(i), end_item);

            end_item = end_item + 70;


        }


        canvas.drawText("Sub-Total", 780, 1300, myPaint);

        canvas.drawText(indianCurrencyFormat.format(Net_AMT), 970, 1300, myPaint);
        long TotalAmount;
        if (IGST != 0L) {
            long IGSTtotal = (long) (Net_AMT * IGST) / 100;
            TotalAmount = Net_AMT + IGSTtotal;
            canvas.drawText(IGST + "% IGST", 780, 1390, myPaint);
            canvas.drawText(indianCurrencyFormat.format(IGSTtotal), 970, 1390, myPaint);

        } else {
            long SGSTtotal = (long) (Net_AMT * SGST) / 100;
            long CGSTtotal = (long) (Net_AMT * CGST) / 100;
            TotalAmount = (long) Net_AMT + SGSTtotal + CGSTtotal;

            canvas.drawText(SGST + "% SGST", 780, 1350, myPaint);
            canvas.drawText(indianCurrencyFormat.format(SGSTtotal), 970, 1350, myPaint);
            canvas.drawText(CGST + "% CGST", 780, 1390, myPaint);
            canvas.drawText(indianCurrencyFormat.format(CGSTtotal), 970, 1390, myPaint);


        }
        canvas.drawText("Total Amount", 780, 1440, myPaint);


        canvas.drawText(indianCurrencyFormat.format(TotalAmount), 970, 1440, myPaint);

        //canvas.drawText(numbertoword.convert((int) TotalAmount)+" Only",50,1300,myPaint);

        end_item = printNextLine(canvas, myPaint, 50, 1300, 600, convertToIndianCurrency(String.valueOf(TotalAmount)), end_item);

        canvas.drawText("DC Date : " + formatter2.format(day), 350, 1440, myPaint);


        titlePaint.setTextSize(25);
        titlePaint.setUnderlineText(true);
        canvas.drawText("OUR BANK DETAILS:", 20, 1500, titlePaint);
        titlePaint.setUnderlineText(false);
        canvas.drawText("FOR KIRTHANA AGENCIES", pageWidth - 350, 1500, titlePaint);


        myPaint.setTextSize(20f);
        canvas.drawText("KARUR VYSYA BANK,", 20, 1530, myPaint);
        canvas.drawText("ALANDUR BRANCH,", 20, 1555, myPaint);
        canvas.drawText("A/C Holder Name: KIRTHANA AGENCIES", 20, 1580, myPaint);
        canvas.drawText("CA A/C No.: 1104115000010212", 20, 1605, myPaint);
        canvas.drawText("IFSC CODE: KVBL0001104", 20, 1630, myPaint);
        myPaint.setTextSize(15f);
        canvas.drawText("Goods once sold, cannot be taken back or exchanged", 20, 1680, myPaint);
        canvas.drawText("'Subject to Chennai jurisdiction only'", 20, 1700, myPaint);
        canvas.drawText("Our responsibility ceases after goods left our premises.", 20, 1720, myPaint);
        canvas.drawText("Buyer has to do transit insurance on their own.", 20, 1740, myPaint);

        myPaint.setTextSize(40);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        if (SPIS_FIRST_TIME != null) {
            Bitmap bitmap = decodeBase64ToBitmap(SPIS_FIRST_TIME);
            drawSignature(canvas, myPaint, bitmap);
        }
        // canvas.drawText("Signature",850,1660,myPaint);
        float scaleX = 0.3f; // Adjust the scale factor for the x-axis as needed
        float scaleY = 0.3f; // Adjust the scale factor for the y-axis as needed
        canvas.scale(scaleX, scaleY, 800, 1550); // Apply the scale at the specified position

// Draw the scaled Bitmap at the position (700, 1660) on the canvas
        myPaint.setTextSize(20f);


        ColorFilter filter1 = new LightingColorFilter(Color.WHITE, 1);
        myPaint.setColorFilter(filter1);


// Decode the base64 string to a Bitmap
        Bitmap bitmap1 = decodeBase64ToBitmap(SPIS_FIRST_LOGO);

        int newWidth = 200; // Set your desired width
        int newHeight = 200; // Set your desired height

// Resize the bitmap if necessary
        Bitmap resizedBitmap = resizeBitmap(bitmap1, newWidth, newHeight);

// Draw the resized bitmap on the canvas at the specified coordinates
        canvas.drawBitmap(resizedBitmap, -1800, -3275, myPaint);


        // Set the paint object to draw the bitmap as a watermark

        myPaint.setAlpha(20); // Adjust the transparency level (0-255), 0 being fully transparent and 255 fully opaque

// Set the color filter for the bitmap to make it appear as a watermark
        ColorFilter filter = new LightingColorFilter(Color.LTGRAY, 1);
        myPaint.setColorFilter(filter);


        int newWidth1 = 2400; // Set your desired width
        int newHeight1 = 2400; // Set your desired height
        Bitmap resizedBitmap1 = resizeBitmap(bitmap1, newWidth1, newHeight1);

        canvas.drawBitmap(resizedBitmap1, -1100, -1850, myPaint);


        document.finishPage(page);

        end_item = 500;


        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    /**
     * Prints multiline text on a Canvas.
     *
     * @param canvas        The Canvas to draw on.
     * @param paint         The Paint object defining the text attributes.
     * @param x             The x-coordinate of the starting point.
     * @param y             The y-coordinate of the starting point.
     * @param maxWidth      The maximum width for a line.
     * @param multiLineText The input multiline text.
     * @param end_item      The end item value.
     * @return The updated end item value.
     */
    static int printNextLine(Canvas canvas, Paint paint, float x, float y, float maxWidth, String multiLineText, int end_item) {


        StringBuilder currentLine = new StringBuilder();

        for (char c : multiLineText.toCharArray()) {
            float textWidth = paint.measureText(currentLine.toString() + c);

            if (textWidth < maxWidth) {
                // Add the character to the current line
                currentLine.append(c);
            } else {
                // Draw the current line and move to the next line
                canvas.drawText(currentLine.toString(), x, y, paint);
                y += paint.getTextSize() * 1.5f;
                end_item = (int) (end_item + paint.getTextSize() * 1.5);// Adjust line spacing as needed
                currentLine = new StringBuilder(String.valueOf(c));
            }
        }

        // Draw the remaining part of the text
        canvas.drawText("-" + currentLine.toString(), x, y, paint);
        return end_item;
    }

    /**
     * Resizes a Bitmap to the specified width and height.
     *
     * @param bitmap    The original Bitmap to be resized.
     * @param newWidth  The target width for the resized Bitmap.
     * @param newHeight The target height for the resized Bitmap.
     * @return The resized Bitmap.
     */

    public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    /**
     * Draws a signature on the canvas.
     *
     * @param canvas  The canvas on which the signature will be drawn.
     * @param myPaint The Paint object to be used for drawing.
     */
    public static void drawSignature(Canvas canvas, Paint myPaint, Bitmap editBitmap) {
        // Set the desired width and height for the thumbnail
        int targetWidth = 250;
        int targetHeight = 130;

        // Generate a thumbnail without scaling the original image
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(editBitmap, targetWidth, targetHeight, true);

        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(resizedBitmap, targetWidth, targetHeight);

        // Set anti-aliasing for smooth drawing
        myPaint.setAntiAlias(true);

        // Draw the thumbnail at the adjusted position on the canvas
        if (thumbnail != null) {
            canvas.save(); // Save the current canvas state

            myPaint.setTextSize(40);
            myPaint.setColor(Color.BLACK);
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            // Calculate the adjusted position for the thumbnail
            float adjustedThumbnailX = 850;
            float adjustedThumbnailY = 1520;

            // Draw the text
            canvas.drawText("Signature", 850, 1680, myPaint);

            // Draw the thumbnail
            canvas.drawBitmap(thumbnail, adjustedThumbnailX, adjustedThumbnailY, myPaint);

            canvas.restore(); // Restore the canvas to the saved state
        }
    }
}
