package com.immo2n.halalife.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.immo2n.halalife.Custom.Global;
import com.immo2n.halalife.R;
import com.immo2n.halalife.databinding.ActivityCreateBinding;

import java.util.Objects;

public class Create extends AppCompatActivity {
    ActivityCreateBinding binding;
    Global global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        global = new Global(this, this);








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