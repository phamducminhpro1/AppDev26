package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        Toolbar toolbar = findViewById(R.id.toolbarImage);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TouchImageView fullscreenImage = findViewById(R.id.fullscreenImage);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        Glide.with(getApplicationContext()).load(imageUrl).into(fullscreenImage);
    }
}