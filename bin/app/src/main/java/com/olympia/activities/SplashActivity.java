package com.olympia.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;

public class SplashActivity extends AppCompatActivity {

    //* Skip intro:
    final static boolean SKIP_INTRO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        TextView textView = findViewById(R.id.splash_screen_text);
        ImageView imageView = findViewById(R.id.splash_screen_img);
        Animation splashAnimate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_transition);
        textView.startAnimation(splashAnimate);
        imageView.startAnimation(splashAnimate);

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (isConnected()) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    openSettings();
                }
            }
        };

        if (SKIP_INTRO) {
            handler.post(r);
        } else {
            handler.postDelayed(r, 3000);
        }
    }

    public void openSettings() {
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_check_internet_connection, null);
        categoryBuilder.setView(mView);

        AlertDialog dialog = categoryBuilder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        Button positiveBtn = mView.findViewById(R.id.button_positive);
        Button negativeBtn = mView.findViewById(R.id.button_negative);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                startActivityForResult(intent, 0);
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public boolean isConnected() {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                android.net.NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                result = ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()));
            } else {
                result = false;
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isConnected()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
