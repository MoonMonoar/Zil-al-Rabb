package com.immo2n.halalife.Custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ImageUtils {
    //Model Classes
    public static class BitmapImageScaled {
        private Bitmap bitmap;
        private float scale;
        public BitmapImageScaled(Bitmap bitmap, float scale){
            this.bitmap = bitmap;
            this.scale = scale;
        }
        public Bitmap getBitmap() {
            return bitmap;
        }
        public float getScale() {
            return scale;
        }
    }

    public static Bitmap uriToBitmap(Context context, Uri imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean saveUriToFile(Context context, Uri uri, File destinationFile) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return saveBitmapToFile(bitmap, destinationFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File destinationFile) {
        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Uri bitmapToUri(Bitmap bitmap, String file_name, Activity activity) {
        try {
            File imageFile = new File(activity.getFilesDir(), file_name);
            FileOutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return Uri.fromFile(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}