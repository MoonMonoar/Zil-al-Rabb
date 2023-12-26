package com.immo2n.halalife.Custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.immo2n.halalife.R;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Enumeration;

public class Global {
    private Gson gson = new Gson();
    public static String LOG_TAG = "UNICORE_BUG-LOG";
    private Context context;
    private Activity activity;
    public Global(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
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
            LayoutInflater inflater = activity.getLayoutInflater();
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.toast, null);
            Toast toast = new Toast(context);
            TextView textView = layout.findViewById(R.id.textView);
            textView.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        catch (Exception e){
            Log.e(LOG_TAG, e.toString());
        }
    }
}
