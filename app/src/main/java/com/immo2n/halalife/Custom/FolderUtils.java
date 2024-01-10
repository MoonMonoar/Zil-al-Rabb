package com.immo2n.halalife.Custom;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class FolderUtils {
    public static String getImageFileName(){
        return "MZ_" + generateRandomDigits(5) + "_" +System.currentTimeMillis() + ".jpg";
    }
    public static String generateRandomDigits(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }
    public static File getDataFolder(Context context) {
        return context.getFilesDir();
    }
    public static File getTempFolderInData(Context context) {
        File dataFolder = getDataFolder(context);
        File tempFolder = new File(dataFolder, "temp");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        return tempFolder;
    }
    public static void clearTemp(Context context){
        File directory = new File(getTempFolderInData(context).toURI());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
    public static void clearFilterCache(){
        File directory = new File(getHALALiFEFilterCacheFolderInDCIM().toURI());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
    public static void clearSelectionCache(){
        File directory = new File(getHALALiFEImageSelectionFolderInDCIM().toURI());
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }
    public static void clearAllCache(Context context){
        clearTemp(context);
        clearFilterCache();
        clearSelectionCache();
    }
    public static File getDiscCacheInData(Context context) {
        File dataFolder = getDataFolder(context);
        File tempFolder = new File(dataFolder, "disk");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        return tempFolder;
    }
    public static File getHALALiFEFolderInDCIM() {
        File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File HALALiFEFolder = new File(dcimFolder, "HALALiFE");
        if (!HALALiFEFolder.exists()) {
            HALALiFEFolder.mkdirs();
        }
        return HALALiFEFolder;
    }

    public static File getLatestImageFileFromFolder(File folder) {
        File[] files = folder.listFiles((dir, name) ->
                name.toLowerCase().endsWith(".jpg")
                        || name.toLowerCase().endsWith(".png")
                        || name.toLowerCase().endsWith(".jpeg")
        );
        if(files != null) {
            Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            return files.length > 0 ? files[0] : null;
        }
        return null;
    }

    public static File getHALALiFEFilterCacheFolderInDCIM() {
        File dcimFolder = getHALALiFEFolderInDCIM();
        File HALALiFEFolder = new File(dcimFolder, ".filterCache");
        if (!HALALiFEFolder.exists()) {
            HALALiFEFolder.mkdirs();
        }
        return HALALiFEFolder;
    }
    public static File getHALALiFEImageSelectionFolderInDCIM() {
        File dcimFolder = getHALALiFEFolderInDCIM();
        File HALALiFEFolder = new File(dcimFolder, ".selectedImages");
        if (!HALALiFEFolder.exists()) {
            HALALiFEFolder.mkdirs();
        }
        return HALALiFEFolder;
    }
    public static File getPhotosFolder() {
        File photosFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File HALALiFEFolder = new File(photosFolder, "HALALiFE");
        if (!HALALiFEFolder.exists()) {
            HALALiFEFolder.mkdirs();
        }
        return HALALiFEFolder;
    }
    public static File getVideosFolder() {
        File movies = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        File HALALiFEFolder = new File(movies, "HALALiFE");
        if (!HALALiFEFolder.exists()) {
            HALALiFEFolder.mkdirs();
        }
        return HALALiFEFolder;
    }
    public static File getNewImageFileInPhotosFolder(boolean is_hidden){
        if(is_hidden){
            return new File(getPhotosFolder()+"/."+FolderUtils.getImageFileName());
        }
        else {
            return new File(getPhotosFolder().getAbsolutePath()+"/"+FolderUtils.getImageFileName());
        }
    }
    public static File getNewPhotoAsUploadCache(Context context) {
        File dataFolder = getDataFolder(context);
        File tempFolder = new File(dataFolder, "upload");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        return new File(tempFolder, getImageFileName());
    }
}