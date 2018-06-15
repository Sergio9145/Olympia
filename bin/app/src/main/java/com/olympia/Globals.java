package com.olympia;

public class Globals {
    private Globals() {};
    public final static String TAG = "Olmp";

    public static final int MIN_USERNAME_LENGTH = 5, MIN_PASSWORD_LENGTH = 4;

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

    //* Params
    public final static String WORDS_LIST_EXTRA = "WORDS_LIST_EXTRA";
    public final static String WORD_CARD_EXTRA = "WORD_CARD_EXTRA";
    public final static String SETTINGS_EXTRA = "SETTINGS_EXTRA";

    //* Results
    public final static int LOGOUT_REQUESTED = 42;
    public final static int DELETE_ACCOUNT_REQUESTED = 76;
}
