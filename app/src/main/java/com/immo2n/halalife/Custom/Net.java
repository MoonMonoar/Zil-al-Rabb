package com.immo2n.halalife.Custom;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.immo2n.halalife.CrossProgram.MD5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Net {
    public static int NET_CODE_SESSION = 1;
    public static int NET_CODE_SIGNUP = 2;
    public static int NET_CODE_SEND_VERIFICATION_EMAIL = 2;
    private final NetCache netCache;
    private static Handler netHandler;
    private final boolean net_cache_mode;
    private static WeakReference<Global> globalRef;
    public Net(Handler handler, Global global_object, boolean cache) {
        netHandler = handler;
        net_cache_mode = cache;
        globalRef = new WeakReference<>(global_object);
        netCache = new NetCache(global_object);
    }
    public interface parallelEvents {
        void onResponse(String response);
        void onError(String message);
    }

    public void postParallel(String url, String payload, parallelEvents callBack){
        Global global = globalRef.get();
        payload+= "&device_id="+global.makeUrlSafe(global.getDeviceID()); //Auto device signature
        String cache_key = MD5.Generate(url+payload); // MASH URL WITH PAYLOAD DATA TO MAKE KEY
        if(net_cache_mode){
            //Check cache --  return if exists
            String finalPayload1 = payload;
            final boolean[] done = {false};
            netCache.getDataFromCache(cache_key, new NetCache.netCacheCallback() {
                @Override
                public void onDone(String result) {
                    callBack.onResponse(result);
                    done[0] = true;
                    //Still load and store the new data for next call
                    new Thread(() -> {
                        try {
                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Accept", "application/json");
                            con.setDoOutput(true);
                            con.setDoInput(true);
                            OutputStream os = con.getOutputStream();
                            os.write(finalPayload1.getBytes(StandardCharsets.UTF_8));
                            os.flush();
                            os.close();
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            netCache.storeDataInCache(cache_key, response.toString());
                        }
                        catch (Exception e){
                            callBack.onError(e.toString());
                            Log.e("MAGAZINE-NET", e.toString());
                        }
                    }).start();
                }
                @Override
                public void onFail(String error) {
                    callBack.onError(error);
                    Log.d("MAGAZINE-NET-PR ERROR-CACHE", error);
                }
            });
            if(done[0]){
                return;
            }
        }
        if(!globalRef.get().netConnected()){
            callBack.onResponse(null);
            return;
        }
        String finalPayload = payload;
        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                OutputStream os = con.getOutputStream();
                os.write(finalPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                netCache.storeDataInCache(cache_key, response.toString());
                callBack.onResponse(response.toString());
            }
            catch (Exception e){
                Log.d("MAGAZINE-NET-PR ERROR", e.toString());
                callBack.onError(e.toString());
            }
        }).start();
    }
    public void get(String url) {
        Log.i("MAGAZINE-NET-CALL", "HIDDEN GET SR NET CALL");
        httpRequest(url, "GET");
    }
    //This is separated from http request method because it has to transfer payloads
    public void post(String url, String payload, Integer request_identifier){
        Log.i("MAGAZINE-NET-CALL", "HIDDEN POST SR NET CALL");
        Global global = globalRef.get();
        payload+= "&device_id="+global.makeUrlSafe(global.getDeviceID()); //Auto device signature
        String cache_key = MD5.Generate(url+payload); // MASH URL WITH PAYLOAD DATA TO MAKE KEY
        if(null == request_identifier){
            request_identifier = 0;
        }
        if(net_cache_mode){
            //Check cache --  return if exists
            Integer finalRequest_identifier1 = request_identifier;
            final boolean[] done = {false};
            String finalPayload1 = payload;
            netCache.getDataFromCache(cache_key, new NetCache.netCacheCallback() {
                @Override
                public void onDone(String result) {
                    if(null != netHandler){
                        Message message = new Message();
                        message.obj = result;
                        message.what = finalRequest_identifier1;
                        netHandler.sendMessage(message);
                    }
                    done[0] = true;
                    //Still load and store the new data for next call
                    new Thread(() -> {
                        try {
                            URL obj = new URL(url);
                            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                            con.setRequestMethod("POST");
                            con.setRequestProperty("Accept", "application/json");
                            con.setDoOutput(true);
                            con.setDoInput(true);
                            OutputStream os = con.getOutputStream();
                            os.write(finalPayload1.getBytes(StandardCharsets.UTF_8));
                            os.flush();
                            os.close();
                            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                            String inputLine;
                            StringBuilder response = new StringBuilder();
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            netCache.storeDataInCache(cache_key, response.toString());
                        }
                        catch (Exception e){
                            Log.e("MAGAZINE-NET", e.toString());
                        }
                    }).start();
                }

                @Override
                public void onFail(String error) {
                    done[0] = true;
                    Log.e("MAGAZINE-NET", error);
                }
            });
            if(done[0]){
                return;
            }
        }
        if(!globalRef.get().netConnected() && null != netHandler){
            Message message = new Message();
            message.what = request_identifier;
            message.obj = "ERROR_NO_NET";
            netHandler.sendMessage(message);
            return;
        }
        Integer finalRequest_identifier = request_identifier;
        String finalPayload = payload;
        new Thread(() -> {
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                OutputStream os = con.getOutputStream();
                os.write(finalPayload.getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                if(net_cache_mode){
                    netCache.storeDataInCache(cache_key, response.toString());
                }
                if(null != netHandler) {
                    Message message = new Message();
                    message.what = finalRequest_identifier;
                    message.obj = response.toString();
                    netHandler.sendMessage(message);
                }
            }
            catch (Exception e){
                if(null == netHandler){
                    Log.d("MAGAZINE-NET", e.toString());
                    return;
                }
                Message message = new Message();
                message.what = finalRequest_identifier;
                message.obj = e.toString();
                netHandler.sendMessage(message);
            }
        }).start();
    }
    public void data(String url, String token) {
        //CACHE FREE - HOT RELOAD EVERY TIME
        new Thread(() -> {
            if(globalRef.get().netConnected()){
                Message message = new Message();
                message.obj = "ERROR_NO_NET";
                netHandler.sendMessage(message);
                return;
            }
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);
                con.setDoInput(true);
                OutputStream os = con.getOutputStream();
                os.write(("token=" + globalRef.get().makeUrlSafe(token)).getBytes(StandardCharsets.UTF_8));
                os.flush();
                os.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Message message = new Message();
                message.obj = response.toString();
                netHandler.sendMessage(message);
            }
            catch (Exception e){
                Message message = new Message();
                message.obj = e.toString();
                netHandler.sendMessage(message);
            }
        }).start();
    }
    public static void httpRequest(String url, String netMethod) {
        new Thread(() -> {
            if(!globalRef.get().netConnected()){
                Message message = new Message();
                message.obj = "ERROR_NO_NET";
                netHandler.sendMessage(message);
                return;
            }
            String method = netMethod;
            try {
                if (!method.equals("GET") && !method.equals("POST")) {
                    method = "GET";
                }
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod(method);
                con.setRequestProperty("Accept", "application/json");
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                Message message = new Message();
                message.obj = response.toString();
                netHandler.sendMessage(message);
            }
            catch (Exception e){
                Message message = new Message();
                message.obj = e.toString();
                netHandler.sendMessage(message);
            }
        }).start();
    }
}