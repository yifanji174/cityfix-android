package com.g04.cityfix.ui.common;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

/**
 * The page where user can view full images
 * @author Jerry Yang
 */
public class FullscreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageView imageView = new ImageView(this);
        setContentView(imageView);

        String imageUrl = getIntent().getStringExtra("image_url");

        Glide.with(this)
                .load(imageUrl)
                .into(imageView);

        imageView.setOnClickListener(v -> finish());
    }
}
