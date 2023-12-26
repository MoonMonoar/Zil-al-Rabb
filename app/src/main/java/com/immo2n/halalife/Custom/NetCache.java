package com.immo2n.halalife.Custom;

import android.content.Context;

public class NetCache {
    public interface netCacheCallback {
        void onDone(String result);
        void onFail(String error);
    }
    private final DBhandler dbHandler;

    public NetCache(Global global){
        Context context = global.getContext();
        this.dbHandler = new DBhandler(context);
    }

    public void getDataFromCache(String hash, netCacheCallback callback){
        dbHandler.getCache(hash, new DBhandler.DBhandlerCallback() {
            @Override
            public void onDone(String result) {
                callback.onDone(result);
            }

            @Override
            public void onFail(String error) {
                callback.onFail(error);
            }
        });
    }
    public void storeDataInCache(String hash, String value){
        dbHandler.addCache(hash, value);
    }
    public void clearCache(){
        dbHandler.clearCache();
    }
}