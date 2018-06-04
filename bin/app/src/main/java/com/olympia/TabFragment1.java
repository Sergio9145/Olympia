package com.olympia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.olympia.oxford_api.api.DictionaryEntriesApi;
import com.olympia.oxford_api.model.Entry;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TabFragment1 extends Fragment {
    private RecyclerView recyclerView;
    private TextView search;
    private DictionaryEntriesApi entriesApi;

    WordsListAdapter wordsListAdapter;

    public TabFragment1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment_1, container, false);

        entriesApi = ((SampleApp) this.getActivity().getApplication()).apiClient().get(DictionaryEntriesApi.class);

        search = (TextView) v.findViewById(R.id.search);

        recyclerView = (RecyclerView) v.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        RecyclerView recentWordsList = (RecyclerView) v.findViewById(R.id.list_of_recent_words);
        recentWordsList.setLayoutManager(new LinearLayoutManager(this.getContext()));
        wordsListAdapter = new WordsListAdapter(Vocabulary.keywords);
        recentWordsList.setAdapter(wordsListAdapter);

        v.findViewById(R.id.fab).setOnClickListener(v1 -> performSearch(search.getText().toString()));

        return v;
    }

    private void performSearch(final String searchTerm) {
        if (!searchTerm.isEmpty()) {
            entriesApi.getDictionaryEntries("en", searchTerm, BuildConfig.APP_ID, BuildConfig.APP_KEY)
                    .doOnSubscribe(d -> hideKeyboard())
                    .flatMap(re -> Observable.fromIterable(re.getResults()))
                    .flatMap(he -> Observable.fromIterable(he.getLexicalEntries()))
                    .flatMap(le -> Observable.fromIterable(le.getEntries()).map(e -> new CategorizedEntry(searchTerm, le.getLexicalCategory(), e)))
                    .flatMap(ce -> Observable.fromIterable(ce.entry.getSenses()).map(s -> new Definition(ce.category, ce.word, ce.entry, s)))
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(this::createAdapter)
                    .subscribe(this::updateRecyclerView);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
    }

    @NonNull
    private RVRendererAdapter<Definition> createAdapter(List<Definition> definitions) {
        Node node = new Node();
        node.definitions = definitions;
        Vocabulary.nodes.add(node);
        Vocabulary.keywords.add(definitions.get(0).getWord());

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
        wordsListAdapter.notifyDataSetChanged();

        Intent intent = new Intent(getActivity(), WordCardActivity.class);
        intent.putExtra(Globals.WORD_CARD_EXTRA, Vocabulary.keywords.size()-1);
        startActivityForResult(intent, Globals.WORD_CARD_ACTIVITY);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Globals.WORD_CARD_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//                if (lastEntrySearched.size() > 0) {
//                    wordsListAdapter.notifyDataSetChanged();
//                }
//            }
//        }
//    }

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
