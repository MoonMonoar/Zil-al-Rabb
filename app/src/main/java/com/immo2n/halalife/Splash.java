package com.immo2n.halalife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.immo2n.halalife.Login.Login;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(this::checkRoute, 1000);
    }

    private void checkRoute() {
        //Goto login
        startActivity(new Intent(this, Login.class));
        finish();
    }
}