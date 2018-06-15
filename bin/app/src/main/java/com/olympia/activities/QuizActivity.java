package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.Quiz;
import com.olympia.R;
import com.olympia.Vocabulary;

public class QuizActivity extends AppCompatActivity {
    TextView question;
    int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

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
        if (pos < Quiz.filteredWords.size()) {
            if (Vocabulary.nodes != null && !Vocabulary.nodes.isEmpty()) {
                String word = Quiz.filteredWords.get(pos).name;
                if (Vocabulary.nodes.get(word) != null) {
                    for (Definition s : Vocabulary.nodes.get(word).definitions) {
                        if (s != null && s.getDefiniton() != null) {
                            toSet2.append(s.getCategory())
                                    .append(":\n")
                                    .append(s.getDefiniton())
                                    .append("\n\n");
                        }
                    }
                }
                if (toSet2.toString().isEmpty()) {
                    question.setText(getResources().getString(R.string.quiz_node_not_found));
                } else {
                    question.setText(toSet2);
                }
            }
        } else {
            Intent intent = new Intent(QuizActivity.this, StatisticsActivity.class);
            startActivityForResult(intent, Globals.QUIZ_ACTIVITY);
        }
    }
}
