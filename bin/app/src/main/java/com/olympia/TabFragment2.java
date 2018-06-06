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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class TabFragment2 extends Fragment {

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
        View v = inflater.inflate(R.layout.tab_fragment_2, container, false);

        RecyclerView categoryList = v.findViewById(R.id.categories_list);
        ArrayList<String> categoryArrayList = new ArrayList<String>(); // TODO: move to vocabulary
        categoryList.setLayoutManager(new GridLayoutManager(getContext(), 1));
        categoryList.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), categoryList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
//                        Toast.makeText(getContext(), String.format(Locale.ENGLISH,"Item's position: %d", position), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
//                        Toast.makeText(getContext(), String.format(Locale.ENGLISH,"Item's loooong: %d", position), Toast.LENGTH_SHORT).show();
                    }
                })
        );

        CategoriesListAdapter categoriesAdapter = new CategoriesListAdapter(categoryArrayList);
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
                categoriesAdapter.onItemDismiss(viewHolder.getAdapterPosition());
            }
        });

        itemTouchHelper.attachToRecyclerView(categoryList);

        FloatingActionButton fabAdd = v.findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener(){

            @Override
            public  void onClick(View view){
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(view.getContext());
                View mView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
                categoryBuilder.setView(mView);
                AlertDialog dialog = categoryBuilder.create();

                EditText categoryText = mView.findViewById(R.id.category_name);
                Button createCategory = mView.findViewById(R.id.button_positive);
                Button cancelDialog = mView.findViewById(R.id.button_negative);

                createCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryName = categoryText.getText().toString();

                        if (categoryArrayList.contains(categoryName)) {
                            Toast.makeText(getContext(),"Category already exists",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            categoryArrayList.add(categoryName);
                            categoriesAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(),"Category added",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                cancelDialog.setOnClickListener(new View.OnClickListener() {
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
