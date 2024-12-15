package com.someone.familytree.TreeMenu;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.someone.familytree.R;

import java.util.List;

public class TreeMenuActivity extends AppCompatActivity {
    public PrivateFragment privateFragment;
    Fragment currentFragment;
    int selected = 0;

    ConstraintLayout selectedToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_menu);

        selectedToolbarLayout = findViewById(R.id.selectedToolbarLayout);

        if(selectedToolbarLayout.getVisibility() == View.VISIBLE) {
            selectedToolbarLayout.setVisibility(View.GONE);
        }

        ImageButton overflow_menu = findViewById(R.id.default_overflow_menu);
        overflow_menu.setOnClickListener(v -> {
            Log.d("TreeMenuActivity", "Overflow menu clicked");
            PopupMenu popupMenu = new PopupMenu(TreeMenuActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_tree_menu, popupMenu.getMenu());
            popupMenu.show();
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Private");
            } else {
                tab.setText("Public");
            }
        }
        ).attach();

        viewPager.setUserInputEnabled(true);
    }

    private void switchFragment(Fragment fragment) {
        if(currentFragment != fragment) {
            currentFragment = fragment;
        }
    }

    public void itemsSelected(int size, boolean isSelectionMode, List<Integer> itemList) {
        if(size>0) {
            selected = size;
            updateSelectedToolbar(isSelectionMode, itemList);
        }else{
            hideSelectedToolbar();
        }
    }

    private void updateSelectedToolbar(boolean isSelectionMode, List<Integer> itemList) {
        Log.d("TreeMenuActivity", "Updating selected toolbar");
        if(isSelectionMode) {
            selectedToolbarLayout.setVisibility(View.VISIBLE);
            TextView selectedCount = selectedToolbarLayout.findViewById(R.id.selectedCount);
            selectedCount.setText(selected+"");
            ImageButton unselectButton = selectedToolbarLayout.findViewById(R.id.unselectButton);
            unselectButton.setOnClickListener(v -> {
                privateFragment.clearSelection();
                selectedToolbarLayout.setVisibility(View.GONE);
            });
            ImageButton deleteButton = selectedToolbarLayout.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(v -> {
                privateFragment.deleteTree(itemList);
//                itemAdapter.clearSelection();
                selectedToolbarLayout.setVisibility(View.GONE);
            });
            ImageButton selectedOverflowMenu = selectedToolbarLayout.findViewById(R.id.selectedOverflowMenu);
            selectedOverflowMenu.setOnClickListener(v -> {
                Log.d("TreeMenuActivity", "Selected overflow menu clicked");
                PopupMenu popupMenu = new PopupMenu(TreeMenuActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.selected_item_options, popupMenu.getMenu());
                if(selected == 1) {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(true);
                } else {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                }
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.edit) {
                        Log.d("TreeMenuActivity", "Edit clicked");
                        int id = itemList.get(0);
                        privateFragment.editTree(id);
                        selectedToolbarLayout.setVisibility(View.GONE);
                        return true;
                    }else if (item.getItemId() == R.id.duplicate) {
                        Log.d("TreeMenuActivity", "Delete clicked");
                        privateFragment.duplicate(itemList);
                        selectedToolbarLayout.setVisibility(View.GONE);
                        return true;
                    }
                    return false;
                });
            });
        }else{
            hideSelectedToolbar();
        }
    }

    void hideSelectedToolbar() {
        selectedToolbarLayout.setVisibility(View.GONE);
    }
}