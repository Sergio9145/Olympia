package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympia.MainActivity;
import com.olympia.R;

public class SplashActivity extends AppCompatActivity {

    //* Skip intro:
    final static boolean SKIP_INTRO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SKIP_INTRO) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_splash_screen);

            TextView textView = findViewById(R.id.splash_screen_text);
            ImageView imageView = findViewById(R.id.splash_screen_img);
            Animation splashAnimate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_transition);
            textView.startAnimation(splashAnimate);
            imageView.startAnimation(splashAnimate);

            Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            };

            handler.postDelayed(r, 3000);
        }
    }
}
