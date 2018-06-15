package com.olympia.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.olympia.Quiz;
import com.olympia.R;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView t1 = findViewById(R.id.last_score);
        t1.setText(String.valueOf(Quiz.lastScore));

        TextView t2 = findViewById(R.id.overall_score);
        t2.setText(String.valueOf(Quiz.overallScore));
    }
}
