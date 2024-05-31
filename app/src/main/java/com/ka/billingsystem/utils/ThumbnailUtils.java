package com.ka.billingsystem.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.graphics.Path;

public class ThumbnailUtils {

    public static Bitmap createThumbnail(Context context, Bitmap sourceBitmap) {
        // Convert dp to pixels
        int thumbnailSizeInDp = 40;
        int thumbnailSizeInPixels = dpToPixels(context, thumbnailSizeInDp);

        // Resize the bitmap to the desired thumbnail size
        Bitmap resizedBitmap = resizeBitmap(sourceBitmap, thumbnailSizeInPixels, thumbnailSizeInPixels);

        // Create a circular bitmap
        return getCircularBitmap(resizedBitmap);
    }

    private static int dpToPixels(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private static Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        float radius = Math.min(width, height) / 2.0f;

        canvas.drawCircle(width / 2.0f, height / 2.0f, radius, paint);
        paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));

        // Create a circular path
        Path path = new Path();
        path.addCircle(width / 2.0f, height / 2.0f, radius, Path.Direction.CW);
        canvas.clipPath(path);

        canvas.drawBitmap(bitmap, 0, 0, paint);

        return output;
    }
}
