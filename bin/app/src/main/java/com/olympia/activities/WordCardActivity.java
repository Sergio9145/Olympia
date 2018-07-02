package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.olympia.Category;
import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.R;
import com.olympia.Vocabulary;

import java.util.ArrayList;

public class WordCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Globals.loadTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_card);

        //* 1
        TextView keywordLabel = findViewById(R.id.wordEntry);
        keywordLabel.setText(Vocabulary.currentKeyword.name.toUpperCase());

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
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
