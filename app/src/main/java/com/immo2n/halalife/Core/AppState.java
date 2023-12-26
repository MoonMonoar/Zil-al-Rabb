package com.immo2n.halalife.Core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.immo2n.halalife.Custom.DBhandler;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.Net;
import com.immo2n.halalife.DataObjects.ProfileResponse;

public class AppState {
    private DBhandler dBhandler;
    private Global global;
    Context context;
    public AppState(Global global) {
        this.context = global.getContext();
        dBhandler = new DBhandler(context);
        this.global = global;
    }
    public boolean isUserLoggedIn(){
        String check = dBhandler.getSettings(DBhandler.userLoggedInEntryName);
        if(null != check && check.equals(DBhandler.signalTrue)){
            return true;
        }
        return false;
    }
    public Profile getProfile(){
        String profile = dBhandler.getSettings(DBhandler.userProfileEntryName);
        if(null != profile){
            try {
                return global.getGson().fromJson(profile, Profile.class);
            }
            catch (Exception e){
                Log.d(Global.LOG_TAG, e.toString());
            }
        }
        return null;
    }
    public String getToken(){
        if(isUserLoggedIn()){
            return dBhandler.getSettings(DBhandler.userTokenEntryName);
        }
        return "null";
    }
    public void syncProfileAuto(){
        new Net(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                try {
                    Log.d(Global.LOG_TAG, msg.obj.toString());
                    ProfileResponse response = global.getGson().fromJson(msg.obj.toString(), ProfileResponse.class);
                    if(response.isStatusOk()){
                        dBhandler.addSetting(DBhandler.userProfileEntryName, global.getGson().toJson(response.getProfile()));
                    }
                }
                catch (Exception e){
                    Log.d(Global.LOG_TAG, e.toString());
                }
            }
        }, global, false).post(Server.routeGetProfile, "token="+global.makeUrlSafe(getToken()), 3);
    }
}
