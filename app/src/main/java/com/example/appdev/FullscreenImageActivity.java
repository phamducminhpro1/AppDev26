package com.example.appdev;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

/*
Simple activity to show an image in fullscreen allowing the user to zoom and pan around.
This is used when users want to view an image which was sent in the chat.
Or when users want to have a better look at someone's profile picture.
 */
public class FullscreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        // Allow the user to close the activity by hitting the back button.
        Toolbar toolbar = findViewById(R.id.toolbarImage);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TouchImageView fullscreenImage = findViewById(R.id.fullscreenImage);

        // Load the image by retrieving the URL from the intent.
        String imageUrl = getIntent().getStringExtra("imageUrl");
        Glide.with(getApplicationContext()).load(imageUrl).into(fullscreenImage);
    }
}