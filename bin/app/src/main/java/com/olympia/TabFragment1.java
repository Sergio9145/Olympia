package com.olympia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.activities.WordCardActivity;
import com.olympia.oxford_api.api.DictionaryEntriesApi;
import com.olympia.oxford_api.model.Entry;
import com.pedrogomez.renderers.ListAdapteeCollection;
import com.pedrogomez.renderers.RVRendererAdapter;
import com.pedrogomez.renderers.RendererBuilder;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TabFragment1 extends Fragment {
    private RecyclerView wordsList;
    private TextView search;
    private DictionaryEntriesApi entriesApi;
    private View v;
    private AdapterWordsList wordsAdapter;
    private String currentWord;

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
        v = inflater.inflate(R.layout.tab_fragment_1, container, false);

        entriesApi = ((SampleApp) this.getActivity().getApplication()).apiClient().get(DictionaryEntriesApi.class);
        search = v.findViewById(R.id.search);

        wordsList = v.findViewById(R.id.list_of_recent_words);
        wordsList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        wordsList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), wordsList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        currentWord = Vocabulary.keywords.get(position);
                        Intent intent = new Intent(getActivity(), WordCardActivity.class);
                        intent.putExtra(Globals.WORD_CARD_EXTRA, currentWord);
                        startActivityForResult(intent, Globals.WORD_CARD_ACTIVITY);
                    }

                    @Override public void onLongItemClick(View view, int pos) {
                        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
                        View w = getLayoutInflater().inflate(R.layout.dialog_categories_selection, null);
                        categoryBuilder.setView(w);

                        boolean[] selectedCategories = new boolean[Vocabulary.categories.size()];
                        RecyclerView categories = w.findViewById(R.id.categories_selection_list);
                        categories.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        categories.addOnItemTouchListener(
                                new RecyclerItemClickListener(getContext(), categories, new RecyclerItemClickListener.OnItemClickListener() {
                                    @Override public void onItemClick(View view, int position) {
                                        //* Switch between enabled/disabled
                                        selectedCategories[position] = !selectedCategories[position];
                                        if (selectedCategories[position]) {
                                            view.setBackground(getResources().getDrawable(R.drawable.bordered_button_yellow));
                                        } else {
                                            view.setBackground(getResources().getDrawable(R.drawable.bordered_button_grey));
                                        }
                                    }
                                    @Override public void onLongItemClick(View view, int position) {
                                        //* Do nothing
                                    }
                                })
                        );
                        AdapterCategoriesSelect categoriesAdapter = new AdapterCategoriesSelect(Vocabulary.categories);
                        categories.setAdapter(categoriesAdapter);

                        //* IMPORTANT! DO NOT PLACE BEFORE SETTING ADAPTER!
                        categories.measure(0, 0);

                        //* Restore previously picked categories if any
                        ArrayList<Category> pickedCategories = Vocabulary.map.get(Vocabulary.keywords.get(pos));
                        if (pickedCategories != null && !pickedCategories.isEmpty()) {
                            for (int i = 0; i < pickedCategories.size(); i++) {
                                for (int j = 0; j < Vocabulary.categories.size(); j++) {
                                    if (pickedCategories.get(i).name.equalsIgnoreCase(Vocabulary.categories.get(j).name)) {
                                        selectedCategories[j] = true;
                                        View v1 = categories.getChildAt(j);
                                        v1.setBackground(getResources().getDrawable(R.drawable.bordered_button_yellow));
                                    }
                                }
                            }
                        }

                        AlertDialog dialog = categoryBuilder.create();

                        Button positiveBtn = w.findViewById(R.id.button_positive);
                        Button negativeBtn = w.findViewById(R.id.button_negative);

                        positiveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<Category> pickedCategories = new ArrayList<>();
                                for (int i = 0; i < Vocabulary.categories.size(); i++) {
                                    if (selectedCategories[i]) {
                                        pickedCategories.add(Vocabulary.categories.get(i));
                                    }
                                }
                                Vocabulary.map.put(Vocabulary.keywords.get(pos), pickedCategories);
                                dialog.dismiss();
                            }
                        });
                        negativeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                })
        );

        wordsAdapter = new AdapterWordsList(Vocabulary.keywords);
        wordsList.setAdapter(wordsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                wordsAdapter.onItemMove(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                wordsAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(wordsList);
        
        v.findViewById(R.id.fab).setOnClickListener(v1 -> performSearch(search.getText().toString()));

        return v;
    }

    private void performSearch(final String searchTerm) {
        if (!searchTerm.isEmpty()) {
            search.setText("");
            currentWord = searchTerm;
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
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.enter_search_word), Toast.LENGTH_LONG).show();
        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(wordsList.getWindowToken(), 0);
    }

    @NonNull
    private RVRendererAdapter<Definition> createAdapter(List<Definition> definitions) {
        if (Vocabulary.keywords.size() > 0) {
            if (!Vocabulary.keywords.contains(definitions.get(0).getWord().toLowerCase())) {
                Node node = new Node();
                node.definitions = definitions;
                Vocabulary.nodes.put(currentWord, node);
                Vocabulary.keywords.add(currentWord);
            }
        } else {
            Node node = new Node();
            node.definitions = definitions;
            Vocabulary.nodes.put(currentWord, node);
            Vocabulary.keywords.add(currentWord);
        }

        RendererBuilder<Definition> builder = new RendererBuilder<Definition>()
                .bind(Definition.class, new DefinitionRenderer());
        ListAdapteeCollection<Definition> collection = new ListAdapteeCollection<>(definitions);
        return new RVRendererAdapter<>(builder, collection);
    }

    private void updateRecyclerView(RVRendererAdapter<Definition> adapter) {
        wordsAdapter.notifyDataSetChanged();

        Intent intent = new Intent(getActivity(), WordCardActivity.class);
        intent.putExtra(Globals.WORD_CARD_EXTRA, currentWord);
        startActivityForResult(intent, Globals.WORD_CARD_ACTIVITY);
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
