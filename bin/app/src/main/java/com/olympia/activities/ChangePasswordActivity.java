package com.olympia.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;
import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.C9NewPassword;
import com.olympia.cloud9_api.ICloud9;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText currentPassword, newPassword, repeatPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        repeatPassword = findViewById(R.id.repeatPassword);

        Button b = findViewById(R.id.change_password_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputIsValid()) {
                    ICloud9 cloud9service = ApiUtils.getAPIService();
                    cloud9service.changePassword(MainActivity.currentUsername,
                            currentPassword.getText().toString(),
                            newPassword.getText().toString())
                        .enqueue(new Callback<C9NewPassword>() {

                            @Override
                            public void onResponse(Call<C9NewPassword> call, Response<C9NewPassword> response) {
                                if (response.isSuccessful()) {
                                    String s = getResources().getString(R.string.account_change_password_success);
                                    Log.i(Globals.TAG, s);
                                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                                    finish();
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
                            public void onFailure(Call<C9NewPassword> call, Throwable t) {
                                String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                        getResources().getString(R.string.account_change_password));
                                Log.e(Globals.TAG, s);
                                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                            }
                        });
                }
            }
        });
    }

    boolean inputIsValid() {
        boolean result = false;
        if (currentPassword.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && newPassword.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && repeatPassword.getText().length() >= Globals.MIN_PASSWORD_LENGTH
                && newPassword.getText().toString().equals(repeatPassword.getText().toString())
                && !newPassword.getText().toString().equals(currentPassword.getText().toString())) {
            result = true;
        } else {
            Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                    getResources().getString(R.string.change_password_error),
                    Globals.MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
