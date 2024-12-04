package com.someone.familytree.TreeMenu;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.someone.familytree.R;
import com.someone.familytree.database.FamilyDatabase;
import com.someone.familytree.database.FamilyTreeTable;

import java.util.ArrayList;
import java.util.List;

public class OfflineFragment extends Fragment {
    private final List<Item> itemList = new ArrayList<>();
    ItemAdapter itemAdapter;
    ConstraintLayout addTreeLayout;
    TreeMenuActivity treeMenuActivity;

    public OfflineFragment(TreeMenuActivity treeMenuActivity) {
        super();
        this.treeMenuActivity = treeMenuActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree_menu, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        FamilyDatabase db = FamilyDatabase.getDatabase(requireContext());

        new Thread(() -> {
            List<FamilyTreeTable> familyTreeTableList = db.familyDao().getAllTrees();

            for (FamilyTreeTable familyTreeTable : familyTreeTableList) {
                itemList.add(new Item(familyTreeTable.getTreeName(), familyTreeTable.getId()));
            }

            ImageButton addTreeButton = view.findViewById(R.id.fab);

            addTreeLayout = view.findViewById(R.id.addTreeLayout);


            new Handler(Looper.getMainLooper()).post(() -> {

                addTreeLayout.setVisibility(View.GONE);

                addTreeButton.setOnClickListener(v -> {
                    showAddTreeDialog();
                });
                itemAdapter = new ItemAdapter(itemList, treeMenuActivity);
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(requireContext()));

            });

        }).start();

        return view;
    }

    private void showAddTreeDialog() {
        if (addTreeLayout.getVisibility() == View.GONE) {
            addTreeLayout.setVisibility(View.VISIBLE);
        }

        Button cancel = addTreeLayout.findViewById(R.id.createTreeCancelButton);
        Button create = addTreeLayout.findViewById(R.id.addTreeButton);

        cancel.setOnClickListener(v -> {
            clearEditTexts();
            create.setOnClickListener(null);
            hideAddTreeDialog();
        });

        create.setOnClickListener(v -> {
            EditText treeNameET = addTreeLayout.findViewById(R.id.treeName);
            String treeName = treeNameET.getText().toString().trim();
            if (treeName.isEmpty()) {
                treeNameET.setError("Tree name cannot be empty");
                return;
            }
            FamilyDatabase db = FamilyDatabase.getDatabase(requireContext());
            FamilyTreeTable familyTreeTable = new FamilyTreeTable(treeName);
            new Thread(() -> {
                long treeId = db.familyDao().insertTree(familyTreeTable);
                Log.d("TreeMenuActivity", "Tree id: " + treeId);
                new Handler(Looper.getMainLooper()).post(() -> {
                    clearEditTexts();
                    itemList.add(new Item(familyTreeTable.getTreeName(), (int) treeId));
                    hideAddTreeDialog();
                });
            }).start();
        });


    }
    private void clearEditTexts() {
        EditText treeName = addTreeLayout.findViewById(R.id.treeName);
        EditText treeDescription = addTreeLayout.findViewById(R.id.treeDescription);
        treeName.setText("");
        treeDescription.setText("");
    }
    private void hideAddTreeDialog() {
        addTreeLayout.setVisibility(View.GONE);
        addTreeLayout.findViewById(R.id.createTreeCancelButton).setOnClickListener(null);
        addTreeLayout.findViewById(R.id.addTreeButton).setOnClickListener(null);
        addTreeLayout.findViewById(R.id.treeName).clearFocus();
        addTreeLayout.findViewById(R.id.treeDescription).clearFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(addTreeLayout.getWindowToken(), 0);
    }
}
