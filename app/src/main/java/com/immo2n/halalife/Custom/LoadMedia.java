package com.immo2n.halalife.Custom;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class LoadMedia {
    private static final int BUFFER_SIZE = 4096;
    private static final long MAX_CACHE_SIZE = 1073741824;
    private final File cacheDir;
    public LoadMedia(Context context) {
        this.cacheDir = FolderUtils.getDiscCacheInData(context);
    }
    public interface CallBack {
        void onDone(File file);
        void onFail(String message);
    }
    public void get(String fileUrl, CallBack callBack) {
        File cachedFile = getCacheFile(fileUrl);
        if (cachedFile.exists()) {
            callBack.onDone(cachedFile);
        }
        else {
            if (getCacheSize() > MAX_CACHE_SIZE) {
                deleteQuarterOfFiles();
            }
            new Thread(()-> downloadFile(fileUrl, cachedFile, callBack)).start();
        }
    }
    private File getCacheFile(String fileUrl) {
        String fileName = getFileNameFromUrl(fileUrl);
        return new File(cacheDir, fileName);
    }
    private String getFileNameFromUrl(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }
    private void downloadFile(String fileUrl, File destination, CallBack callBack) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(destination);
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                long contentLength = connection.getContentLength();
                long totalBytesRead = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
                output.close();
                input.close();
                connection.disconnect();
                if (contentLength >= 0 && totalBytesRead != contentLength) {
                    if (destination.exists() && destination.isFile()) {
                        destination.delete();
                    }
                    callBack.onFail("Downloaded file length does not match the expected length");
                } else {
                    callBack.onDone(destination);
                }
            } else {
                callBack.onFail("Response code: " + responseCode);
                Log.e("MAGAZINE-REMOTE-MEDIA-ERROR", "Failed to download file. Response code: " + responseCode);
            }
        }
        catch (IOException e) {
            callBack.onFail(e.toString());
            Log.e("MAGAZINE-REMOTE-MEDIA-ERROR", "Error downloading file: " + e.getMessage());
        }
    }
    private void deleteQuarterOfFiles() {
        if (cacheDir.exists() && cacheDir.isDirectory()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                int numFilesToDelete = files.length / 4;
                for (int i = 0; i < numFilesToDelete; i++) {
                    files[i].delete();
                }
            }
        }
    }
    private long getCacheSize() {
        long size = 0;
        if (cacheDir.exists() && cacheDir.isDirectory()) {
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    size += file.length();
                }
            }
        }
        return size;
    }
}