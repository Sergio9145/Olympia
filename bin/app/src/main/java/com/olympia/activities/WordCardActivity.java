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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card);

        Intent intent = getIntent();
        String keyword = intent.getStringExtra(Globals.WORD_CARD_EXTRA);

        //* 1
        TextView keywordLabel = findViewById(R.id.wordEntry);
        keywordLabel.setText(keyword.toUpperCase());

        //* 2
        StringBuffer toSet1 = new StringBuffer();
        TextView categories = findViewById(R.id.wordCategories);
        ArrayList<Category> categoriesN = Vocabulary.map.get(keyword);
        if (categoriesN != null && !categoriesN.isEmpty()) {
            categories.setVisibility(View.VISIBLE);
            toSet1.append("Categories: ");
            for (Category c : categoriesN) {
                toSet1.append(c.name)
                .append(", ");
            }
            categories.setText(toSet1);
        }

        //* 3
        StringBuffer toSet2 = new StringBuffer();
        TextView entry = findViewById(R.id.wordDefinition);
        for (Definition s : Vocabulary.nodes.get(keyword).definitions) {
            if (s.getDefiniton() != null) {
                toSet2.append(s.getCategory())
                    .append(":\n")
                    .append(s.getDefiniton())
                    .append("\n\n");
            }
        }
        entry.setText(toSet2);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
