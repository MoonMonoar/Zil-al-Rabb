package com.immo2n.halalife.Core;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.DataObjects.FileCallback;
import com.immo2n.halalife.DataObjects.UploaderResponse;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class FileUploader {
    private static final String TAG = "FILE_UPLOADER";
    public interface UploadCallback {
        void onProgressUpdate(int percent, String fileName);
        void onUploadComplete(FileCallback fileCallback);
        void onUploadFailed(String errorMessage);
    }
    public static void uploadFile(String UserToken, File file, String reason, UploadCallback callback) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        OkHttpClient client = clientBuilder.build();

        String randomFileName = generateRandomFileName() + getFileExtension(file.getName());

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", randomFileName, createRequestBody(file, callback, randomFileName))
                .addFormDataPart("token", UserToken)
                .addFormDataPart("reason", reason)
                .build();

        Request request = new Request.Builder()
                .url(Server.routeUploadFile)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String serverResponse = response.body().string();
                    Gson gson = new Gson();
                    UploaderResponse uploaderResponse = gson.fromJson(serverResponse, UploaderResponse.class);
                    if(uploaderResponse.isSuccess()){
                        FileCallback fileCallback = new FileCallback();
                        fileCallback.setStatus(FileCallback.FLAG_SUCCESS);
                        fileCallback.setProgress(100);
                        fileCallback.setReason(reason);
                        fileCallback.setFile(randomFileName);
                        callback.onUploadComplete(fileCallback);
                    }
                    else {
                        Log.d(Global.LOG_TAG, "Uploader error, response: "+uploaderResponse);
                    }
                } else {
                    callback.onUploadFailed("HTTP Error Code: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onUploadFailed(e.getMessage());
            }
        });
    }
    private static String generateRandomFileName() {
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(1000000);
        return timestamp + "_" + random;
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    private static RequestBody createRequestBody(final File file, final UploadCallback callback, String name) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream");
            }
            @Override
            public void writeTo(@NonNull BufferedSink sink) throws IOException {
                try (Source source = Okio.source(file)) {
                    long contentLength = contentLength();
                    long totalBytesWritten = 0;
                    Buffer buffer = new Buffer();
                    long bytesRead;
                    while ((bytesRead = source.read(buffer, 2048)) != -1) {
                        sink.write(buffer, bytesRead);
                        totalBytesWritten += bytesRead;
                        int percent = (int) ((totalBytesWritten * 100) / contentLength);
                        callback.onProgressUpdate(percent, name);
                    }
                }
            }
            @Override
            public long contentLength() {
                return file.length();
            }
        };
    }
}
