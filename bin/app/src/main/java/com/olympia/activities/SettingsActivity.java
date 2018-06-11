package com.olympia.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.Globals;
import com.olympia.R;
import com.olympia.room_db.AppDatabase;
import com.olympia.room_db.RoomDbFacade;

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
                Toast.makeText(SettingsActivity.this, "Not implemented yet", Toast.LENGTH_LONG).show();
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Not implemented yet", Toast.LENGTH_LONG).show();
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Not implemented yet", Toast.LENGTH_LONG).show();
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
                Toast.makeText(SettingsActivity.this, "Not implemented yet", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
