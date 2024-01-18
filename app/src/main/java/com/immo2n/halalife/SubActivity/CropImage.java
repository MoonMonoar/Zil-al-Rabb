package com.immo2n.halalife.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.immo2n.halalife.Custom.FileUtils;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityCropImageBinding;
import com.takusemba.cropme.OnCropListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class CropImage extends AppCompatActivity {
    private ActivityCropImageBinding binding;
    String source = null, destination = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCropImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(v-> finish());

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_overlay));

        Bundle b = getIntent().getExtras();

        Global global = new Global(this, this);

        if(null != b && null != b.getString("source") && null != b.getString("destination")) {
            source = b.getString("source");
            destination = b.getString("destination");
            File file = new File(source), destinationFile = new File(destination);
            if(file.length() > 5000000){ //5 megabyte sample bottleneck
                binding.image.setBitmap(FileUtils.decodeSampledBitmapFromFile(source, (int) global.getDisplayWidth(), (int) global.getDisplayHeight()));
            }
            else {
                binding.image.setUri(Uri.fromFile(file));
            }
            binding.CropMain.setOnClickListener(v-> {
                if(binding.image.isOffFrame()){
                    global.toast("Zoom to fit in!");
                    return;
                }
                binding.image.crop();
                binding.CropMainDone.setVisibility(View.GONE);
                binding.CropMainProgress.setVisibility(View.VISIBLE);
            });
            binding.image.addOnCropListener(new OnCropListener() {
                @Override
                public void onSuccess(@NonNull Bitmap bitmap) {
                    try {
                        FileOutputStream outputStream = new FileOutputStream(destinationFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        goBack(destinationFile);
                    }
                    catch (Exception e){
                        goBack(file);
                        global.toast("Failed to crop!");
                    }
                }
                @Override
                public void onFailure(@NonNull Exception e) {
                    goBack(file);
                    global.toast("Failed to crop!");
                }
            });
        }
        else {
            global.toast("File lost!");
            finish();
        }

    }
    private void goBack(File file){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("source", source);
        resultIntent.putExtra("destination", file.getAbsolutePath());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}