package com.olympia.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import java.util.UUID;

public class WordCardActivity extends AppCompatActivity {
    private StringBuffer text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        text = new StringBuffer();

        Button btn_tts = findViewById(R.id.button_tts);
        btn_tts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globals.tts_enabled) {
                    TypedArray a;
                    if (!Globals.is_speaking) {
                        String utteranceId = UUID.randomUUID().toString();
                        Globals.tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
                        a = getTheme().obtainStyledAttributes(Globals.getTheme(), new int[] { R.attr.stopIcon });
                    } else {
                        Globals.tts.stop();
                        Globals.is_speaking = false;
                        a = getTheme().obtainStyledAttributes(Globals.getTheme(), new int[] { R.attr.speakIcon });
                    }
                    int attributeResourceId = a.getResourceId(0, 0);
                    Drawable drawable = getResources().getDrawable(attributeResourceId);
                    btn_tts.setBackground(drawable);
                    a.recycle();
                } else {
                    Toast.makeText(WordCardActivity.this, getString(R.string.no_tts_support), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //* 1
        TextView keywordLabel = findViewById(R.id.wordEntry);
        keywordLabel.setText(Vocabulary.currentKeyword.name.toUpperCase());
        text.append(keywordLabel.getText());

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
}
