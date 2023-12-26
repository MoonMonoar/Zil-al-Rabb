package com.immo2n.halalife.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import com.immo2n.halalife.Core.AppState;
import com.immo2n.halalife.Core.Profile;
import com.immo2n.halalife.Core.Server;
import com.immo2n.halalife.Custom.DBhandler;
import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.Custom.Net;
import com.immo2n.halalife.Custom.Profanity;
import com.immo2n.halalife.DataObjects.RegisterOBJ;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityRegistrationBinding;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private ActivityRegistrationBinding binding;
    private Global global;
    private AppState appState;
    private Server server;
    private DBhandler dBhandler;
    private String profanity_Allow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.signup_page_status_bar));
        global = new Global(this, this);
        appState = new AppState(global);
        server = new Server(global);
        dBhandler = new DBhandler(this);
        //Main
        binding.createProceed.setOnClickListener(view -> {
            release_all();
            String fullName = binding.fullName.getText().toString();
            if(fullName.length() == 0  || !isValidName(fullName)){
                if(fullName.length() == 0){
                    global.toast(global.getActivity().getString(R.string.invalid_name));
                }
                return;
            }
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if(email.length() == 0  || !isValidEmail(email)){
                global.toast(global.getActivity().getString(R.string.invalid_email));
                return;
            }
            if(password.length() == 0 || isWeakPassword(password)){
                global.toast(global.getActivity().getString(R.string.invalid_password));
                return;
            }
            String phone = binding.phoneNumber.getText().toString();
            if(phone.length() == 0){
                global.toast(global.getActivity().getString(R.string.invalid_phone));
                return;
            }
            //All okay send it!
            binding.createProceed.setText(R.string.creating_account);
            //binding.createProceed.setEnabled(false);
            new Net(new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    try {
                        RegisterOBJ obj = global.getGson().fromJson(msg.obj.toString(), RegisterOBJ.class);
                        if(obj.isStatusTrue()){
                            dBhandler.addSetting(DBhandler.userTokenEntryName, obj.getToken());
                            dBhandler.addSetting(DBhandler.userLoggedInEntryName, DBhandler.signalTrue);
                            dBhandler.addSetting(DBhandler.userProfileEntryName, global.getGson().toJson(obj.getProfile()));
                            //Done!
                            if(appState.getProfile().getEmail_verified().equals("Yes")){
                                //Go to home
                                global.toast("Home!");
                            }
                            else {
                                //Go verify
                                startActivity(new Intent(Registration.this, Verification.class));
                            }
                        }
                        else {
                            binding.createProceed.setText(R.string.create_account);
                            binding.createProceed.setEnabled(true);
                            runOnUiThread(() -> global.toast(global.getActivity().getString(R.string.try_again)));
                        }
                    }
                    catch (Exception e){
                        runOnUiThread(() -> {
                            Log.d(Global.LOG_TAG, e.toString());
                            binding.createProceed.setText(R.string.create_account);
                            binding.createProceed.setEnabled(true);
                            global.toast(global.getActivity().getString(R.string.try_again));
                        });
                    }
                }
            }, global, false).post(Server.routeSignup, "name="
                    +global.makeUrlSafe(fullName)+
                    "&email="
                    +global.makeUrlSafe(email)+
                    "&phone="
                    +global.makeUrlSafe(phone)+
                    "&gender="
                    +global.makeUrlSafe(binding.gender.getSelectedItem().toString())+
                    "&pass="
                    +global.makeUrlSafe(password), 1);
        });
        //Main - ends
        //Page components
        String[] options = getResources().getStringArray(R.array.genders);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.gender.setAdapter(adapter);
        binding.login.setOnClickListener(v-> finish());
        //Behaviour
        binding.mainBody.setOnClickListener(view -> release_all());
    }
    private void release_all(){
        binding.fullName.clearFocus();
        binding.email.clearFocus();
        binding.phoneNumber.clearFocus();
        binding.password.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.fullName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.email.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.phoneNumber.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.password.getWindowToken(), 0);
    }
    public static boolean isWeakPassword(String password) {
        if (password.length() < 6) {
            return true;
        }
        String[] weakPatterns = {
                "password", "123456", "qwerty", "abc123", "letmein", "admin",
                "welcome", "123abc", "iloveyou", "sunshine", "princess",
                "12345", "football", "monkey", "qwertyuiop", "solo",
                "password1", "passw0rd", "123456789", "superman", "batman",
                "baseball", "iloveyou1", "111111", "aaaaaa", "999999",
                "password123", "starwars", "pokemon", "flower", "hello",
                "welcome1", "abcdef", "123123", "qwerty123", "shadow",
                "test123", "letmein1", "hello123", "123qwe", "000000",
                "password!", "1234", "dragon", "555555", "1q2w3e4r",
                "admin123", "abc1234", "123321", "trustno1", "pass123",
                "password1234", "monkey1", "123qwe", "1111", "qazwsx",
                "password12", "123456a", "password!", "qwerty123!",
                "baseball1", "welcome123", "pass1234", "1234567890",
                "iloveyou2", "password12!", "1234567", "asdfgh", "welcome1!",
                "letmein123", "iloveyou!", "qwertyuiop1", "12345678", "sunshine1",
                "123abc!", "abcdefg", "letmein12", "123456a!", "password!",
                "iloveyou12", "qazwsx1", "monkey12", "1q2w3e4r!", "baseball12",
                "welcome123!", "12345!", "password1!", "qwertyuio", "123qwe!",
                "passw0rd1", "superman1", "qazwsx!", "iloveyou12!", "baseball123",
                "password123!", "letmein12!", "12345a", "123123!", "password12!",
                "monkey123", "12345q", "admin1", "1234qwer", "abcd1234",
                "123abcde", "1111111", "passw0rd!", "123456789a", "1234567a",
                "password!", "1234abcd", "password!1", "12345678910", "welcome!1",
                "abcd1234!", "qazwsx!1", "sunshine!1", "iloveyou!1", "admin!1"
        };
        for (String weakPattern : weakPatterns) {
            if(password.equalsIgnoreCase(weakPattern)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isValidEmail(String email) {
        // Regular expression for email validation
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean isValidName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        if (fullName.length() > 50) {
            return false;
        }
        if(Profanity.isAbusive(fullName)){
            if(null == profanity_Allow || !profanity_Allow.equals(fullName)) {
                Dialog dialog = global.makeDialogue(R.layout.dialogue_prafanity_warning, R.drawable.dialogue_back);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.show();
                Objects.requireNonNull(dialog.getWindow()).findViewById(R.id.close).setOnClickListener(v -> dialog.dismiss());
                profanity_Allow = fullName;
                return false;
            }
        }
        Pattern pattern = Pattern.compile("^[\\p{L}\\p{Z}.'\\-\\ud83c[\\ud000-\\udfff]\\ud83d[\\ud000-\\udfff]\\ud83e[\\ud000-\\udfff]]+$");
        Matcher matcher = pattern.matcher(fullName);
        if (matcher.find()) {
            // No emoji
            return true;
        } else {
            // Has emoji - check if there's at least one non-emoji character
            String textWithoutEmojis = fullName.replaceAll("\\p{So}", "");
            return !textWithoutEmojis.trim().isEmpty();
        }
    }
}