package com.inuker.library;

import android.graphics.Bitmap;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liwentian on 2017/10/12.
 */

public class BitmapUtils {

    public static void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public static void saveBitmap(InputStream inputStream, File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) > 0) {
            bos.write(buffer, 0, bytesRead);
        }
        bos.close();
        inputStream.close();
    }

    public static void saveBitmap(Bitmap bitmap, File file) {
        saveBitmap(bitmap, 100, file);
    }

    public static void saveBitmap(Bitmap bitmap, int quality, File file) {
        try {
            if (bitmap == null || file == null) {
                return;
            }
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
