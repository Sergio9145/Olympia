package com.olympia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

    private View loginView;
    private View registrationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loginView = findViewById(R.id.login_view);
        registrationView = findViewById(R.id.registration_view);

        cloud9service = ApiUtils.getAPIService();
    }

    private void switchViews(View v1, View v2) {
        v1.setVisibility(View.GONE);
        v2.setVisibility(View.VISIBLE);
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

    public void gotoRegistration(View v)
    {
        switchViews(loginView, registrationView);
    }

    public void gotoLogin(View v)
    {
        switchViews(registrationView, loginView);
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
                    showResponse(response.body().toString());
                    Log.i(TAG, "Submitted to API" + response.body().toString());
                } else {
                    showResponse(response.body().toString());
                    Log.i(TAG, "Was not submitted to API" + response.body().toString());
                }
                Toast.makeText(getApplicationContext(), "User successfully registered. Proceeding", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this, WordsList.class);
//                    intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "Unable to submit post to API");
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
                            showResponse(response.body().toString());
                            Log.i(TAG, "Submitted to API" + response.body().toString());
                        } else {
                            showResponse(response.body().toString());
                            Log.i(TAG, "Was not submitted to API" + response.body().toString());
                        }
                        Toast.makeText(getApplicationContext(), "User has successfully logged in. Proceeding", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(MainActivity.this, WordsList.class);
//                    intent.putExtra(EXTRA_MESSAGE, message);
                        startActivity(intent);
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Unable to submit post to API");
                    }
                });
    }

    public void showResponse(String response) {
        TextView tv = findViewById(R.id.log);
        if(tv.getVisibility() == View.GONE) {
            tv.setVisibility(View.VISIBLE);
        }
        tv.setText(response);
    }
}
