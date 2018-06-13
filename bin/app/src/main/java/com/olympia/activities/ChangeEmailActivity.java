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
import com.olympia.cloud9_api.C9Email;
import com.olympia.cloud9_api.C9User;
import com.olympia.cloud9_api.ICloud9;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        EditText email, password;
        email = findViewById(R.id.newEmail);
        password = findViewById(R.id.password);

        Button b = findViewById(R.id.change_email_btn);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                C9User user = new C9User();

                user.setEmail(email.getText().toString());
                user.setUsername(MainActivity.currentUsername);
                user.setPassword(password.getText().toString());

                ICloud9 cloud9service = ApiUtils.getAPIService();
                cloud9service.changeEmail(user.getEmail(),
                    user.getUsername(),
                    user.getPassword())
                    .enqueue(new Callback<C9Email>() {

                        @Override
                        public void onResponse(Call<C9Email> call, Response<C9Email> response) {
                            if (response.isSuccessful()) {
                                C9Email newEmail = response.body();
                                Toast.makeText(getApplicationContext(), "User's email was changed to "
                                    .concat(newEmail.email), Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                try {
                                    JSONObject error = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getApplicationContext(), error.getString("msg"), Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Server unreachable", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<C9Email> call, Throwable t) {
                            Log.e(Globals.TAG, "Unable to submit Change name request to the server");
                            Toast.makeText(getApplicationContext(), "Unable to submit Change name request to the server", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
    }
}
