package com.immo2n.halalife.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {
    ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());









        //Hooks
        binding.profileButton.setOnClickListener(view -> startActivity(new Intent(Home.this, Profile.class)));
    }
}