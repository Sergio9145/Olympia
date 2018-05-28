package com.olympia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private EditText username_input, password_input;
    private ICloud9 cloud9service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username_input = (EditText) findViewById(R.id.username);
        password_input = (EditText) findViewById(R.id.password);
    }

    public void onClickBtn(View v)
    {
        cloud9service = ApiUtils.getAPIService();

        User user = new User();
        user.setUsername(username_input.getText().toString());
        user.setPassword(password_input.getText().toString());
        sendPostRequest(user);
    }

    public void sendPostRequest(User user) {
        cloud9service.createUser(user.getUsername(), user.getPassword(), 42).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    showResponse(response.body().toString());
                    Log.i(TAG, "Submitted to API" + response.body().toString());
                } else {
                    showResponse(response.body().toString());
                    Log.i(TAG, "Was not submitted to API" + response.body().toString());
                }
                Toast.makeText(getApplicationContext(), "Successfully logged in. Proceeding", Toast.LENGTH_LONG).show();

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
