package com.immo2n.halalife.Main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityCreateBinding;

import java.util.Objects;

public class Create extends AppCompatActivity {
    ActivityCreateBinding binding;
    Global global;

    //Behaviour flags
    boolean bigText = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);






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
    private void publish() {
        Toast.makeText(this, "Publish!", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        goBack();
    }
    private void goBack(){
        finish();
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
    }
}