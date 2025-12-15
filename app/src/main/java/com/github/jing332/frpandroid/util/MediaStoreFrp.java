package com.github.jing332.frpandroid.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class MediaStoreFrp {

    private MediaStoreFrp() {}

    // 写入字节数组到 /Download/FrpAndroid，返回写入后的 Uri
    public static Uri writeToDownloads(Context context, String fileName, byte[] data) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
            // 目标目录：/storage/emulated/0/Download/FrpAndroid
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/FrpAndroid");

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                    if (out != null) {
                        out.write(data);
                        out.flush();
                    }
                }
            }
            return uri;
        } else {
            // API 29 以下：写入公开 Downloads/FrpAndroid，需运行时权限 WRITE_EXTERNAL_STORAGE
            File base = new File(Environment.getExternalStorageDirectory(), "Download");
            File frpDir = new File(base, "FrpAndroid");
            if (!frpDir.exists()) frpDir.mkdirs();
            File outFile = new File(frpDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                fos.write(data);
                fos.flush();
            }
            return Uri.fromFile(outFile);
        }
    }

    // 写入自 InputStream 到 /Download/FrpAndroid，返回写入后的 Uri
    public static Uri writeToDownloads(Context context, String fileName, InputStream input) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
            values.put(MediaStore.Downloads.RELATIVE_PATH, "Download/FrpAndroid");

            Uri uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                    if (out != null) {
                        byte[] buffer = new byte[8192];
                        int read;
                        while ((read = input.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        out.flush();
                    }
                }
            }
            return uri;
        } else {
            File base = new File(Environment.getExternalStorageDirectory(), "Download");
            File frpDir = new File(base, "FrpAndroid");
            if (!frpDir.exists()) frpDir.mkdirs();
            File outFile = new File(frpDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
            }
            return Uri.fromFile(outFile);
        }
    }

    public static boolean isPre29AndPermissionNeeded() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q;
    }
}
