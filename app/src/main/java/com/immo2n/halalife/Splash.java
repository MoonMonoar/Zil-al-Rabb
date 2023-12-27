package com.immo2n.halalife;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Login.Login;
import com.immo2n.halalife.Login.ProfilePicture;
import com.immo2n.halalife.Login.Registration;
import com.immo2n.halalife.Login.Verification;

public class Splash extends AppCompatActivity {
    private Global global;
    private AppState appState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        global = new Global(this, this);
        appState = new AppState(global);
        new Handler(Looper.getMainLooper()).postDelayed(this::checkRoute, 1000);
    }

    private void checkRoute() {
        //Goto login
        if(appState.isUserLoggedIn()){
            //Go home or Verify
            if(appState.getProfile().getEmail_verified().equals("Yes")){
                //Go to home
                if(appState.needProfilePicUpdate()){
                    startActivity(new Intent(this, ProfilePicture.class));
                }
                else {
                    global.toast("Home!");
                }
            }
            else {
                //Go verify
                startActivity(new Intent(this, Verification.class));
            }
        }
        else {
            //Go to login
            startActivity(new Intent(this, Login.class));
        }
        finish();
    }
}