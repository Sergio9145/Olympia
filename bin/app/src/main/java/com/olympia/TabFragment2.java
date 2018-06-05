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
        categoryList.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        CategoriesListAdapter adapter = new CategoriesListAdapter(categoryArrayList);
        categoryList.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getAdapterPosition(),
                        target.getAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                 int direction) {
                adapter.onItemDismiss(viewHolder.getAdapterPosition());
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

                EditText categoryText = (EditText) mView.findViewById(R.id.category_name);
                Button createCategory = (Button) mView.findViewById(R.id.button_positive);
                Button cancelDialog = (Button) mView.findViewById(R.id.button_negative);

                createCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String categoryName = categoryText.getText().toString();

                        if (categoryArrayList.contains(categoryName)) {
                            Toast.makeText(getContext(),"Category already exists",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            categoryArrayList.add(categoryName);
                            adapter.notifyDataSetChanged();
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
