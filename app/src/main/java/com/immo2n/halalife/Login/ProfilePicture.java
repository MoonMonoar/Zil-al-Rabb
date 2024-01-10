package com.immo2n.halalife.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.DBhandler;
import com.immo2n.halalife.Custom.FileUtils;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.ImageUtils;
import com.immo2n.halalife.Custom.Net;
import com.immo2n.halalife.DataObjects.FileCallback;
import com.immo2n.halalife.DataObjects.HalalCheckObject;
import com.immo2n.halalife.Main.Home;
import com.immo2n.halalife.R;
import com.immo2n.halalife.SubActivity.CropImage;
import com.immo2n.halalife.databinding.ActivityProfilePictureBinding;

import java.io.File;
import java.io.FileOutputStream;

public class ProfilePicture extends AppCompatActivity {
    private ActivityProfilePictureBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher, cropLauncher;
    private ActivityResultLauncher<Uri> pickLauncher;
    private Global global;
    private AppState appState;
    private DBhandler dBhandler;
    private Server server;
    private boolean haveCameraPermission = false;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private File tempFile, tempFileMain;

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilePictureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.signup_page_status_bar));

        global = new Global(this, this);
        appState = new AppState(global);
        server = new Server(global);
        dBhandler = new DBhandler(this);

        tempFile = new File(this.getFilesDir(), "profile_picture.jpg");
        tempFileMain = new File(this.getFilesDir(), "profile_picture_main.jpg");

        pickLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), r-> {
            processImage();
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ImageUtils.saveUriToFile(global.getContext(), result.getData().getData(), tempFile);
                        processImage();
                    }
                });

        binding.openCamera.setOnClickListener(view -> {
            if(!haveCameraPermission){
                gotoAppSettings();
            }
            pickLauncher.launch(createImageUri());
        });

        binding.openGallery.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            galleryLauncher.launch(intent);
        });

        checkCameraPermission();

        cropLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //Process the image file
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (null != data) {
                    if (null != data.getStringExtra("path")) {
                        binding.picture.setImageBitmap(null);
                        binding.picture.setImageURI(Uri.fromFile(tempFile));
                        FileUtils.compressImage(tempFileMain, tempFileMain);
                        binding.saveProgress.setVisibility(View.VISIBLE);
                        uploadFiles(tempFileMain, tempFile);
                    }
                }
            }
        });

    }

    private void processImage() {
        if(tempFile.length() > 0){
            try {
                FileUtils.copyFile(tempFile, tempFileMain);
                cropLauncher.launch(new Intent(this, CropImage.class).putExtra("path", tempFile.getAbsolutePath()));
            }
            catch (Exception e){
                global.toast("Could not copy file!");
            }
        }
        else {
            global.toast("Try again!");
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            haveCameraPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                haveCameraPermission = true;
            } else {
                // Camera permission denied, handle accordingly (show a message, etc.)
                global.toast("Give camera permission!");
                gotoAppSettings();
                haveCameraPermission = false;
            }
        }
    }

    private Uri createImageUri(){
        if(tempFile.delete()){
            tempFile = new File(this.getFilesDir(), "profile_picture.jpg");
        }
        return FileProvider.getUriForFile(this, "com.immo2n.halalife.fileprovider", tempFile);
    }

    private void gotoAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String face_file, dp_file;
    private void uploadFiles(File face, File fullDp) {
        if(null != face && null != fullDp){
            server.uploadFilesIsolated(new File[]{
                            face,
                            fullDp
                    },
                    new String[]{
                            Server.REASON_UPLOAD_FACE,
                            Server.REASON_UPLOAD_DP
                    },
                    0,
                    new Handler(Looper.getMainLooper()){
                        private int faceProgress = 0;
                        private int fullDpProgress = 0;
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == Server.UPLOAD_ISOLATED_CODE) {
                                FileCallback fileCallback = (FileCallback) msg.obj;
                                if (fileCallback.getStatus().equals(FileCallback.FLAG_PROGRESS)) {
                                    if (fileCallback.getReason().equals(Server.REASON_UPLOAD_FACE)) {
                                        faceProgress = fileCallback.getProgress();
                                        if (null == face_file || !face_file.equals(fileCallback.getFile())) {
                                            face_file = fileCallback.getFile();
                                        }
                                    } else if (fileCallback.getReason().equals(Server.REASON_UPLOAD_DP)) {
                                        fullDpProgress = fileCallback.getProgress();
                                        if (null == dp_file || !dp_file.equals(fileCallback.getFile())) {
                                            dp_file = fileCallback.getFile();
                                        }
                                    }
                                    int totalProgress = (faceProgress + fullDpProgress) / 2;

                                    binding.saveProgress.setProgress(totalProgress);

                                    if (totalProgress == 100) {
                                        if (null == dp_file || null == face_file) {
                                            global.toast("Failed! Try later");
                                        }
                                        saveToProfile(dp_file, face_file);
                                    }
                                }
                                else {
                                    Log.d(Global.LOG_TAG, "File Upload Callback: " + fileCallback.getStatus());
                                }
                            }
                        }
                    }
            );
        }
    }

    private void saveToProfile(String dpFile, String faceFile) {
        runOnUiThread(() -> binding.saveMessage.setText(global.getActivity().getText(R.string.saving)));
        binding.saveProgress.setVisibility(View.GONE);
        binding.saveMessage.setVisibility(View.VISIBLE);
        new Net(new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(msg.what == 3){
                    HalalCheckObject object = global.getGson().fromJson(msg.obj.toString(), HalalCheckObject.class);
                    if(object.isSuccess()){
                        if(object.isHalal()){
                            runOnUiThread(() -> binding.saveMessage.setText(global.getActivity().getText(R.string.image_looks_good)));
                            //Its saved already, go home -- sync tho
                            appState.syncProfileAuto();
                            Intent intent = new Intent(ProfilePicture.this, Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            runOnUiThread(() -> binding.saveMessage.setText(global.getActivity().getText(R.string.image_is_not_halal_please_select_halal_image)));
                            global.vibrate(500);
                        }
                    }
                    else {
                        global.toast("Please restart app!");
                    }
                }
            }
        }, global, false).post(Server.routeDPupdate, "token="+global.makeUrlSafe(
                appState.getToken())+"&dp="+dpFile+"&face="+faceFile
                , 3);
    }

}