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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    String TAG = "Olmp";

    private ICloud9 cloud9service;

    private View loginView, registrationView, resetPasswordView;

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
        EditText username, password;

        username = findViewById(R.id.username1);
        password = findViewById(R.id.password1);

        User user = new User();

        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        if (!TextUtils.isEmpty(username.getText())
            &&!TextUtils.isEmpty(password.getText()))
        {
            sendLoginRequest(user);
        }
    }

    public void onRegistration(View v)
    {
        EditText firstName, lastName, username, email, password;

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username2);
        password = findViewById(R.id.password2);

        User user = new User();

        user.setFirstName(firstName.getText().toString());
        user.setLastName(lastName.getText().toString());
        user.setEmail(email.getText().toString());
        user.setUsername(username.getText().toString());
        user.setPassword(password.getText().toString());

        if (!TextUtils.isEmpty(firstName.getText())
            &&!TextUtils.isEmpty(lastName.getText())
            &&!TextUtils.isEmpty(email.getText())
            &&!TextUtils.isEmpty(username.getText())
            &&!TextUtils.isEmpty(password.getText()))
        {
            sendRegisterRequest(user);
        }
    }

    public void onPasswordReset(View v)
    {
        EditText email, password;

        email = findViewById(R.id.email3);
        password = findViewById(R.id.password3);

        User user = new User();

        user.setEmail(email.getText().toString());
        user.setPassword(password.getText().toString());

        if (!TextUtils.isEmpty(email.getText())
                &&!TextUtils.isEmpty(password.getText()))
        {
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
