package com.olympia.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.Quiz;
import com.olympia.R;
import com.olympia.Vocabulary;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class QuizActivity extends AppCompatActivity {
    private TextView number, question;
    private EditText answer;
    private int pos = -1, score = 0;
    private Drawable drawable1, drawable2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quizzes);

        number = findViewById(R.id.number);
        question = findViewById(R.id.question_descr);
        answer = findViewById(R.id.answer);

        drawable1 = getDrawable(R.drawable.btn_speak);
        drawable2 = getDrawable(R.drawable.btn_stop);

        Button btn = findViewById(R.id.next),
            btn_tts = findViewById(R.id.button_tts),
            microphoneBtn = findViewById(R.id.button_mic);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                setTask(pos++);
            }
        });

        setTask(pos++);

        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globals.tts_enabled) {
                    if (!Globals.is_speaking) {
                        String utteranceId = UUID.randomUUID().toString();
                        Globals.tts.speak(question.getText(), TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                        btn_tts.setBackground(drawable2);

                    } else {
                        Globals.tts.stop();
                        Globals.is_speaking = false;
                        btn_tts.setBackground(drawable1);
                    }
                } else {
                    Toast.makeText(QuizActivity.this, getString(R.string.tts_not_supported), Toast.LENGTH_SHORT).show();
                }
            }
        });

        microphoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt));
                try {
                    startActivityForResult(intent, Globals.SPEECH_ACTIVITY);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(QuizActivity.this, getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Globals.SPEECH_ACTIVITY:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    answer.setText(result.get(0));
                    break;
                default:
                    break;
            }
        }
    }

    private void setTask(int i) {
        StringBuffer toSet2 = new StringBuffer();
        if (pos < Quiz.filteredWords.size()) {
            if (Vocabulary.nodes != null && !Vocabulary.nodes.isEmpty()) {
                StringBuffer toSet1 = new StringBuffer();
                toSet1.append(pos + 1).append(" / ").append(Quiz.filteredWords.size());
                number.setText(toSet1);

                String word = Quiz.filteredWords.get(pos).name;
                if (Vocabulary.nodes.get(word) != null && Vocabulary.nodes.get(word).definitions.size() > 0) {
                    Definition s = Vocabulary.nodes.get(word).definitions.get(0);
                    if (s != null && s.getDefiniton() != null) {
                        toSet2.append(s.getDefiniton()).append("\n\n");
                    }
                }
                if (toSet2.toString().isEmpty()) {
                    question.setText(getResources().getString(R.string.quiz_node_not_found));
                } else {
                    question.setText(toSet2);
                }
                answer.setText("");
            }
        } else {
            Quiz.lastScore = score;
            Quiz.overallScore += score;
            Intent intent = new Intent(QuizActivity.this, StatisticsActivity.class);
            startActivityForResult(intent, Globals.QUIZ_ACTIVITY);
        }
    }

    private void checkAnswer() {
        String s = answer.getText().toString();
        if (s.equals(Quiz.filteredWords.get(pos).name)) {
            score += 10;
        } else {
            score -= 10;
        }
    }
}
