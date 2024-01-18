package com.immo2n.halalife.Main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.immo2n.halalife.Core.CreatorService;
import com.immo2n.halalife.Custom.FileUtils;
import com.immo2n.halalife.Custom.FolderUtils;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.DataObjects.CreatorPayload;
import com.immo2n.halalife.DataObjects.MediaSelectionList;
import com.immo2n.halalife.R;
import com.immo2n.halalife.SubActivity.CropImage;
import com.immo2n.halalife.SubActivity.Media;
import com.immo2n.halalife.databinding.ActivityCreateBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Create extends AppCompatActivity {
    ActivityCreateBinding binding;
    Global global;

    //Behaviour flags
    boolean bigText = false;
    ImageView mediaImageCachedHolder, cachedCropIcon;
    TextView cachedVideoTag;
    RelativeLayout cachedHolderParent;
    int fileGridCount;
    private ActivityResultLauncher<Intent> cropLauncher, mediaSelectLauncher;

    //Main media factory
    private HashMap<String, File> finalMediaFactory = new HashMap<>();
    private HashMap<String, RelativeLayout> mediaViewFactory = new HashMap<>();

    //Mode
    int mode = 0; //0 = normal post, 1 = Reels

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);
        fileGridCount = 1;
        finalMediaFactory = new HashMap<>();
        mediaViewFactory = new HashMap<>();

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
        cachedVideoTag = binding.firstVideoTag;
        cachedCropIcon = binding.firstCropIcon;
        cachedHolderParent = binding.holderParent;

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

        mediaSelectLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> handleSelectedMedia(result.getResultCode(), result.getData())
        );

        cropLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            //Process the image file
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (null != data) {
                    String source = data.getStringExtra("source"), destination = data.getStringExtra("destination");
                    if (null != source && null != destination) {
                        //Replace the factory image with the new file with tag
                        if(finalMediaFactory.containsKey(source)){
                            finalMediaFactory.put(source, new File(destination));
                            RelativeLayout parent = mediaViewFactory.get(source);
                            if(null != parent){
                                ImageView image = parent.findViewById(R.id.imageLeft);
                                if(null == image){
                                    image = parent.findViewById(R.id.imageRight);
                                }
                                if(null == image){
                                    image = parent.findViewById(R.id.firstMediaImage);
                                }
                                if(null != image) {
                                    ImageView finalImage = image;
                                    global.runOnUI(() -> {
                                        Bitmap bitmap = BitmapFactory.decodeFile(destination);
                                        if (bitmap != null) {
                                            finalImage.setImageBitmap(bitmap);
                                            parent.setOnClickListener(view1 -> cropCall(source, new File(destination)));
                                        }
                                    });
                                }
                            }
                        }
                    }
                    else {
                        Toast.makeText(this, "Data lost!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.addMedia.setOnClickListener(view -> {
            //Go to the media selector to select files
            if(checkPermission()){
                //Go to the media activity
                mediaSelectLauncher.launch(new Intent(Create.this, Media.class));
            }
            else {
                Snackbar.make(binding.getRoot(), "Allow file management permission from settings!", Snackbar.LENGTH_SHORT).show();
            }
        });

        //Permission for all file access
        checkPermission();

        //Back press protection
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(global.getContext())
                        .setTitle("Warning")
                        .setMessage("Going back will delete your progress. Are you sure?")
                        .setPositiveButton("Yes, go back", (dialog, which) -> {
                            FolderUtils.clearCreatorCache();
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });
    }

    private void handleSelectedMedia(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    String resultData = data.getStringExtra(Media.RESULT_FILES);
                    MediaSelectionList listOBJ = global.getGson().fromJson(resultData, MediaSelectionList.class);
                    List<String> fileList = listOBJ.getFileList();
                    if(fileList.size() > 0){
                        //Process the files
                        for(String path:fileList){
                            File file = new File(path);
                            if(file.exists()) {
                                finalMediaFactory.put(path, file);
                                ImageView view = mediaImageCachedHolder;
                                if (fileGridCount%2 == 0) {
                                    //New grid
                                    @SuppressLint("InflateParams")
                                    View gridParent = getLayoutInflater().inflate(R.layout.create_media_item, null);
                                    ImageView leftImage = gridParent.findViewById(R.id.imageLeft);
                                    TextView videoTag = gridParent.findViewById(R.id.videoTagLeft);
                                    Glide.with(global.getContext())
                                            .load(path)
                                            .centerCrop()
                                            .placeholder(R.drawable.file_placeholder)
                                            .error(R.drawable.error)
                                            .into(leftImage);
                                    mediaImageCachedHolder = gridParent.findViewById(R.id.imageRight);
                                    cachedCropIcon = gridParent.findViewById(R.id.cropIconRight);
                                    cachedVideoTag = gridParent.findViewById(R.id.videoTagRight);
                                    cachedHolderParent = gridParent.findViewById(R.id.holderParentRight);

                                    if(FileUtils.isVideoFile(file)){
                                        videoTag.setText(FileUtils.getVideoDuration(file.getAbsolutePath()));
                                        videoTag.setVisibility(View.VISIBLE);
                                        //Videos do not need processing
                                    }
                                    else {
                                        gridParent.findViewById(R.id.cropIconLeft).setVisibility(View.VISIBLE);
                                        gridParent.findViewById(R.id.holderParentLeft).setOnClickListener(view1 -> cropCall(path, file));
                                    }

                                    binding.addedMediaList.addView(gridParent);
                                    mediaViewFactory.put(file.getAbsolutePath(), gridParent.findViewById(R.id.holderParentLeft));
                                }
                                else {
                                    if (null != view && null != cachedCropIcon && null != cachedVideoTag && null != cachedHolderParent) {
                                        mediaViewFactory.put(file.getAbsolutePath(), cachedHolderParent);
                                        Glide.with(global.getContext())
                                                .load(path)
                                                .centerCrop()
                                                .placeholder(R.drawable.file_placeholder)
                                                .error(R.drawable.error)
                                                .into(view);

                                        if(FileUtils.isVideoFile(file)){
                                            cachedVideoTag.setText(FileUtils.getVideoDuration(file.getAbsolutePath()));
                                            cachedVideoTag.setVisibility(View.VISIBLE);
                                            //Videos do not need processing
                                        }
                                        else {
                                            cachedCropIcon.setVisibility(View.VISIBLE);
                                            cachedHolderParent.setOnClickListener(view1 -> cropCall(path, file));
                                        }

                                    }
                                }
                                fileGridCount++;
                            }
                        }
                    }
                    else {
                        Snackbar.make(binding.getRoot(), "Nothing selected!", Snackbar.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Snackbar.make(binding.getRoot(), "Selection lost!", Snackbar.LENGTH_SHORT).show();
                }
            }
        } else {
            Snackbar.make(binding.getRoot(), "Selection canceled!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void cropCall(String path, File file) {
        File tempFile = new File(FolderUtils.getHALALiFECreatorCacheFolderInDCIM(), file.getName());
        try {
            cropLauncher.launch(new Intent(Create.this, CropImage.class)
                    .putExtra("source", path)
                    .putExtra("destination", tempFile.getAbsolutePath())
            );
        }
        catch (Exception e){
            Toast.makeText(this, "Could not copy the file!", Toast.LENGTH_SHORT).show();
        }
    }

    boolean publishEngaged = false;
    private void publish() {
        if(publishEngaged){
            Toast.makeText(this, "A moment please...", Toast.LENGTH_SHORT).show();
            return;
        }
        publishEngaged = true;
        //Create payload
        List<String> pathList = new ArrayList<>();
        List<File> files = new ArrayList<>(finalMediaFactory.values());
        for(File file:files){
            pathList.add(file.getAbsolutePath());
        }
        String body = binding.postText.getText().toString();

        //Empty check
        if(body.isEmpty() && pathList.size() == 0){
            new AlertDialog.Builder(global.getContext())
                    .setTitle("Empty post")
                    .setMessage("Your post is empty! You may continue crating the post.")
                    .setPositiveButton("Exit", (dialog, which) -> {
                        FolderUtils.clearCreatorCache();
                        finish();
                    })
                    .setNegativeButton("Continue creating", (dialog, which) -> dialog.dismiss())
                    .show();
            publishEngaged = false;
            return;
        }
        String payload = global.getGson().toJson(new CreatorPayload(
                mode,
                binding.privacy.getSelectedItemPosition(),
                pathList,
                body
        ));
        Toast.makeText(this, "Creating post...", Toast.LENGTH_LONG).show();
        startService(new Intent(this, CreatorService.class).putExtra(CreatorService.PAYLOAD_TAG, payload));
        finish();
    }

    private boolean checkPermission(){
        boolean r = this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) < 0
                || this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) < 0;
        if(r){
            Toast.makeText(this, "Need file management permission!", Toast.LENGTH_SHORT).show();
            //Need permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
            ActivityCompat.requestPermissions(global.getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, 2);
        }
        return !r;
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
}