package com.someone.familytree.TreeMenu;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.database.FamilyDatabase;
import com.someone.familytree.database.FamilyMember;
import com.someone.familytree.database.FamilyTreeTable;

import java.util.ArrayList;
import java.util.List;

public class OfflineFragment extends Fragment {
    private final List<Item> itemList = new ArrayList<>();
    ConstraintLayout addTreeLayout;
    TreeMenuActivity treeMenuActivity;
    ConstraintLayout editTreeLayout;
    public List<Integer> selectedItems = new ArrayList<>();
    FamilyDatabase familyDatabase;
    LinearLayout listOfTrees;
    View view;

    public OfflineFragment(TreeMenuActivity treeMenuActivity) {
        super();
        this.treeMenuActivity = treeMenuActivity;
        this.treeMenuActivity.offlineFragment = this;
    }

    public OfflineFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tree_menu, container, false);
        this.view = view;
        listOfTrees = view.findViewById(R.id.listOfTrees);
        familyDatabase = FamilyDatabase.getDatabase(requireContext());

        generateList();

        return view;
    }

    private void generateList() {
        new Thread(() -> {
            List<FamilyTreeTable> familyTreeTableList = familyDatabase.familyDao().getAllTrees();

            for (FamilyTreeTable familyTreeTable : familyTreeTableList) {
                itemList.add(new Item(familyTreeTable.getTreeName(), familyTreeTable.getId()));
            }

            ImageButton addTreeButton = view.findViewById(R.id.fab);

            addTreeLayout = view.findViewById(R.id.addTreeLayout);
            editTreeLayout = view.findViewById(R.id.editTreeLayout);


            new Handler(Looper.getMainLooper()).post(() -> {
                addTreeLayout.setVisibility(View.GONE);
                editTreeLayout.setVisibility(View.GONE);

                addTreeButton.setOnClickListener(v -> {
                    showAddTreeDialog();
                });

                LayoutInflater inflater1 = LayoutInflater.from(requireContext());
                for (Item item : itemList) {
                    if (item == null) {
                        continue;
                    }
                    View itemView = inflater1.inflate(R.layout.menu_tree_item, listOfTrees, false);
                    CheckBox checkBox = itemView.findViewById(R.id.checkBox);
                    TextView textView = itemView.findViewById(R.id.item_text);
                    Log.d("TreeMenuActivity", "Adding item: " + item.getTreeName());
                    textView.setText(item.getTreeName());
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        Log.d("TreeMenuActivity", "Checkbox checked: " + isChecked);
                        if (isChecked) {
                            itemView.setBackground(ContextCompat.getDrawable(requireContext(), R.color.selectedItem));
                            selectedItems.add(item.getTreeId());
                            itemSelected(checkBox);
                            treeMenuActivity.itemsSelected(selectedItems.size(), !selectedItems.isEmpty(), selectedItems);
                        } else {
                            itemView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ripple_effect));
                            selectedItems.remove((Integer) item.getTreeId());
                            itemUnselected(checkBox);
                            treeMenuActivity.itemsSelected(selectedItems.size(), !selectedItems.isEmpty(), selectedItems);
                        }
                    });
                    itemView.setOnClickListener(v -> {
                        if(!selectedItems.isEmpty()) {
                            checkBox.performClick();
                        }else{
                            Intent intent = new Intent(requireContext(), SketchActivity.class);
                            intent.putExtra("treeId", item.getTreeId());
                            startActivity(intent);
                        }
                    });
                    itemView.setOnLongClickListener(v -> {
                        checkBox.performClick();
                        return true;
                    });
                    listOfTrees.addView(itemView);
                }
            });
        }).start();
    }

    public void itemSelected(CheckBox checkBox) {
        // Ensure that the CheckBox is measured
        checkBox.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int targetWidth = checkBox.getMeasuredWidth();
        int startWidth = checkBox.getWidth();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, targetWidth);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = checkBox.getLayoutParams();
            layoutParams.width = value;
            checkBox.setLayoutParams(layoutParams);  // Correct way to update layout params
        });
        widthAnimator.setDuration(400);
        widthAnimator.start();

        float targetScale = 1f;
        float startScale = checkBox.getScaleX();
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(startScale, targetScale);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());
        scaleAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            checkBox.setScaleX(value);
            checkBox.setScaleY(value);
        });
        scaleAnimator.setDuration(400);
        scaleAnimator.start();
    }

    public void itemUnselected(CheckBox checkBox) {
        // Ensure that the CheckBox is measured
        checkBox.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int targetWidth = 0;
        int startWidth = checkBox.getWidth();
        ValueAnimator widthAnimator = ValueAnimator.ofInt(startWidth, targetWidth);
        widthAnimator.setInterpolator(new DecelerateInterpolator());
        widthAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = checkBox.getLayoutParams();
            layoutParams.width = value;
            checkBox.setLayoutParams(layoutParams);  // Correct way to update layout params
        });
        widthAnimator.setDuration(400);
        widthAnimator.start();

        float targetScale = 0.5f;
        float startScale = checkBox.getScaleX();
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(startScale, targetScale);
        scaleAnimator.setInterpolator(new DecelerateInterpolator());
        scaleAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            checkBox.setScaleX(value);
            checkBox.setScaleY(value);
        });
        scaleAnimator.setDuration(400);
        scaleAnimator.start();
    }

    private void showAddTreeDialog() {
        if (addTreeLayout.getVisibility() == View.GONE) {
            addTreeLayout.setVisibility(View.VISIBLE);
        }

        Button cancel = addTreeLayout.findViewById(R.id.createTreeCancelButton);
        Button create = addTreeLayout.findViewById(R.id.addTreeButton);

        cancel.setOnClickListener(v -> {
            clearAddEditTexts();
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
            FamilyTreeTable familyTreeTable = new FamilyTreeTable(treeName);
            new Thread(() -> {
                long treeId = familyDatabase.familyDao().insertTree(familyTreeTable);
                Log.d("TreeMenuActivity", "Tree id: " + treeId);
                new Handler(Looper.getMainLooper()).post(() -> {
                    clearAddEditTexts();
                    itemList.add(new Item(familyTreeTable.getTreeName(), (int) treeId));
                    refreshList();
                    hideAddTreeDialog();
                });
            }).start();
        });


    }
    private void clearAddEditTexts() {
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

    public void deleteTree(List<Integer> ids) {
        new Thread(() -> {
            for(Integer id : ids){
                familyDatabase.familyDao().deleteTree(id);
                familyDatabase.familyDao().deleteAll(id);
            }
            new Handler(Looper.getMainLooper()).post(this::refreshList);
        }).start();
    }

    public void refreshList() {
        listOfTrees.removeAllViews();
        selectedItems.clear();
        itemList.clear();
        generateList();
    }

    public void editTree(int id) {
        if (editTreeLayout.getVisibility() == View.GONE) {
            editTreeLayout.setVisibility(View.VISIBLE);
        }

        Button cancel = editTreeLayout.findViewById(R.id.editTreeCancelButton);
        Button save = editTreeLayout.findViewById(R.id.editTreeButton);

        EditText treeNameET = editTreeLayout.findViewById(R.id.editTreeName);
        EditText treeDescriptionET = editTreeLayout.findViewById(R.id.editTreeDescription);

        new Thread(() -> {
            String treeName = familyDatabase.familyDao().getTreeName(id);
            new Handler(Looper.getMainLooper()).post(() -> {
                treeNameET.setText(treeName);
//                treeDescriptionET.setText(familyTreeTable.getTreeDescription());
            });
        }).start();

        cancel.setOnClickListener(v -> {
            clearEditTexts();
            save.setOnClickListener(null);
            hideEditTreeDialog();
        });

        save.setOnClickListener(v -> {
            String treeName = treeNameET.getText().toString().trim();
            String treeDescription = treeDescriptionET.getText().toString().trim();
            if (treeName.isEmpty()) {
                treeNameET.setError("Tree name cannot be empty");
                return;
            }
            FamilyTreeTable familyTreeTable = new FamilyTreeTable(treeName);
            familyTreeTable.setId(id);
            new Thread(() -> {
                familyDatabase.familyDao().updateTree(familyTreeTable);
                new Handler(Looper.getMainLooper()).post(() -> {
                    clearEditTexts();
                    hideEditTreeDialog();
                    for (Item item : itemList) {
                        if (item.getTreeId() == id) {
                            item.setTreeName(familyTreeTable.getTreeName());
                        }
                    }
                    refreshList();
                });
            }).start();
        });
    }

    private void clearEditTexts() {
        EditText treeName = editTreeLayout.findViewById(R.id.editTreeName);
        EditText treeDescription = editTreeLayout.findViewById(R.id.editTreeDescription);
        treeName.setText("");
        treeDescription.setText("");
    }

    private void hideEditTreeDialog() {
        editTreeLayout.setVisibility(View.GONE);
        editTreeLayout.findViewById(R.id.editTreeCancelButton).setOnClickListener(null);
        editTreeLayout.findViewById(R.id.editTreeButton).setOnClickListener(null);
        editTreeLayout.findViewById(R.id.editTreeName).clearFocus();
        editTreeLayout.findViewById(R.id.editTreeDescription).clearFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTreeLayout.getWindowToken(),0);
    }

    public void clearSelection() {
        selectedItems.clear();
        for (int i = 0; i < listOfTrees.getChildCount(); i++) {
            View itemView = listOfTrees.getChildAt(i);
            CheckBox checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setChecked(false);
        }
        treeMenuActivity.itemsSelected(0, false, selectedItems);
    }

    class SingleMember{
        String Name;
        List<SingleMember> children;
        SingleMember(String name){
            Name = name;
            children = new ArrayList<>();
        }
        void addChildren(SingleMember child){
            children.add(child);
        }
    }

    public void duplicate(List<Integer> ids) {
        new Thread(() -> {
            for (Integer id : ids) {
                String newName = familyDatabase.familyDao().getTreeName(id) + " (Copy)";
                newName = checkDuplicateName(newName);
                FamilyTreeTable familyTreeTable = new FamilyTreeTable(newName);
                int treeId = (int) familyDatabase.familyDao().insertTree(familyTreeTable);
                List<FamilyMember> familyMembers = familyDatabase.familyDao().getChildren(0, id);
                if (!familyMembers.isEmpty()) {
                    FamilyMember familyMember = familyMembers.get(0);
                    SingleMember root = new SingleMember(familyMember.getName());
                    convertToSingleMember(root, familyMember.getId(), id);
                    duplicateMembers(root, 0, treeId);
                }
            }
            new Handler(Looper.getMainLooper()).post(this::refreshList);
        }).start();
    }

    private String checkDuplicateName(String newName) {
        List<FamilyTreeTable> familyTreeTables = familyDatabase.familyDao().getAllTrees();
        int i = 1;
        String tempName = newName;
        while (true) {
            boolean found = false;
            for (FamilyTreeTable familyTreeTable : familyTreeTables) {
                if (familyTreeTable.getTreeName().equals(tempName)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
            tempName = newName + " (" + i + ")";
            i++;
        }
        return tempName;
    }

    private void duplicateMembers(SingleMember root, int i, int treeId) {
        FamilyMember familyMember = new FamilyMember(root.Name, i, treeId);
        long id = familyDatabase.familyDao().insertMember(familyMember);
        for (SingleMember child : root.children) {
            duplicateMembers(child, (int) id, treeId);
        }
    }

    private void printAll(SingleMember root) {
        Log.d("TreeMenuActivity", root.Name);
        for (SingleMember child : root.children) {
            printAll(child);
        }
    }

    private void convertToSingleMember(SingleMember root, int id, int treeId) {
        for (FamilyMember member : familyDatabase.familyDao().getChildren(id, treeId)) {
            SingleMember child = new SingleMember(member.getName());
            root.addChildren(child);
            convertToSingleMember(child, member.getId(), treeId);
        }
    }
}
