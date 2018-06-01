package com.olympia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.ICloud9;
import com.olympia.cloud9_api.User;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    String TAG = "Olmp";

    private ICloud9 cloud9service;

    private View loginView, registrationView, resetPasswordView;
    final int MIN_USERNAME_LENGTH = 5,
        MIN_PASSWORD_LENGTH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginView = findViewById(R.id.login_view);
        registrationView = findViewById(R.id.registration_view);
        resetPasswordView = findViewById(R.id.reset_password_view);

        cloud9service = ApiUtils.getAPIService();
    }

    public void onLogin(View v)
    {
        if (loginInputIsValid()) {
            EditText username, password;

            username = findViewById(R.id.username1);
            password = findViewById(R.id.password1);

            User user = new User();

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

            User user = new User();

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

            User user = new User();

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

            if (username.getText().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                    "Username and password cannot be empty. Username must be unique and have at least %d characters. Password must be of at least %d characters",
                    MIN_USERNAME_LENGTH, MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
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
                && username.getText().length() >= MIN_USERNAME_LENGTH
                && password.getText().length() >= MIN_PASSWORD_LENGTH
                && repeatPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(repeatPassword.getText().toString())) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                    "None of the fields can be empty. Username must be unique and have at least %d characters. Email must contain @. Password must be of at least %d characters. Passwords must match",
                    MIN_USERNAME_LENGTH, MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
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
                && password.getText().length() >= MIN_PASSWORD_LENGTH
                && repeatPassword.getText().length() >= MIN_PASSWORD_LENGTH
                && password.getText().toString().equals(repeatPassword.getText().toString())) {
                result = true;
            } else {
                Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                        "Email must contain @. Password must be of at least %d characters. Passwords must match",
                        MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
            }
        }
        return result;
    }
    public void sendRegisterRequest(User user) {
        cloud9service.registerUser(user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword())
            .enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User successfully registered. Proceeding", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, WordsList.class);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "User was cannot be registered or other error");
                    Toast.makeText(getApplicationContext(), "User was cannot be registered or other error", Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Unable to submit Register request to the server");
                Toast.makeText(getApplicationContext(), "Unable to submit Register request to the server", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendLoginRequest(User user) {
        cloud9service.signUserIn(user.getUsername(),
                user.getPassword())
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User has successfully logged in. Proceeding", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(MainActivity.this, WordsList.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "Incorrect password or other error");
                            Toast.makeText(getApplicationContext(), "Incorrect password or other error", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Unable to submit Login request to the server");
                        Toast.makeText(getApplicationContext(), "Unable to submit Login request to the server", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void sendResetPasswordRequest(User user) {
        cloud9service.resetPassword(user.getEmail(),
                user.getPassword())
                .enqueue(new Callback<User>() {

                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Check the mail for password reset link", Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "Password was not reset or other error");
                            Toast.makeText(getApplicationContext(), "Password was not reset or other error", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Unable to submit ResetPassword request to the server");
                        Toast.makeText(getApplicationContext(), "Unable to submit ResetPassword request to the server", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
