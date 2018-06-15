package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olympia.Category;
import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.Keyword;
import com.olympia.R;
import com.olympia.Vocabulary;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizActivity extends AppCompatActivity {
    private Category category = null;
    private ArrayList<Keyword> filteredWords = new ArrayList<>();
    TextView question;
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        Intent intent = getIntent();
        int id = intent.getIntExtra(Globals.QUIZZES_EXTRA, -1);
        if (id > -1) {
            category = Vocabulary.getCategoryById(id);
        }
        if (category != null) {
            for (HashMap.Entry<Keyword, ArrayList<Category>> entry : Vocabulary.map.entrySet()) {
                if (entry.getValue().contains(category)) {
                    filteredWords.add(entry.getKey());
                }
            }
        } else {
            filteredWords = Vocabulary.keywords;
        }

        question = findViewById(R.id.question_descr);

        Button btn = findViewById(R.id.next);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTask(pos++);
            }
        });

        setTask(pos++);
    }

    private void setTask(int i) {
        StringBuffer toSet2 = new StringBuffer();
        if (pos < filteredWords.size()) {
            if (Vocabulary.nodes != null && !Vocabulary.nodes.isEmpty()) {
                String word = filteredWords.get(pos).name;
                for (Definition s : Vocabulary.nodes.get(word).definitions) {
                    if (s != null && s.getDefiniton() != null) {
                        toSet2.append(s.getCategory())
                                .append(":\n")
                                .append(s.getDefiniton())
                                .append("\n\n");
                    }
                }
                question.setText(toSet2);
            }
        } else {
            Intent intent = new Intent(QuizActivity.this, StatisticsActivity.class);
            startActivityForResult(intent, Globals.QUIZ_ACTIVITY);
        }
    }
}
