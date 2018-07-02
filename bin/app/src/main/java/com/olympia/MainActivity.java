package com.olympia;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.olympia.activities.WordsListActivity;
import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.C9Token;
import com.olympia.cloud9_api.C9User;
import com.olympia.cloud9_api.ICloud9;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    //* Skip login:
    final static boolean QUICK_LAUNCH = false;

    final String OLYMPIA_PREFERENCES = "OLYMPIA_PREFERENCES",
        LOGIN_TOKEN = "LOGIN_TOKEN",
        USERNAME = "USERNAME";
    public static String currentUsername = "";

    private ICloud9 cloud9service;

    private View loginView, registrationView, resetPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);

        cloud9service = ApiUtils.getAPIService();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginView = findViewById(R.id.login_view);
        registrationView = findViewById(R.id.registration_view);
        resetPasswordView = findViewById(R.id.reset_password_view);

        String token = readToken();
        if ((token != null && !currentUsername.isEmpty()) || QUICK_LAUNCH) {
            Intent intent = new Intent(MainActivity.this, WordsListActivity.class);
            startActivityForResult(intent, Globals.WORDS_LIST_ACTIVITY);
        }
    }

    public void onLogin(View v)
    {
        if (loginInputIsValid()) {
            EditText username, password;

            username = findViewById(R.id.username1);
            password = findViewById(R.id.password1);

            C9User user = new C9User();

            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());

            sendLoginRequest(user);
        }
    }

    public void onRegistration(View v)
    {
        if (registrationInputIsValid()) {
            EditText firstName, lastName, username, email, password;

            firstName = findViewById(R.id.firstName2);
            lastName = findViewById(R.id.lastName2);
            email = findViewById(R.id.email2);
            username = findViewById(R.id.username2);
            password = findViewById(R.id.password2);

            C9User user = new C9User();

            user.setFirstName(firstName.getText().toString());
            user.setLastName(lastName.getText().toString());
            user.setEmail(email.getText().toString());
            user.setUsername(username.getText().toString());
            user.setPassword(password.getText().toString());

            sendRegisterRequest(user);
        }
    }

    public void onPasswordReset(View v)
    {
        if (resetPasswordInputIsValid()) {
            EditText email, password;

            email = findViewById(R.id.email3);
            password = findViewById(R.id.password3);

            C9User user = new C9User();

            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());

            sendResetPasswordRequest(user);
        }
    }

    public void gotoRegistration(View v)
    {
        loginView.setVisibility(View.GONE);
        resetPasswordView.setVisibility(View.GONE);
        registrationView.setVisibility(View.VISIBLE);
    }

    public void gotoLogin(View v)
    {
        registrationView.setVisibility(View.GONE);
        resetPasswordView.setVisibility(View.GONE);
        loginView.setVisibility(View.VISIBLE);
    }

    public void gotoResetPassword(View v)
    {
        loginView.setVisibility(View.GONE);
        registrationView.setVisibility(View.GONE);
        resetPasswordView.setVisibility(View.VISIBLE);
    }

    boolean loginInputIsValid() {
        boolean result = false;
        if (loginView.getVisibility() == View.VISIBLE) {
            EditText username, password;

            username = findViewById(R.id.username1);
            password = findViewById(R.id.password1);

            if (username.getText().length() >= Globals.MIN_USERNAME_LENGTH
                && password.getText().length() >= Globals.MIN_PASSWORD_LENGTH) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                    getResources().getString(R.string.login_field_invalid),
                        Globals.MIN_USERNAME_LENGTH, Globals.MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }

    boolean registrationInputIsValid() {
        boolean result = false;
        if (registrationView.getVisibility() == View.VISIBLE) {
            EditText firstName, lastName, username, email, password, repeatPassword;

            firstName = findViewById(R.id.firstName2);
            lastName = findViewById(R.id.lastName2);
            email = findViewById(R.id.email2);
            username = findViewById(R.id.username2);
            password = findViewById(R.id.password2);
            repeatPassword = findViewById(R.id.repeatPassword2);

            if (!TextUtils.isEmpty(firstName.getText())
                && !TextUtils.isEmpty(lastName.getText())
                && email.getText().toString().contains("@")
                && username.getText().length() >= Globals.MIN_USERNAME_LENGTH
                && password.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && repeatPassword.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(repeatPassword.getText().toString())) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                        getResources().getString(R.string.registration_field_invalid),
                        Globals.MIN_USERNAME_LENGTH, Globals.MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }

    boolean resetPasswordInputIsValid() {
        boolean result = false;
        if (resetPasswordView.getVisibility() == View.VISIBLE) {
            EditText email, password, repeatPassword;

            email = findViewById(R.id.email3);
            password = findViewById(R.id.password3);
            repeatPassword = findViewById(R.id.repeatPassword3);

            if (!TextUtils.isEmpty(email.getText())
                && email.getText().toString().contains("@")
                && password.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && repeatPassword.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(repeatPassword.getText().toString())) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                        getResources().getString(R.string.reset_password_field_invalid),
                        Globals.MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }

    public void sendRegisterRequest(C9User user) {
        cloud9service.registerUser(user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword())
            .enqueue(new Callback<C9Token>() {

            @Override
            public void onResponse(Call<C9Token> call, Response<C9Token> response) {
                if (response.isSuccessful()) {
                    saveToken(user.getUsername(), response.body().token);

                    Intent intent = new Intent(MainActivity.this, WordsListActivity.class);
                    startActivity(intent);
                } else {
                    try {
                        JSONObject error = new JSONObject(response.errorBody().string());
                        Log.e(Globals.TAG, error.getString("msg"));
                        Toast.makeText(getApplicationContext(), error.getString("msg"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        String s = getResources().getString(R.string.error_server_unreachable);
                        Log.e(Globals.TAG, s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<C9Token> call, Throwable t) {
                String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                        getResources().getString(R.string.register));
                Log.e(Globals.TAG, s);
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendLoginRequest(C9User user) {
        cloud9service.signUserIn(user.getUsername(),
                user.getPassword())
                .enqueue(new Callback<C9Token>() {

                    @Override
                    public void onResponse(Call<C9Token> call, Response<C9Token> response) {
                        if (response.isSuccessful()) {
                            saveToken(user.getUsername(), response.body().token);

                            Intent intent = new Intent(MainActivity.this, WordsListActivity.class);
                            startActivity(intent);
                        } else {
                            try {
                                JSONObject error = new JSONObject(response.errorBody().string());
                                Log.e(Globals.TAG, error.getString("msg"));
                                Toast.makeText(getApplicationContext(), error.getString("msg"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                String s = getResources().getString(R.string.error_server_unreachable);
                                Log.e(Globals.TAG, s);
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<C9Token> call, Throwable t) {
                        String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                getResources().getString(R.string.signin));
                        Log.e(Globals.TAG, s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void sendResetPasswordRequest(C9User user) {
        cloud9service.resetPassword(user.getEmail(),
                user.getPassword())
                .enqueue(new Callback<C9User>() {

                    @Override
                    public void onResponse(Call<C9User> call, Response<C9User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Check the mail for password reset link", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONObject error = new JSONObject(response.errorBody().string());
                                Log.e(Globals.TAG, error.getString("msg"));
                                Toast.makeText(getApplicationContext(), error.getString("msg"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                String s = getResources().getString(R.string.error_server_unreachable);
                                Log.e(Globals.TAG, s);
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<C9User> call, Throwable t) {
                        String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                getResources().getString(R.string.reset_password));
                        Log.e(Globals.TAG, s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        //* Suppressing navigating to splash screen!
    }

    private void saveToken(String username, String token) {
        currentUsername = username;
        SharedPreferences.Editor editor = getSharedPreferences(OLYMPIA_PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(USERNAME, username);
        editor.putString(LOGIN_TOKEN, token);
        editor.apply();
    }

    private String readToken () {
        SharedPreferences prefs = getSharedPreferences(OLYMPIA_PREFERENCES, MODE_PRIVATE);
        currentUsername = prefs.getString(USERNAME, "");
        return prefs.getString(LOGIN_TOKEN, null);
    }

    private void deleteToken() {
        currentUsername = "";
        SharedPreferences.Editor editor = getSharedPreferences(OLYMPIA_PREFERENCES, MODE_PRIVATE).edit();
        editor.remove(USERNAME);
        editor.remove(LOGIN_TOKEN);
        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Globals.WORDS_LIST_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Globals.WORDS_LIST_EXTRA, 0);
                switch (result) {
                    case Globals.LOGOUT_REQUESTED:
                    case Globals.DELETE_ACCOUNT_REQUESTED:
                        deleteToken();
                        gotoLogin(null);
                        break;
                    case Globals.CHANGE_THEME_REQUESTED:
                        recreate();
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
