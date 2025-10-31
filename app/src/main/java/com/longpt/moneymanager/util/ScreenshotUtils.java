package com.longpt.moneymanager.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenshotUtils {

    public static void captureAndShare(Activity activity, View view) {
        // 1. Chụp view thành bitmap
        Bitmap bitmap = captureViewToBitmap(view);

        // 2. Lưu vào cache
        File imageFile = saveBitmapToCache(activity, bitmap);
        if (imageFile == null) return;

        // 3. Lấy content Uri
        String authority = activity.getPackageName() + ".fileprovider";
        Uri contentUri;
        try {
            contentUri = FileProvider.getUriForFile(activity, authority, imageFile);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        // 4. Tạo và gửi Intent share
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"));
    }

    private static Bitmap captureViewToBitmap(View view) {
        int width = view.getWidth() > 0 ? view.getWidth() : view.getMeasuredWidth();
        int height = view.getHeight() > 0 ? view.getHeight() : view.getMeasuredHeight();

        Bitmap bmp = Bitmap.createBitmap(Math.max(width, 1), Math.max(height, 1), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);
        return bmp;
    }

    private static File saveBitmapToCache(Activity activity, Bitmap bitmap) {
        File screenshotsDir = new File(activity.getCacheDir(), "screenshots");
        if (!screenshotsDir.exists()) {
            screenshotsDir.mkdirs();
        }

        String fileName = "screenshot_" + System.currentTimeMillis() + ".png";
        File imageFile = new File(screenshotsDir, fileName);

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
