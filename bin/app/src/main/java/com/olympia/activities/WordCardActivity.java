package com.olympia.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.Category;
import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.R;
import com.olympia.Vocabulary;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class WordCardActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private StringBuffer text;
    boolean tts_enabled = false,
        is_speaking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        tts = new TextToSpeech(WordCardActivity.this, WordCardActivity.this);
        text = new StringBuffer();

        Button btn = findViewById(R.id.button_tts);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts_enabled) {
                    TypedArray a;
                    if (!is_speaking) {
                        String utteranceId = UUID.randomUUID().toString();
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                    } else {
                        tts.stop();
                        is_speaking = false;
                    }
                } else {
                    Toast.makeText(WordCardActivity.this, getString(R.string.no_tts_support), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //* 1
        TextView keywordLabel = findViewById(R.id.wordEntry);
        keywordLabel.setText(Vocabulary.currentKeyword.name.toUpperCase());
        text.append(keywordLabel.getText());
        text.append("/n");

        //* 2
        StringBuffer toSet1 = new StringBuffer();
        TextView categories = findViewById(R.id.wordCategories);
        ArrayList<Category> categoriesN = Vocabulary.map.get(Vocabulary.currentKeyword);
        if (categoriesN != null && !categoriesN.isEmpty()) {
            categories.setVisibility(View.VISIBLE);
            toSet1.append(getResources().getString(R.string.word_card_categories));
            for (int i = 0; i < categoriesN.size(); i++) {
                toSet1.append(categoriesN.get(i).name);
                if (i < categoriesN.size() - 1) {
                    toSet1.append(", ");
                }
            }
            categories.setText(toSet1);
        }
        text.append(toSet1);
        text.append("/n");

        //* 3
        StringBuffer toSet2 = new StringBuffer();
        TextView entry = findViewById(R.id.wordDefinition);
        if (Vocabulary.nodes != null && !Vocabulary.nodes.isEmpty()) {
            for (Definition s : Vocabulary.nodes.get(Vocabulary.currentKeyword.name).definitions) {
                if (s != null && s.getDefiniton() != null) {
                    toSet2.append(s.getCategory())
                            .append(":\n")
                            .append(s.getDefiniton())
                            .append("\n\n");
                }
            }
            entry.setText(toSet2);
        }
        text.append(toSet2);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onInit(int status) {
        Locale language = Locale.ENGLISH;
        if (language == null) {
            this.tts_enabled = false;
            Toast.makeText(this, "Not language selected", Toast.LENGTH_SHORT).show();
            return;
        }
        int result = tts.setLanguage(language);
        if (result == TextToSpeech.LANG_MISSING_DATA) {
            this.tts_enabled = false;
            Toast.makeText(this, "Missing language data", Toast.LENGTH_SHORT).show();
            return;
        } else if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            this.tts_enabled = false;
            Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.tts_enabled = true;
            Locale currentLanguage = tts.getVoice().getLocale();
            Toast.makeText(this, "Language " + currentLanguage, Toast.LENGTH_SHORT).show();
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    is_speaking = false;
                }

                @Override
                public void onError(String utteranceId) { }

                @Override
                public void onStart(String utteranceId) {
                    is_speaking = true;
                }
            });
        }
    }
}
