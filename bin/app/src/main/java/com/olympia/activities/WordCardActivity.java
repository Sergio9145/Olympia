package com.olympia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.olympia.Definition;
import com.olympia.Globals;
import com.olympia.R;
import com.olympia.Vocabulary;

public class WordCardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_card);

        Intent intent = getIntent();
        int word_id = intent.getIntExtra(Globals.WORD_CARD_EXTRA, 0);

        StringBuffer toSet = new StringBuffer();

        toSet.append(Vocabulary.nodes.get(word_id).definitions.get(0).getWord().toUpperCase())
            .append("\n\n");
        for (Definition s : Vocabulary.nodes.get(word_id).definitions) {
            if (s.getDefiniton() != null) {
                toSet.append(s.getCategory())
                    .append(":\n")
                    .append(s.getDefiniton())
                    .append("\n\n");
            }
        }

        TextView entry = findViewById(R.id.wordDefinition);
        entry.setText(toSet);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
