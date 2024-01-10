package com.immo2n.halalife.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    Global global;
    AppState appState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);
        appState = new AppState(global);








        //Hooks
        binding.profileButton.setOnClickListener(view -> startActivity(new Intent(Home.this, Profile.class)));
    }
}