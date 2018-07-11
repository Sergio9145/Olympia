package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olympia.Globals;
import com.olympia.Quiz;
import com.olympia.R;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView t1 = findViewById(R.id.last_score);
        t1.setText(String.valueOf(Quiz.lastScore));

        TextView t2 = findViewById(R.id.overall_score);
        t2.setText(String.valueOf(Quiz.overallScore));

        Button b = findViewById(R.id.ok);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatisticsActivity.this, WordsListActivity.class);
                startActivity(intent);
            }
        });
    }
}
