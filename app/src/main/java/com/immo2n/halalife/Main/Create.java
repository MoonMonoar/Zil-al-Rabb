package com.immo2n.halalife.Main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.immo2n.halalife.Custom.FolderUtils;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.ImageUtils;
import com.immo2n.halalife.R;
import com.immo2n.halalife.SubActivity.CropImage;
import com.immo2n.halalife.databinding.ActivityCreateBinding;

import java.io.File;
import java.util.Objects;

public class Create extends AppCompatActivity {
    ActivityCreateBinding binding;
    Global global;

    //Behaviour flags
    boolean bigText = false;
    int postMode = 0; //0 = post, 1 = reels
    ImageView mediaImageCachedHolder;
    int mediaHolderCount = 1;
    private ActivityResultLauncher<Intent> galleryLauncher, cropLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);







        //Page components
        ArrayAdapter<CharSequence> privacyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.privacy_options,
                R.layout.create_spinner
        );
        privacyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.privacy.setAdapter(privacyAdapter);
        binding.privacy.setSelection(0);
        mediaImageCachedHolder = binding.firstMediaImage;

        //Post text controls
        binding.postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do none
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Text size
                if(binding.postText.getText().toString().length() > 30){
                    global.runOnUI(() -> binding.postText.setTextSize(17));
                    bigText = false;
                }
                else {
                    if(!bigText){
                        global.runOnUI(() -> binding.postText.setTextSize(26));
                        bigText = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Do none
            }
        });

        //Toolbar
        setSupportActionBar(binding.toolbar);
        int newColor = ContextCompat.getColor(this, R.color.black);
        Objects.requireNonNull(getSupportActionBar()).setTitle(HtmlCompat.fromHtml("<font color='" + newColor + "'>" + "Create post" + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(global.getDrawable(R.drawable.back_arrow));

        //Page behavior
        binding.container.setOnClickListener(view -> release_all());
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        Toast.makeText(global.getContext(), uri.getPath(), Toast.LENGTH_SHORT).show();


                        /*
                        tempFilePostTarget = new File(FolderUtils.getCreateCacheFolder(global.getContext()), FolderUtils.getImageFileName());
                        if(isVideoUri(result.getData().getData())){
                            tempFilePostTarget = new File(FolderUtils.getCreateCacheFolder(global.getContext()), FolderUtils.getVideoFileName());
                            //Video file so, do whats needed - save the file first! from data data


                        }
                        else {
                            //Image file - so after copying, crop it
                            new Thread(() -> {
                                try {
                                    ImageUtils.saveImageUriToFile(global.getContext(), result.getData().getData(), tempFilePostTarget);
                                    runOnUiThread(() -> cropLauncher.launch(new Intent(Create.this, CropImage.class).putExtra("path", tempFilePostTarget.getAbsolutePath())));
                                }
                                catch (Exception e){
                                    global.toast("Could not copy file!");
                                }
                            }).start();
                        }
                         */
                    }
                });
        cropLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //Process the image file
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (null != data) {
                    if (null != data.getStringExtra("path")) {
                        //processMediaPosts(tempFilePostTarget);
                    }
                }
            }
        });
        binding.addMedia.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/* video/*");
            galleryLauncher.launch(intent);
        });
    }

    private void publish() {

    }

    private void processMediaPosts(File tempFile) {
        if(mediaHolderCount%2 == 0){
            //New insert
            @SuppressLint("InflateParams")
            View holder = getLayoutInflater().inflate(R.layout.create_media_item, null);
            ImageView leftImage = holder.findViewById(R.id.imageLeft);
            leftImage.setImageURI(Uri.fromFile(tempFile));
            mediaImageCachedHolder = holder.findViewById(R.id.imageRight);
            binding.addedMediaList.addView(holder);
        }
        else {
            if(null != mediaImageCachedHolder){
                mediaImageCachedHolder.setImageURI(Uri.fromFile(tempFile));
            }
        }
        mediaHolderCount++;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_page_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            goBack();
            return true;
        } else if (itemId == R.id.publishPost) {
            publish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
    private void goBack(){
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }
    private void release_all(){
        binding.postText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.postText.getWindowToken(), 0);
    }
    private boolean isVideoUri(Uri contentUri) {
        String type = getContentResolver().getType(contentUri);
        return type != null && type.startsWith("video/");
    }
}