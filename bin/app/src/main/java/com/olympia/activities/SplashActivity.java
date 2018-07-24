package com.olympia.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    //* Skip intro:
    final static boolean SKIP_INTRO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        Globals.tts = new TextToSpeech(this, this);

        TextView textView = findViewById(R.id.splash_screen_text);
        ImageView imageView = findViewById(R.id.splash_screen_img);
        Animation splashAnimate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_screen_transition);
        textView.startAnimation(splashAnimate);
        imageView.startAnimation(splashAnimate);

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                if (isConnected(SplashActivity.this)) {
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

    @Override
    public void onInit(int status) {
        Locale language = Locale.ENGLISH;
        if (language == null) {
            Globals.tts_enabled = false;
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.tts_no_lang), Toast.LENGTH_SHORT).show();
            return;
        }
        int result = Globals.tts.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Globals.tts_enabled = false;
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.tts_missing_data), Toast.LENGTH_SHORT).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Globals.tts_enabled = false;
            Toast.makeText(SplashActivity.this, getResources().getString(R.string.tts_not_supported), Toast.LENGTH_SHORT).show();
            return;
        } else {
            Globals.tts_enabled = true;
            Locale currentLanguage = Globals.tts.getVoice().getLocale();
            String s = String.format(Locale.ENGLISH, getResources().getString(R.string.tts_set_to), currentLanguage);
            Toast.makeText(SplashActivity.this, s, Toast.LENGTH_SHORT).show();
            Globals.tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    Globals.is_speaking = false;
                }

                @Override
                public void onError(String utteranceId) { }

                @Override
                public void onStart(String utteranceId) {
                    Globals.is_speaking = true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isConnected(SplashActivity.this)) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void openSettings() {
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_check_internet_connection, null);
        categoryBuilder.setView(mView);

        AlertDialog dialog = categoryBuilder.create();
        dialog.setCancelable(false);
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

    public static boolean isConnected(Context c) {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

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
}
