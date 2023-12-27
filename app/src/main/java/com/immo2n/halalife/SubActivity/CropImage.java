package com.immo2n.halalife.SubActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityCropImageBinding;
import com.takusemba.cropme.OnCropListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class CropImage extends AppCompatActivity {
    private ActivityCropImageBinding binding;
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

        if(null != b && null != b.getString("path")) {
            File file = new File(Objects.requireNonNull(b.getString("path")));
            binding.image.setUri(Uri.fromFile(file));
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
                        FileOutputStream outputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        goBack(file);
                    }
                    catch (Exception e){
                        //Do nothing
                        goBack(file);
                    }
                }
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Do nothing
                    goBack(file);
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
        resultIntent.putExtra("path", file.getAbsolutePath());
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}