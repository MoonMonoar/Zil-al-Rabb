package com.immo2n.halalife.SubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityMediaBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Media extends AppCompatActivity {
    ActivityMediaBinding binding;
    public static List<File> photosList = new ArrayList<>(),  videosList = new ArrayList<>(), allFiles = new ArrayList<>();
    private static List<String> allFolderNames = new ArrayList<>();
    private static Map<String, List<File>> allFoldersMap = new HashMap<>();
    boolean spinnerLock = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMediaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Get folders for spinner
        photosList = com.immo2n.halalife.Custom.Media.getAllImages(this);
        videosList = com.immo2n.halalife.Custom.Media.getAllVideos(this);

        //Reset everything
        allFolderNames = new ArrayList<>();
        allFoldersMap = new HashMap<>();

        //Put files
        allFolderNames.add("All files");
        for(File file: photosList){
            allFiles.add(file);
            File parentFolder = file.getParentFile();
            if (parentFolder != null) {
                String folderName = parentFolder.getName();
                if (!allFolderNames.contains(folderName)) {
                    allFolderNames.add(folderName);
                }
                List<File> filesInFolder = allFoldersMap.getOrDefault(folderName, new ArrayList<>());
                if(null != filesInFolder) {
                    filesInFolder.add(file);
                    allFoldersMap.put(folderName, filesInFolder);
                }
            }
        }
        for(File file: videosList){
            allFiles.add(file);
            File parentFolder = file.getParentFile();
            if (parentFolder != null) {
                String folderName = parentFolder.getName();
                if (!allFolderNames.contains(folderName)) {
                    allFolderNames.add(folderName);
                }
                List<File> filesInFolder = allFoldersMap.getOrDefault(folderName, new ArrayList<>());
                if(null != filesInFolder) {
                    filesInFolder.add(file);
                    allFoldersMap.put(folderName, filesInFolder);
                }
            }
        }
        allFoldersMap.put("All files", allFiles);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allFolderNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.folderSpinner.setAdapter(adapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        binding.fileList.setLayoutManager(gridLayoutManager);
        binding.fileList.setItemAnimator(null);

        //Next time just replace allFiles with wanted file list and than just notify changes
        MediaAdapter mediaAdapter = new MediaAdapter(allFiles, this);
        binding.fileList.setAdapter(mediaAdapter);

        binding.folderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerLock){
                    spinnerLock = false;
                    return;
                }
                allFiles = allFoldersMap.get(allFolderNames.get(i));
                MediaAdapter mediaAdapter = new MediaAdapter(allFiles, Media.this);
                binding.fileList.setAdapter(mediaAdapter);
                mediaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do none
            }
        });

    }
}