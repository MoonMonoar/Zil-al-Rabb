package com.immo2n.halalife.Core;

import android.content.Context;
import android.util.Log;

import com.immo2n.halalife.Custom.DBhandler;
import com.immo2n.halalife.Custom.Global;

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
}
