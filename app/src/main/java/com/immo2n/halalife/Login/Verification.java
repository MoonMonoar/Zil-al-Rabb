package com.immo2n.halalife.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.DBhandler;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.Net;
import com.immo2n.halalife.DataObjects.CommonResponse;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityVerificationBinding;

public class Verification extends AppCompatActivity {
    private ActivityVerificationBinding binding;
    private Global global;
    private AppState appState;
    private DBhandler dBhandler;
    private Server server;
    private String opt_target;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.signup_page_status_bar));
        global = new Global(this, this);
        appState = new AppState(global);
        server = new Server(global);
        dBhandler = new DBhandler(this);

        opt_target = dBhandler.getSettings("email_otp");
        if(null == opt_target) {
            String otp = global.generateOTP(6);
            dBhandler.addSetting("email_otp", otp);
            sendPin(otp);
        }
        else {
            viewGetPin();
            global.toast("Enter: "+opt_target);
        }

        binding.pinText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //No need
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String otp = charSequence.toString();
                if(otp.length() < 6) return;
                if(otp.equals(opt_target)){
                    new Net(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(@NonNull Message msg) {
                            super.handleMessage(msg);
                            try {
                                CommonResponse response = global.getGson().fromJson(msg.obj.toString(), CommonResponse.class);
                                if(response.isStatusOk()){
                                    //Sync profile auto
                                    appState.syncProfileAuto();
                                    if(appState.needProfilePicUpdate()){
                                        startActivity(new Intent(Verification.this, ProfilePicture.class));
                                        finish();
                                    }
                                    else {
                                        global.toast("Go home");
                                    }
                                }
                                else {
                                    global.toast("Try again!");
                                }
                            }
                            catch (Exception e){
                                global.toast("Server error!");
                            }
                        }
                    }, global, false).post(Server.routeUpdateProfile,
                                "token="+global.makeUrlSafe(appState.getToken())
                            +"&fields=[\"email_verified\"]&values=[\"Yes\"]"
                            , 2);
                }
                else {
                    global.toast(global.getActivity().getString(R.string.invalid_pin));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //No need
            }
        });

    }
    private void sendPin(String otp){
        //Temporary
        global.toast("Sent: "+otp);
        opt_target = otp;
        //View change
        viewGetPin();
    }
    private void viewGetPin(){
        binding.sending.setVisibility(View.GONE);
        binding.message.setVisibility(View.VISIBLE);
        binding.changeEmail.setVisibility(View.VISIBLE);
        binding.pinText.setVisibility(View.VISIBLE);
    }
    private void viewSendPin(){
        binding.message.setVisibility(View.GONE);
        binding.changeEmail.setVisibility(View.GONE);
        binding.pinText.setVisibility(View.GONE);
        binding.sending.setVisibility(View.VISIBLE);
    }
}