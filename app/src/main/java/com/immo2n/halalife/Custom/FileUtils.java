package com.immo2n.halalife.Custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;

public class FileUtils {
    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        return decimalFormat.format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    public static boolean isFileGreaterThanKB(long fileSizeInBytes, int TargetKB) {
        long fileSizeInKB = fileSizeInBytes / 1024;
        return fileSizeInKB > TargetKB;
    }
    public static void compressImage(File imageFile, File destination) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int maxFileSizeKB = 450; // Maximum target file size in KB
            int compressionQuality = 90; // Initial compression quality
            if(!imageFile.getAbsolutePath().equals(destination.getAbsolutePath())) {
                copyFile(imageFile, destination);
            }
            while (destination.length() / 1024 > maxFileSizeKB && compressionQuality > 0) {
                byteArrayOutputStream.reset(); // Clear the stream for the next compression
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, byteArrayOutputStream);
                FileOutputStream fos = new FileOutputStream(destination);
                fos.write(byteArrayOutputStream.toByteArray());
                fos.flush();
                fos.close();
                compressionQuality -= 10; // Decrease the compression quality
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void copyFile(File source, File destination) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(source);
             FileOutputStream outputStream = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }
}