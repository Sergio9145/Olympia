package com.olympia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.olympia.oxford_api.api.DictionaryEntriesApi;
import com.olympia.oxford_api.model.Entry;
import com.olympia.oxford_api.model.HeadwordEntry;
import com.olympia.oxford_api.model.Sense;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WordsList extends AppCompatActivity {
    final static String EXTRA_DEF = "EXTRA_DEF";
    final static int WORD_CARD_ACTIVITY = 42;

    private RecyclerView recyclerView;
    private TextView search;
    private DictionaryEntriesApi entriesApi;
    public List<HeadwordEntry> lastEntrySearched;
    public ArrayList<String> vocabulary;
    public ArrayList<Definition> definitions;
    public ArrayList<Sense> senses;
    WordsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabs_activity);

        entriesApi = ((SampleApp) getApplication()).apiClient().get(DictionaryEntriesApi.class);

        search = (TextView) findViewById(R.id.search);

        vocabulary = new ArrayList<>();
        definitions = new ArrayList<>();
        senses = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RecyclerView recentWordsList = (RecyclerView) findViewById(R.id.list_of_recent_words);
        recentWordsList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WordsListAdapter(vocabulary);
        recentWordsList.setAdapter(adapter);

        findViewById(R.id.fab).setOnClickListener(v -> performSearch(search.getText().toString()));
    }

    private void performSearch(final String searchTerm) {
        adapter.notifyDataSetChanged();
        entriesApi.getDictionaryEntries("en", searchTerm, BuildConfig.APP_ID, BuildConfig.APP_KEY)
                .doOnSubscribe(d -> hideKeyboard())
                .flatMap(re -> {
                    lastEntrySearched = re.getResults();
                    if (lastEntrySearched.size() > 0) {
                        vocabulary.add(lastEntrySearched.get(0).getWord());
                    }
                    return Observable.fromIterable(lastEntrySearched);
                })
                .flatMap(he -> Observable.fromIterable(he.getLexicalEntries()))
                .flatMap(le -> Observable.fromIterable(le.getEntries()).map(e -> new CategorizedEntry(searchTerm, le.getLexicalCategory(), e)))
                .flatMap(ce -> {
                    if (ce.entry.getSenses().size() > 0) {
                        senses.add(ce.entry.getSenses().get(0));
                        definitions.add(new Definition(ce.category, ce.word, ce.entry, ce.entry.getSenses().get(0)));
                    }
                    return Observable.fromIterable(ce.entry.getSenses()).map(s -> new Definition(ce.category, ce.word, ce.entry, s));
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .map(WordsList.this::createAdapter)
                .subscribe(WordsList.this::updateRecyclerView);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
    }

    @NonNull
    private RVRendererAdapter<Definition> createAdapter(List<Definition> definitions) {
        RendererBuilder<Definition> builder = new RendererBuilder<Definition>()
                .bind(Definition.class, new DefinitionRenderer());
        ListAdapteeCollection<Definition> collection = new ListAdapteeCollection<>(definitions);
        return new RVRendererAdapter<>(builder, collection);
    }

    private void updateRecyclerView(RVRendererAdapter<Definition> adapter) {
//        if (recyclerView.getAdapter() != null) {
//            recyclerView.swapAdapter(adapter, true);
//        } else {
//            recyclerView.setAdapter(adapter);
//        }

        Intent intent = new Intent(WordsList.this, WordCard.class);
        intent.putExtra(EXTRA_DEF, senses.get(senses.size() - 1).getDefinitions()[0]);
        startActivityForResult(intent, WORD_CARD_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == WORD_CARD_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                if (lastEntrySearched.size() > 0) {
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    private static class CategorizedEntry {
        final String word;
        final String category;
        final Entry entry;

        CategorizedEntry(String word, String category, Entry entry) {
            this.word = word;
            this.category = category;
            this.entry = entry;
        }
    }
}
