package com.immo2n.halalife.Custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static void saveImageUriToFile(Context context, Uri uri, File destinationFile) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                saveBitmapToFile(bitmap, destinationFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, File destinationFile) {
        try (FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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