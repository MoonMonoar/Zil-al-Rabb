package com.immo2n.halalife.Core;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.immo2n.halalife.Custom.FileUtils;
import com.immo2n.halalife.Custom.FolderUtils;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.Net;
import com.immo2n.halalife.DataObjects.CommonResponse;
import com.immo2n.halalife.DataObjects.CreatorPayload;
import com.immo2n.halalife.DataObjects.FileCallback;
import com.immo2n.halalife.Main.Home;
import com.immo2n.halalife.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatorService extends Service {
    public static final String PAYLOAD_TAG = "PAYLOAD";
    private static final String TAG = "Creator Service";
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Zil Creator";
    private Global global;
    private AppState appState;
    private List<String> fileList = new ArrayList<>();

    public interface UploadServiceCallBack{
        void onSuccess();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        global = new Global(getApplicationContext(), null);
        appState = new AppState(global);
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String payload = intent.getStringExtra(PAYLOAD_TAG);
        try {
            //doneUploading();
            CreatorPayload creatorPayload = new Gson().fromJson(payload, CreatorPayload.class);
            //Everything happens here!
            //Upload files
            List<String> pathList = creatorPayload.getFileList();
            if(pathList.size() > 0){
                //Upload first then process
                uploadFiles(pathList, 0, creatorPayload.getMode() == 1, () -> processPost(creatorPayload));
            }
            else {
                processPost(creatorPayload);
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Failed to post!", Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        return START_STICKY;
    }

    private void processPost(CreatorPayload creatorPayload) {
        Log.d(TAG, "PROCESS CALLED!");
        new Net(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 70){
                    try {
                        String response = msg.obj.toString();
                        CommonResponse commonResponse = global.getGson().fromJson(response, CommonResponse.class);
                        if(commonResponse.isStatusOk()){
                            doneUploading();
                        }
                        else {
                            failUploading(commonResponse.getComment());
                        }
                    }
                    catch (Exception e){
                        failUploading(null);
                    }
                }
            }
        }, global, false).post(
                Server.routeCreatePost,
                "token="+global.makeUrlSafe(appState.getToken())+
                        "&body="+global.makeUrlSafe(creatorPayload.getBody())+
                        "&privacy="+global.makeUrlSafe(Integer.toString(creatorPayload.getPrivacy()))+
                        "&file_array="+global.makeUrlSafe(global.getGson().toJson(fileList)),
                70
        );

    }

    private void uploadFiles(List<String> filePaths, int index, boolean isReel, UploadServiceCallBack callBack) {
        if(filePaths.size() == index){
            //Last one
            callBack.onSuccess();
            return;
        }
        File target = new File(filePaths.get(index));
        String reason = "PHOTO";
        if(FileUtils.isVideoFile(target)){
            reason = "VIDEO";
            if(isReel){
                reason = "REEL";
            }
        }
        FileUploader.uploadFile(appState.getToken(), target, reason, new FileUploader.UploadCallback() {
            @Override
            public void onProgressUpdate(int percent, String fileName) {
                //Do none for now
            }

            @Override
            public void onUploadComplete(FileCallback fileCallback) {
                if(fileCallback.getStatus().equals(FileCallback.FLAG_SUCCESS)){
                    String path = fileCallback.getFile();
                    if(FileUtils.isVideoFile(target)){
                        path = "/videos/"+fileCallback.getFile();
                        if(isReel){
                            path = "/reels/"+fileCallback.getFile();
                        }
                    }
                    fileList.add(path);
                }
                uploadFiles(filePaths, index+1, isReel, callBack);
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                failUploading(null);
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Zil Creator",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @SuppressLint("ForegroundServiceType")
    private void showNotification() {
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Uploading post")
                .setContentText("Uploading your post in background")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Notification notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, notification);
    }
    public void doneUploading(){
        stopForeground(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Post uploaded")
                .setContentText("Your post was successfully uploaded!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        FolderUtils.clearCreatorCache();
    }
    public void failUploading(String comment){
        stopForeground(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Upload failed")
                .setContentText((null != comment && comment.equals("NSFW"))?"One or more content is not halal! Please upload only halal content.":"Failed to upload your post!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        FolderUtils.clearCreatorCache();
    }
}
