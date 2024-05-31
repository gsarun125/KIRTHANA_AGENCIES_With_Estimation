package com.ka.billingsystem.proforma.activities.utils;

import static com.ka.billingsystem.utils.Currency.convertToIndianCurrency;
import static com.ka.billingsystem.utils.ImageEncodeAndDecode.decodeBase64ToBitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
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

public class ProfomaColourInvoice {
    public static File colourPDF(int count, Long Net_AMT, int Bill_NO, String CusName, String CusPhone, List<String> mQty, List<String> mCost, List<Long> mTotal, List<String> mProduct_name, String SPIS_FIRST_TIME, String SPIS_FIRST_LOGO, File file, long time, Long IGST, Long CGST, Long SGST) {
        System.out.println("cusnemae:"+CusName);
        //System.out.println("estimatedAmount"+estimatedAmount);
        // System.out.println(gstType);
        int end_item = 560;
        int pageWidth = 1200;

        NumberFormat indianCurrencyFormat = NumberFormat.getInstance(new Locale("en", "IN"));
        indianCurrencyFormat.setMinimumFractionDigits(2); // If you want to show decimal places


        PdfDocument document = new PdfDocument();
        Paint myPaint = new Paint();
        Paint titlePaint = new Paint();
        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = document.startPage(myPageInfo1);
        Canvas canvas = page.getCanvas();


        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        titlePaint.setTextSize(45);
        titlePaint.setColor(Color.BLACK);
        canvas.drawText("KIRTHANA AGENCIES", 30, 100, titlePaint);
        titlePaint.setTextSize(25);
        canvas.drawText("QUALITY OFFSET PRINTERS & BINDERS", 30, 130, titlePaint);

        //Address
        String Address = "#6,Alikhan Street,";
        String Adddress1 = " Alandur,Chennai-600 016,";
        String Address2 ="Tamil Nadu, India.";
        String Tel = "+91 44 2231 4628";
        String Fax = "+91 44 2231 4635";
        String Email = "kirthana.agencies@outlook.com";
        String GSTIN = "33AEFPJ5208Q1ZB";

        // String Data
        String Ref = "KA/342/2024";

        String Line1 = "To Cost of One offset printing machine,Autoplate,";
        String Line2 = "SIZE 19\"X 26\", Alcohol Damping,CPC, Powder Spray,Non Stop Feeder,";
        String Line3 ="Pnumatic Compressor,Mechanical and Electrical Manuals.....";
        String Line4 ="IGST @18%.....Rs.14,40,000.00";
        String Line5 ="Total.....Rs.94,40,000.00";

        String BLine2 = "Subject to Chennai Jurisdiction";
        String BLine3 = "Delivery: Again Full Payment.";
        String BLine4 = "Bank Details: The Karur Vysya Bank Limited, Alandur Branch,Chennai-600016,";
        String BLine5 = "A/C.No.1104115000010212,     IFSC: KVBL0001104";

        myPaint.setTextAlign(Paint.Align.RIGHT);
        myPaint.setTextSize(25);
        myPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Address: " + Address, pageWidth - 100, 90, myPaint);
        canvas.drawText(Adddress1, pageWidth - 100, 120, myPaint);
        canvas.drawText(Address2, pageWidth - 100, 150, myPaint);

        titlePaint.setTextSize(25);
        canvas.drawText("Mob: 0091 98412 38396", 30, 190, titlePaint);
        canvas.drawText("Ph:"+Tel, 30, 210, titlePaint);
        canvas.drawText("Fax:"+Fax, 30, 230, titlePaint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
        titlePaint.setTextSize(44);
        canvas.drawText("PROFORMA INVOICE",360,300,titlePaint);
        myPaint.setTextSize(20);

        canvas.drawText("GSTIN: " + GSTIN, pageWidth - 100, 200, myPaint);


        SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date day = new Date();
        titlePaint.setTextSize(25);
        canvas.drawText("Ref:"+Ref,30,400,titlePaint);
        canvas.drawText("Date:"+formatter1.format(day),pageWidth - 100, 400,myPaint);

        titlePaint.setTextSize(25);
        canvas.drawText("To",60,450,titlePaint);
        float yPosition =490;
        String[] lines = CusName.split("\n");


        for (String line : lines) {
            canvas.drawText( line.trim()+",", 60, yPosition, titlePaint);
            yPosition += titlePaint.descent() - titlePaint.ascent()+1; // Move to the next line
        }

        titlePaint.setTextSize(30);
        canvas.drawText(Line1,60,700,titlePaint);
        canvas.drawText(Line2,60,740,titlePaint);
        canvas.drawText(Line3,60,780,titlePaint);

        long TotalAmount;

        if (IGST != 0L) {
            long IGSTtotal = (long) (Net_AMT * IGST) / 100;
            TotalAmount = Net_AMT + IGSTtotal;
            canvas.drawText("COST@"+IGST+"%...Rs."+Net_AMT , 660, 820,titlePaint);
            canvas.drawText("IGST@"+IGST+"%...Rs."+IGSTtotal , 660, 860,titlePaint);
            // canvas.drawText(indianCurrencyFormat.format(IGSTtotal), 970, 1390, titlePaint);
            canvas.drawText("Total....Rs."+TotalAmount,660,900,titlePaint);
        } else {

            long SGSTtotal = (long) (Net_AMT * SGST) / 100;
            long CGSTtotal = (long) (Net_AMT * CGST) / 100;
            TotalAmount = (long) Net_AMT + SGSTtotal + CGSTtotal;
            canvas.drawText("COST@"+IGST+"%...Rs."+Net_AMT , 660, 820,titlePaint);
            canvas.drawText("SGST@"+SGST+"%...Rs."+SGSTtotal,660, 860,titlePaint);
            //canvas.drawText(indianCurrencyFormat.format(SGSTtotal), 970, 1350, myPaint);
            canvas.drawText("CGST@"+CGST+"%...Rs."+CGSTtotal, 660, 900,titlePaint);
            canvas.drawText("Total....Rs."+TotalAmount,660,940,titlePaint);
            //  canvas.drawText(indianCurrencyFormat.format(CGSTtotal), 970, 1390, myPaint);
        }
        String BLine1 =  convertToIndianCurrency(String.valueOf(TotalAmount));

        titlePaint.setTextSize(25);
        canvas.drawText(BLine1,30,1320,titlePaint);
        canvas.drawText(BLine2,30,1360,titlePaint);
        canvas.drawText(BLine3,30,1400,titlePaint);
        canvas.drawText(BLine4,30,1440,titlePaint);
        canvas.drawText(BLine5,30,1480,titlePaint);

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
     * @param canvas     The canvas on which the signature will be drawn.
     * @param myPaint    The Paint object to be used for drawing.

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
