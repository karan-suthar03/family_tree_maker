package com.someone.familytree.TreeMenu;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.someone.familytree.R;
import com.someone.familytree.SketchActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> itemList;
    private Set<Integer> selectedPositions = new HashSet<>();  // Track selected items
    private boolean isSelectionMode = false;  // Track if selection mode is active
    TreeMenuActivity treeMenuActivity;

    public ItemAdapter(List<Item> itemList, TreeMenuActivity treeMenuActivity) {
        this.itemList = itemList;
        this.treeMenuActivity = treeMenuActivity;
        this.treeMenuActivity.itemAdapter = this;
    }

    // ViewHolder class to represent each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_text);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_tree_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item currentItem = itemList.get(position);
        holder.textView.setText(currentItem.getTreeName());

        // Change background color if selected
        if (selectedPositions.contains(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Handle click
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleSelection(position);  // Toggle item selection
            } else {
                // Normal click action
                Intent intent = new Intent(v.getContext(), SketchActivity.class);
                intent.putExtra("treeId", currentItem.getTreeId());
                v.getContext().startActivity(intent);
            }
        });

        // Handle long press to activate selection mode
        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                toggleSelection(position);
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.tree_item_options, popupMenu.getMenu());
                popupMenu.show();
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // Toggle selection state
    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }
        if (selectedPositions.isEmpty()) {
            isSelectionMode = false;
        }

        notifyDataSetChanged();  // Refresh RecyclerView
    }
}
