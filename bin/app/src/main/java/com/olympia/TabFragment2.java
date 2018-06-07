package com.olympia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TabFragment2 extends Fragment {

    private AdapterCategoriesList categoriesAdapter;
    private View v;

    public TabFragment2() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_fragment_2, container, false);

        RecyclerView categoryList = v.findViewById(R.id.categories_list);
        categoryList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        categoryList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), categoryList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
                        View mView = getLayoutInflater().inflate(R.layout.dialog_rename_category, null);
                        categoryBuilder.setView(mView);
                        AlertDialog dialog = categoryBuilder.create();
                        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                        EditText renamedCategory = mView.findViewById(R.id.category_name);
                        renamedCategory.setText(Vocabulary.categories.get(position).name);

                        Button positiveBtn = mView.findViewById(R.id.button_positive);
                        Button negativeBtn = mView.findViewById(R.id.button_negative);

                        positiveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String categoryName = renamedCategory.getText().toString();
                                if (Vocabulary.containsCategory(categoryName)) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.category_exists), Toast.LENGTH_SHORT).show();
                                } else {
                                    Vocabulary.categories.get(position).name = categoryName;
                                    categoriesAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
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

        categoriesAdapter = new AdapterCategoriesList(Vocabulary.categories);
        categoryList.setAdapter(categoriesAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                categoriesAdapter.onItemMove(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(v.getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_delete_category, null);
                categoryBuilder.setView(mView);
                AlertDialog dialog = categoryBuilder.create();
                TextView title = mView.findViewById(R.id.title),
                        descr = mView.findViewById(R.id.descr);

                title.setText(getResources().getString(R.string.deleting_category_title));
                descr.setText(getResources().getString(R.string.deleting_category_descr));

                Button positiveBtn = mView.findViewById(R.id.button_positive),
                        negativeBtn = mView.findViewById(R.id.button_negative);
                positiveBtn.setText(getResources().getString(R.string.delete));
                negativeBtn.setText(getResources().getString(R.string.cancel));

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoriesAdapter.onItemDismiss(viewHolder.getAdapterPosition());
                        dialog.dismiss();
                    }
                });
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoriesAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        itemTouchHelper.attachToRecyclerView(categoryList);

        FloatingActionButton fabAdd = v.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
                categoryBuilder.setView(mView);
                AlertDialog dialog = categoryBuilder.create();
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                EditText addedCategory = mView.findViewById(R.id.category_name);
                Button positiveBtn = mView.findViewById(R.id.button_positive);
                Button negativeBtn = mView.findViewById(R.id.button_negative);

                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryName = addedCategory.getText().toString();
                        if (Vocabulary.containsCategory(categoryName)) {
                            Toast.makeText(getContext(), getResources().getString(R.string.category_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            Category c = new Category();
                            c.name = categoryName;
                            Vocabulary.categories.add(c);
                            categoriesAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
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
        });

        return v;
    }
}
