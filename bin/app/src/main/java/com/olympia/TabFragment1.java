package com.olympia;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class TabFragment1 extends Fragment {
    private RecyclerView wordsList;
    private TextView search, sortLabel;
    private DictionaryEntriesApi entriesApi;
    private AdapterListWords wordsAdapter;
    private String currentWord;
    private ArrayList<Category> filteredCategories = new ArrayList<>();
    private ArrayList<Keyword> filteredWords = new ArrayList<>();
    private int currentSorting = 0;
    private View v;

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
        sortLabel = v.findViewById(R.id.sort_order);

        Button sortBtn = v.findViewById(R.id.button_sort),
                filterBtn = v.findViewById(R.id.button_filter),
                copyBtn = v.findViewById(R.id.button_copy);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSorting++;
                if (currentSorting > 3) {
                    currentSorting = 0;
                }
                onSort(v);
            }
        });
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilter(v);
            }
        });
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Vocabulary.keywords.isEmpty()) {
                    String joinedWords = TextUtils.join("\n", Vocabulary.keywords);
                    ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Olympia words", joinedWords);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), getResources().getString(R.string.copied), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.not_copied), Toast.LENGTH_LONG).show();
                }
            }
        });

        wordsList = v.findViewById(R.id.list_of_recent_words);
        wordsList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        wordsList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), wordsList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        currentWord = Vocabulary.keywords.get(position).name;
                        decideWhatToDo();
                    }

                    @Override public void onLongItemClick(View view, int pos) {
                        onSetCategory(view, pos);
                    }
                })
        );

        wordsAdapter = new AdapterListWords(Vocabulary.keywords);
        wordsList.setAdapter(wordsAdapter);
        onSort(v);

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
        
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWord = search.getText().toString();
                if (!currentWord.isEmpty()) {
                    decideWhatToDo();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.enter_search_word), Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    private void performSearch(final String searchTerm) {
        search.setText("");
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(wordsList.getWindowToken(), 0);
    }

    @NonNull
    private RVRendererAdapter<Definition> createAdapter(List<Definition> definitions) {
        Node node = new Node();
        node.definitions = definitions;
        Vocabulary.nodes.put(currentWord, node);

        if (!Vocabulary.containsWord(currentWord.toLowerCase())) {
            Keyword k = new Keyword();
            k.id = ++Keyword.last_id;
            k.name = currentWord;
            Vocabulary.keywords.add(k);
            onSort(v);
        }

        RendererBuilder<Definition> builder = new RendererBuilder<Definition>()
                .bind(Definition.class, new DefinitionRenderer());
        ListAdapteeCollection<Definition> collection = new ListAdapteeCollection<>(definitions);
        return new RVRendererAdapter<>(builder, collection);
    }

    private void updateRecyclerView(RVRendererAdapter<Definition> adapter) {
        openWordCard();
    }

    private void decideWhatToDo() {
        if (Vocabulary.nodes.get(currentWord) != null) {
            if (Vocabulary.nodes.get(currentWord).definitions.isEmpty()) {
                performSearch(currentWord);
            } else {
                openWordCard();
            }
        } else {
            performSearch(currentWord);
        }
    }

    private void openWordCard() {
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

    private void onSetCategory(View view, int pos) {
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
        View w = getLayoutInflater().inflate(R.layout.dialog_select_categories, null);
        TextView header = w.findViewById(R.id.categories_select_label);
        header.setText(getResources().getString(R.string.select_category));
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
        AdapterListCategoriesSelect categoriesAdapter = new AdapterListCategoriesSelect(Vocabulary.categories);
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

    private void onSort(View view) {
        switch (currentSorting) {
            case 0:
                sortLabel.setText(getResources().getString(R.string.sort_older_first));
                Collections.sort(Vocabulary.keywords, new ComparatorByDateAsc());
                break;
            case 1:
                sortLabel.setText(getResources().getString(R.string.sort_newer_first));
                Collections.sort(Vocabulary.keywords, new ComparatorByDateDesc());
                break;
            case 2:
                sortLabel.setText(getResources().getString(R.string.sort_ascending));
                Collections.sort(Vocabulary.keywords, new ComparatorByNameAsc());
                break;
            case 3:
                sortLabel.setText(getResources().getString(R.string.sort_descending));
                Collections.sort(Vocabulary.keywords, new ComparatorByNameDesc());
                break;
            default:
                break;
        }
        if (wordsAdapter != null) {
            wordsAdapter.notifyDataSetChanged();
        }
    }

    private void onFilter(View view) {
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
        View w = getLayoutInflater().inflate(R.layout.dialog_select_categories, null);
        TextView header = w.findViewById(R.id.categories_select_label);
        header.setText(getResources().getString(R.string.filter_by));
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
        AdapterListCategoriesSelect categoriesAdapter = new AdapterListCategoriesSelect(Vocabulary.categories);
        categories.setAdapter(categoriesAdapter);

        //* IMPORTANT! DO NOT PLACE BEFORE SETTING ADAPTER!
        categories.measure(0, 0);

        //* Restore previously picked categories if any
        if (filteredCategories != null && !filteredCategories.isEmpty()) {
            for (int i = 0; i < filteredCategories.size(); i++) {
                for (int j = 0; j < Vocabulary.categories.size(); j++) {
                    if (filteredCategories.get(i).name.equalsIgnoreCase(Vocabulary.categories.get(j).name)) {
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
                filteredCategories.clear();
                for (int i = 0; i < Vocabulary.categories.size(); i++) {
                    if (selectedCategories[i]) {
                        filteredCategories.add(Vocabulary.categories.get(i));
                    }
                }

                if (filteredCategories.isEmpty()) {
                    wordsAdapter = new AdapterListWords(Vocabulary.keywords);
                    wordsList.setAdapter(wordsAdapter);
                } else {
                    filteredWords.clear();
                    for (HashMap.Entry<Keyword, ArrayList<Category>> entry : Vocabulary.map.entrySet()) {
                        for (Category c : filteredCategories) {
                            if (entry.getValue().contains(c)) {
                                filteredWords.add(entry.getKey());
                            }
                        }
                    }
                    wordsAdapter = new AdapterListWords(filteredWords);
                    wordsList.setAdapter(wordsAdapter);
                }
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

    private class ComparatorByDateAsc implements Comparator<Keyword> {
        public int compare(Keyword left, Keyword right) {
            return left.dateAdded.compareTo(right.dateAdded);
        }
    }
    private class ComparatorByDateDesc implements Comparator<Keyword> {
        public int compare(Keyword left, Keyword right) {
            return right.dateAdded.compareTo(left.dateAdded);
        }
    }
    private class ComparatorByNameAsc implements Comparator<Keyword> {
        public int compare(Keyword left, Keyword right) {
            return left.name.compareTo(right.name);
        }
    }
    private class ComparatorByNameDesc implements Comparator<Keyword> {
        public int compare(Keyword left, Keyword right) {
            return right.name.compareTo(left.name);
        }
    }
}
