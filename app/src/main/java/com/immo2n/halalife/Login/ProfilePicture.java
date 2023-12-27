package com.immo2n.halalife.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;

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
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.ImageUtils;
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
    private File tempFile;

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
                        //Finally upload the file
                        binding.picture.setImageURI(Uri.fromFile(tempFile));
                    }
                }
            }
        });

    }

    private void processImage() {
        if(tempFile.length() > 0){
            cropLauncher.launch(new Intent(this, CropImage.class).putExtra("path", tempFile.getAbsolutePath()));
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
}