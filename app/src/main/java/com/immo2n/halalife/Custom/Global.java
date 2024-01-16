package com.immo2n.halalife.Custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.immo2n.halalife.R;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class Global {
    private Gson gson = new Gson();
    public static String LOG_TAG = "UNICORE_BUG-LOG";
    private Context context;
    private Activity activity;
    public Global(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }
    public void setBoldText(TextView textView, String fullText, String boldText) {
        SpannableString spannableString = new SpannableString(fullText);
        int startIndex = fullText.indexOf(boldText);
        int endIndex = startIndex + boldText.length();
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        runOnUI(() -> textView.setText(spannableString));
    }
    public Drawable getDrawable(int id){
        return ContextCompat.getDrawable(context, id);
    }
    public Gson getGson(){
        return gson;
    }
    public Context getContext() {
        return context;
    }
    public Activity getActivity() {
        return activity;
    }
    public String makeUrlSafe(String input) {
        if(null == input){
            return null;
        }
        try {
            String encodedString = URLEncoder.encode(input, "UTF-8");
            encodedString = encodedString.replace("+", "%20");
            encodedString = encodedString.replace("*", "%2A");
            encodedString = encodedString.replace("%7E", "~");
            return encodedString;
        } catch (UnsupportedEncodingException e) {
            return input;
        }
    }
    public String getDeviceName() {
        return Build.MANUFACTURER+"("+Build.BRAND+" "+Build.MODEL+")";
    }

    public String getDeviceID() {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }
    public String getIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        if(null == ip) {
                            ip = inetAddress.getHostAddress();
                        }
                    }
                }
            }

        } catch (SocketException e) {
            return null;
        }
        return ip;
    }
    public void toast(String msg){
        try {
            runOnUI(() -> {
                LayoutInflater inflater = activity.getLayoutInflater();
                @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
                Toast toast = new Toast(context);
                TextView textView = layout.findViewById(R.id.textView);
                textView.setText(msg);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            });
        }
        catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }
    }

    public boolean netConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
    public boolean isValidLink(String link) {
        String regex = "^(http|https)://([a-zA-Z0-9\\-.]+\\.[a-zA-Z]{2,}([0-9]+)?)(/[a-zA-Z0-9\\-._?,'&%$#=~]+)*$";
        return link.matches(regex);
    }

    public void statusBarColorSet(Window window, int color){
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(context, color));
    }

    public Dialog makeDialogue(int layout, int background){
        Dialog dialog = new Dialog(context);
        dialog.setContentView(layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(context, background));
        dialog.setCanceledOnTouchOutside(true);
        Window d_window = dialog.getWindow();
        d_window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    public String getCurrentDateTime(){
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(calendar.getTime());
    }
    public String getCurrentTimeStamp(){
        Calendar calendar = Calendar.getInstance();
        return Long.toString(calendar.getTimeInMillis());
    }
    public int measureViewHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }
    public String getTimeFromStandard(String time){
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(time);
            assert date != null;
            long timestamp = date.getTime();
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM d, yyyy 'at' h:mma (z)", Locale.getDefault());
            return dateFormat2.format(timestamp);
        }
        catch (Exception e){
            return time;
        }
    }
    public String formatNumber(long number){
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        // Format the number with commas
        return numberFormat.format(number);
    }
    public String getReadableTime(String inputDateString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a");
        try {
            Date date = inputDateFormat.parse(inputDateString);
            if(null != date) {
                return outputDateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDateString;
        }
        return inputDateString;
    }
    public String generateOTP(int length) {
        final String NUMERIC_CHARACTERS = "0123456789";
        if (length <= 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(NUMERIC_CHARACTERS.length());
            char randomChar = NUMERIC_CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    public void runOnUI(Runnable runnable){
        activity.runOnUiThread(runnable);
    }
    public void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(duration);
            }
        }
    }
    public float getDisplayHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
        return 0;
    }

    public float getDisplayWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
        return 0;
    }
}
