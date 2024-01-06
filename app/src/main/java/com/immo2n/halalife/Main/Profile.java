package com.immo2n.halalife.Main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.immo2n.halalife.Main.Adapters.ProfileAdapter;
import com.immo2n.halalife.Main.DataObjects.ProfileGrids;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityProfileBinding;

import java.util.ArrayList;
import java.util.List;

public class Profile extends AppCompatActivity {
    ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Cover accent
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.profile_default_status_bar));

        //Grid list starts with info grid then posts
        List<ProfileGrids> gridsList = new ArrayList<>();
        gridsList.add(new ProfileGrids(null, null, true));
        //Add user posts or others
        //Demos
        gridsList.add(new ProfileGrids(null, null, false));

        //Init
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.mainView.setLayoutManager(layoutManager);
        binding.mainView.setAdapter(new ProfileAdapter(this, gridsList));
    }
}