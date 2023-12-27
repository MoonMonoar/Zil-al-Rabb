package com.immo2n.halalife.Core;

import static com.immo2n.halalife.Core.FileUploader.uploadFile;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.DataObjects.FileCallback;

import java.io.File;

public class Server {
    public static final String REASON_UPLOAD_DP = "DP",
            REASON_UPLOAD_FACE = "FACE";
    public static int uploader_index;
    public static String apiEndPoint = "http://192.168.124.54"; //Main api end point
    public static String
            routeSignup = apiEndPoint+"/api/signup.php",
            routeGetProfile = apiEndPoint+"/api/getProfile.php",
            routeUploadFile = apiEndPoint+"/api/upload.php",
            routeDPupdate = apiEndPoint+"/api/saveProfilePicture.php",
            routeUpdateProfile = apiEndPoint+"/api/updateProfile.php";
    Global global;
    AppState appState;
    public Server(Global global){
        this.global = global;
        appState = new AppState(global);
    }
    public String getApiEndPoint() {
        return apiEndPoint;
    }
    public static final int UPLOAD_ISOLATED_CODE = 6000;
    public void uploadFilesIsolated(File[] file_list, String[] reason_list, int index, Handler callBack) {
        if (index == 0) {
            if (file_list.length != reason_list.length) {
                return;
            }
            uploader_index = 0;
        }
        uploadFile(appState.getToken(), file_list[index], reason_list[index], new FileUploader.UploadCallback() {
            @Override
            public void onProgressUpdate(int percent, String fileName) {
                //Do nothing
                FileCallback fileCallback = new FileCallback();
                fileCallback.setStatus(FileCallback.FLAG_PROGRESS);
                fileCallback.setFile(fileName);
                fileCallback.setProgress(percent);
                fileCallback.setReason(reason_list[index]);
                Message m = new Message();
                m.what = UPLOAD_ISOLATED_CODE;
                m.obj = fileCallback;
                callBack.sendMessage(m);
            }

            @Override
            public void onUploadComplete(FileCallback fileCallback) {
                //Done uploading
                Message m = new Message();
                m.what = UPLOAD_ISOLATED_CODE;
                m.obj = fileCallback;
                callBack.sendMessage(m);
                uploader_index++;
                //Recall self
                if(uploader_index < file_list.length) {
                    uploadFilesIsolated(file_list, reason_list, uploader_index, callBack);
                }
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                FileCallback fileCallback = new FileCallback();
                fileCallback.setStatus(FileCallback.FLAG_FAILED);
                fileCallback.setReason(reason_list[index]);
                fileCallback.setProgress(0);
                Message m = new Message();
                m.what = UPLOAD_ISOLATED_CODE;
                m.obj = fileCallback;
                callBack.sendMessage(m);
            }
        });
    }
}
