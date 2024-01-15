package com.immo2n.halalife.Custom;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Objects;

public class FileUtils {
    private static final String[] videoFileExtensions = {
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "3gp", "webm"
    };
    public static boolean isVideoFile(File file) {
        if (file == null || !file.isFile()) {
            return false;
        }
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(lastDotIndex + 1).toLowerCase();
            for (String videoExtension : videoFileExtensions) {
                if (videoExtension.equals(fileExtension)) {
                    return true;
                }
            }
        }
        return false;
    }
    @SuppressLint("DefaultLocale")
    public static String getVideoDuration(String filePath) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(filePath);
            String mimeType = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            if (mimeType != null && mimeType.startsWith("video/")) {
                long durationMillis = Long.parseLong(Objects.requireNonNull(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
                return String.format("%02d:%02d:%02d", durationMillis / 3600000, (durationMillis % 3600000) / 60000, (durationMillis % 60000) / 1000);
            }
            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00:00";
    }
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