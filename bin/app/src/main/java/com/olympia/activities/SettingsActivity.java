package com.olympia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.Globals;
import com.olympia.MainActivity;
import com.olympia.R;
import com.olympia.cloud9_api.ApiUtils;
import com.olympia.cloud9_api.C9User;
import com.olympia.cloud9_api.ICloud9;
import com.olympia.room_db.AppDatabase;
import com.olympia.room_db.RoomDbFacade;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button b1 = findViewById(R.id.save_to_db),
            b2 = findViewById(R.id.restore_from_db),
            b3 = findViewById(R.id.clear_from_db),
            b4 = findViewById(R.id.account_change_name),
            b5 = findViewById(R.id.account_change_email),
            b6 = findViewById(R.id.account_change_password),
            b7 = findViewById(R.id.account_logout),
            b8 = findViewById(R.id.account_delete);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.saveAllKeywords(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_saved), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.getAllKeywords(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_restored), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.deleteAll(AppDatabase.getAppDatabase(getApplicationContext()));
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.database_cleared), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeNameActivity.class);
                startActivityForResult(intent, Globals.CHANGE_NAME_ACTIVITY);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangeEmailActivity.class);
                startActivityForResult(intent, Globals.CHANGE_EMAIL_ACTIVITY);
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivityForResult(intent, Globals.CHANGE_PASSWORD_ACTIVITY);
            }
        });

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.LOGOUT_REQUESTED);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(v.getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
                categoryBuilder.setView(mView);
                AlertDialog dialog = categoryBuilder.create();
                TextView title = mView.findViewById(R.id.title),
                        descr = mView.findViewById(R.id.descr);

                title.setText(getResources().getString(R.string.deleting_account_title));
                descr.setText(getResources().getString(R.string.deleting_account_descr));

                EditText password = mView.findViewById(R.id.password);

                Button positiveBtn = mView.findViewById(R.id.button_positive),
                        negativeBtn = mView.findViewById(R.id.button_negative);
                positiveBtn.setText(getResources().getString(R.string.delete));
                negativeBtn.setText(getResources().getString(R.string.cancel));

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ICloud9 cloud9service = ApiUtils.getAPIService();
                        cloud9service.deleteAccount(MainActivity.currentUsername,
                                password.getText().toString())
                                .enqueue(new Callback<C9User>() {

                                    @Override
                                    public void onResponse(Call<C9User> call, Response<C9User> response) {
                                        if (response.isSuccessful()) {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra(Globals.SETTINGS_EXTRA, Globals.DELETE_ACCOUNT_REQUESTED);
                                            setResult(Activity.RESULT_OK, returnIntent);
                                            dialog.dismiss();
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
                                    public void onFailure(Call<C9User> call, Throwable t) {
                                        String s = String.format(Locale.ENGLISH, getResources().getString(R.string.error_failed_attempt),
                                                getResources().getString(R.string.account_delete));
                                        Log.e(Globals.TAG, s);
                                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }
}
