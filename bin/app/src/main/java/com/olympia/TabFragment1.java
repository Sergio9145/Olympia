package com.olympia;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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

import com.googlecode.tesseract.android.TessBaseAPI;
import com.olympia.activities.WordCardActivity;
import com.olympia.oxford_api.api.DictionaryEntriesApi;
import com.olympia.oxford_api.api.LemmatronApi;
import com.olympia.oxford_api.model.Entry;
import com.olympia.oxford_api.model.InflectionsListInner;
import com.olympia.oxford_api.model.Lemmatron;
import com.olympia.oxford_api.model.RetrieveEntry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static android.support.v4.app.ActivityCompat.checkSelfPermission;

public class TabFragment1 extends Fragment {
    private RecyclerView wordsList;
    private TextView search, sortLabel;
    private DictionaryEntriesApi entriesApi;
    private LemmatronApi lemmaApi;
    private AdapterListWords wordsAdapter;
    private String currentWord;
    private TessBaseAPI mTess;
    private String datapath = "";
    private Drawable drawable1, drawable2;

    public final static ArrayList<Keyword> filteredWords = new ArrayList<>();

    private View v;
    private boolean error404 = false;

    private static final int CAMERA_PERMISSION_CODE = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        filter();
        sort();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_fragment_1, container, false);

        entriesApi = Globals.apiClient.get(DictionaryEntriesApi.class);
        lemmaApi = Globals.apiClient.get(LemmatronApi.class);

        search = v.findViewById(R.id.search);
        sortLabel = v.findViewById(R.id.sort_order);

        drawable1 = getContext().getDrawable(R.drawable.rect_bg2);
        drawable2 = getContext().getDrawable(R.drawable.rect_bg3);

        Button sortBtn = v.findViewById(R.id.button_sort),
                filterBtn = v.findViewById(R.id.button_filter),
                copyBtn = v.findViewById(R.id.button_copy),
                microphoneBtn = v.findViewById(R.id.button_mic),
                cameraBtn = v.findViewById(R.id.button_cam);

        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Globals.currentSorting++;
                if (Globals.currentSorting > 3) {
                    Globals.currentSorting = 0;
                }
                sort();
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
        sort();

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
                    Toast.makeText(getContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();
                }
            }
        });

        datapath = getActivity().getFilesDir() + "/tesseract/";
        mTess = new TessBaseAPI();

        checkFile(new File(datapath + "tessdata/"));

        mTess.init(datapath, "eng");


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, Globals.CAMERA_ACTIVITY);
                }
            }
        });
        return v;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, Globals.CAMERA_ACTIVITY);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Globals.CAMERA_ACTIVITY:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    String OCRresult = null;
                    mTess.setImage(photo);
                    OCRresult = mTess.getUTF8Text();
                    search.setText(OCRresult);
                    break;
                case Globals.SPEECH_ACTIVITY:
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    search.setText(result.get(0));
                    break;
                default:
                    break;
            }
        }
    }

    private void performSearch(final String searchTerm) {
        search.setText("");
        error404 = false;
        //* TODO: for further usage:
        lemmaApi.inflectionsSourceLangWordIdGet(Vocabulary.currentDictLanguage, searchTerm, BuildConfig.APP_ID, BuildConfig.APP_KEY)
                .onErrorReturn((Throwable ex) -> {
//                    error404 = true;
                    return new Lemmatron();
                })
                .flatMap(re -> Observable.fromIterable(re.getResults()))
                .flatMap(he -> Observable.fromIterable(he.getLexicalEntries()))
                .flatMap(le -> Observable.fromIterable(le.getInflectionOf()))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getWord);
        entriesApi.getDictionaryEntries(Vocabulary.currentDictLanguage, searchTerm, BuildConfig.APP_ID, BuildConfig.APP_KEY)
                .onErrorReturn((Throwable ex) -> {
                    error404 = true;
                    return new RetrieveEntry();
                })
                .doOnSubscribe(d -> hideKeyboard())
                .flatMap(re -> Observable.fromIterable(re.getResults()))
                .flatMap(he -> Observable.fromIterable(he.getLexicalEntries()))
                .flatMap(le -> Observable.fromIterable(le.getEntries())
                .map(e -> new CategorizedEntry(searchTerm, le.getLexicalCategory(), e)))
                .flatMap(ce -> Observable.fromIterable(ce.entry.getSenses())
                .map(s -> new Definition(ce.category, ce.word, ce.entry, s)))
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::getResults);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(wordsList.getWindowToken(), 0);
        }
    }

    private void getWord(List<InflectionsListInner> words) {

    }

    private void getResults(List<Definition> definitions) {
        if (error404) {
            Toast.makeText(getContext(), getResources().getString(R.string.no_such_word), Toast.LENGTH_LONG).show();
        } else {
            Node node = new Node();
            node.definitions = definitions;
            Vocabulary.nodes.put(currentWord, node);

            if (!Vocabulary.containsWord(currentWord.toLowerCase())) {
                Keyword k = new Keyword();
                k.id = ++Keyword.last_id;
                k.name = currentWord;
                Vocabulary.currentKeyword = k;
                Vocabulary.keywords.add(Vocabulary.currentKeyword);
                sort();
            }
            openWordCard();
        }
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
        Vocabulary.currentKeyword = Vocabulary.getKeyword(currentWord);
        Intent intent = new Intent(getActivity(), WordCardActivity.class);
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
                            view.setBackground(drawable2);
                        } else {
                            view.setBackground(drawable1);
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
                        v1.setBackground(drawable2);
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

    private void sort() {
        ArrayList<Keyword> list;
        if (Globals.filteredCategories.isEmpty()) {
            list = Vocabulary.keywords;
        } else {
            list = filteredWords;
        }
        switch (Globals.currentSorting) {
            case 0:
                sortLabel.setText(getResources().getString(R.string.sort_older_first));
                Collections.sort(list, new ComparatorByDateAsc());
                break;
            case 1:
                sortLabel.setText(getResources().getString(R.string.sort_newer_first));
                Collections.sort(list, new ComparatorByDateDesc());
                break;
            case 2:
                sortLabel.setText(getResources().getString(R.string.sort_ascending));
                Collections.sort(list, new ComparatorByNameAsc());
                break;
            case 3:
                sortLabel.setText(getResources().getString(R.string.sort_descending));
                Collections.sort(list, new ComparatorByNameDesc());
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
                            view.setBackground(drawable2);
                        } else {
                            view.setBackground(drawable1);
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
        if (!Globals.filteredCategories.isEmpty()) {
            for (int i = 0; i < Globals.filteredCategories.size(); i++) {
                for (int j = 0; j < Vocabulary.categories.size(); j++) {
                    if (Globals.filteredCategories.get(i).name.equalsIgnoreCase(Vocabulary.categories.get(j).name)) {
                        selectedCategories[j] = true;
                        View v1 = categories.getChildAt(j);
                        v1.setBackground(drawable2);
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
                Globals.filteredCategories.clear();
                for (int i = 0; i < Vocabulary.categories.size(); i++) {
                    if (selectedCategories[i]) {
                        Globals.filteredCategories.add(Vocabulary.categories.get(i));
                    }
                }

                filter();
                sort();
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

    private void filter() {
        if (Globals.filteredCategories.isEmpty()) {
            wordsAdapter = new AdapterListWords(Vocabulary.keywords);
            wordsList.setAdapter(wordsAdapter);
        } else {
            filteredWords.clear();
            for (HashMap.Entry<Keyword, ArrayList<Category>> entry : Vocabulary.map.entrySet()) {
                for (Category c : Globals.filteredCategories) {
                    if (entry.getValue().contains(c)) {
                        filteredWords.add(entry.getKey());
                    }
                }
            }
            wordsAdapter = new AdapterListWords(filteredWords);
            wordsList.setAdapter(wordsAdapter);
        }
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

    private void checkFile(File dir) {
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);

            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            AssetManager assetManager = getActivity().getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }

            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
