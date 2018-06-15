package com.olympia;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.olympia.activities.QuizActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class TabFragment3 extends Fragment {

    private Category selectedCategory = null;

    public TabFragment3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment_3, container, false);
        Button b = v.findViewById(R.id.start_quiz);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelect(v);
            }
        });

        return v;
    }

    private void onSelect(View view) {
        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
        View w = getLayoutInflater().inflate(R.layout.dialog_select_categories, null);
        TextView header = w.findViewById(R.id.categories_select_label);
        header.setText(getResources().getString(R.string.quiz_select_categories));
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

        AlertDialog dialog = categoryBuilder.create();

        Button positiveBtn = w.findViewById(R.id.button_positive);
        Button negativeBtn = w.findViewById(R.id.button_negative);

        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Category> filteredCategories = new ArrayList<>();
                for (int i = 0; i < Vocabulary.categories.size(); i++) {
                    if (selectedCategories[i]) {
                        filteredCategories.add(Vocabulary.categories.get(i));
                    }
                }

                if (filteredCategories.isEmpty()) {
                    Quiz.filteredWords = Vocabulary.keywords;
                } else {
                    Quiz.filteredWords.clear();
                    for (HashMap.Entry<Keyword, ArrayList<Category>> entry : Vocabulary.map.entrySet()) {
                        for (Category c : filteredCategories) {
                            if (entry.getValue().contains(c)) {
                                Quiz.filteredWords.add(entry.getKey());
                            }
                        }
                    }
                }
                if (Quiz.filteredWords.isEmpty()) {
                    Toast.makeText(getContext(), getResources().getString(R.string.quiz_list_is_empty), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getActivity(), QuizActivity.class);
                    startActivityForResult(intent, Globals.QUIZ_ACTIVITY);
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
}
