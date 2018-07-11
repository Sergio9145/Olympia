package com.olympia;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;

import com.olympia.oxford_api.ApiClient;

import java.util.ArrayList;

public class Globals {
    private Globals() {};

    public final static ApiClient apiClient = new ApiClient();

    public final static ArrayList<Category> filteredCategories = new ArrayList<>();
    public static int currentSorting = 0;
    public static int currentTheme = 0;

    public final static String TAG = "Olmp";

    public final static int MIN_USERNAME_LENGTH = 5, MIN_PASSWORD_LENGTH = 4;

    public static TextToSpeech tts = null;
    public static boolean tts_enabled = false, is_speaking = false;

    //* Intents
    public final static int WORDS_LIST_ACTIVITY = 50;
    public final static int WORD_CARD_ACTIVITY = 100;
    public final static int SETTINGS_ACTIVITY = 200;
    public final static int ABOUT_ACTIVITY = 300;
    public final static int lEGAL_ACTIVITY = 400;
    public final static int CHANGE_NAME_ACTIVITY = 500;
    public final static int CHANGE_EMAIL_ACTIVITY = 600;
    public final static int CHANGE_PASSWORD_ACTIVITY = 700;
    public final static int QUIZ_ACTIVITY = 800;
    public final static int CAMERA_ACTIVITY = 900;
    public final static int SPEECH_ACTIVITY = 1000;

    //* Params
    public final static String WORDS_LIST_EXTRA = "WORDS_LIST_EXTRA";
    public final static String SETTINGS_EXTRA = "SETTINGS_EXTRA";

    //* Results
    public final static int LOGOUT_REQUESTED = 42;
    public final static int DELETE_ACCOUNT_REQUESTED = 76;
    public final static int CHANGE_UI_LANGUAGE_REQUESTED = 83;
    public final static int CHANGE_THEME_REQUESTED = 101;

    public static void loadTheme(AppCompatActivity a) {
        switch (Globals.currentTheme){
            case 0:
                a.setTheme(R.style.Theme1);
                break;
            case 1:
                a.setTheme(R.style.Theme2);
                break;
            case 2:
                a.setTheme(R.style.Theme3);
                break;
            case 3:
                a.setTheme(R.style.Theme4);
                break;
        }
    }
}
