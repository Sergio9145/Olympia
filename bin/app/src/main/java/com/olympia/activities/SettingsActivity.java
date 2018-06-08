package com.olympia.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.olympia.R;
import com.olympia.room_db.AppDatabase;
import com.olympia.room_db.RoomDbFacade;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button b1 = findViewById(R.id.save_to_db);
        Button b2 = findViewById(R.id.restore_from_db);
        Button b3 = findViewById(R.id.clear_from_db);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        RoomDbFacade.saveAllKeywords(AppDatabase.getAppDatabase(getApplicationContext()));
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
                    }
                });
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
