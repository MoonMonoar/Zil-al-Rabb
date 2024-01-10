package com.immo2n.halalife.Main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.DataObjects.PostsObject;
import com.immo2n.halalife.Main.Adapters.ProfileAdapter;
import com.immo2n.halalife.Main.DataObjects.ProfileGrid;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding binding;
    Global global;
    AppState appState;
    Server server;
    ProfileAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        global = new Global(this, this);
        appState = new AppState(global);
        server = new Server(global);
        setContentView(binding.getRoot());
        //Sync
        appState.syncProfileAuto();

        //Cover accent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.profile_default_status_bar));

        //Grid list starts with info grid then posts
        List<ProfileGrid> gridsList = new ArrayList<>();

        //Main profile info grid
        gridsList.add(new ProfileGrid(appState, true, null));

        //Add user posts offset starting from 0, expect 15 each call
        server.getPosts(appState.getToken(), 0, new Server.PostsCallBack() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<PostsObject> list) {
                for(PostsObject object : list){
                    gridsList.add(new ProfileGrid(appState, false, object));
                }
                //Notify
                if(null != adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFail(String message) {
                //Posts load failed!
            }
        });

        //Init
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.mainView.setLayoutManager(layoutManager);
        //Pass own profile - for the user, pass the other profile
        binding.mainView.setAdapter(adapter = new ProfileAdapter(this, this, gridsList, appState.getProfile()));
    }
}