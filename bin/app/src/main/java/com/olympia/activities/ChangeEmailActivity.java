package com.olympia.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;
import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.C9Email;
import com.olympia.cloud9_api.C9User;
import com.olympia.cloud9_api.ICloud9;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends AppCompatActivity {
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        email = findViewById(R.id.newEmail);
        password = findViewById(R.id.password);

        Button b = findViewById(R.id.change_email_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputIsValid()) {
                    ICloud9 cloud9service = ApiUtils.getAPIService();
                    cloud9service.changeEmail(email.getText().toString(),
                            MainActivity.currentUsername,
                            password.getText().toString())
                            .enqueue(new Callback<C9Email>() {

                                @Override
                                public void onResponse(Call<C9Email> call, Response<C9Email> response) {
                                    if (response.isSuccessful()) {
                                        C9Email newEmail = response.body();
                                        String s = String.format(Locale.ENGLISH, getResources().getString(R.string.account_change_email_success),
                                                newEmail.email);
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
                                public void onFailure(Call<C9Email> call, Throwable t) {
                                    String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                            getResources().getString(R.string.account_change_email));
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
        if (email.getText().toString().contains("@")
                && password.getText().length() >= Globals.MIN_PASSWORD_LENGTH) {
            result = true;
        } else {
            Toast.makeText(getApplicationContext(), String.format(Locale.ENGLISH,
                    getResources().getString(R.string.change_email_field_invalid),
                    Globals.MIN_PASSWORD_LENGTH), Toast.LENGTH_LONG).show();
        }
        return result;
    }
}
