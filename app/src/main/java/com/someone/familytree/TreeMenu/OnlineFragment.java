package com.someone.familytree.TreeMenu;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.someone.familytree.R;

import java.util.ArrayList;
import java.util.List;

public class OnlineFragment extends Fragment {
    private RecyclerView recyclerView;
    private final List<Item> itemList = new ArrayList<>();
    TreeMenuActivity treeMenuActivity;

    public OnlineFragment(TreeMenuActivity treeMenuActivity) {
        super();
        this.treeMenuActivity = treeMenuActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree_menu, container, false);

//        String[] items = {"Online Item 1", "Online Item 2", "Online Item 3", "Online Item 4", "Online Item 5"};
//        for (String item : items) {
//            itemList.add(new Item(item, 0));
//        }
//
//        recyclerView = view.findViewById(R.id.recyclerView);
//        itemAdapter = new ItemAdapter(itemList);
//        recyclerView.setAdapter(itemAdapter);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        return view;
    }
}
