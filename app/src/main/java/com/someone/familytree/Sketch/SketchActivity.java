package com.someone.familytree.Sketch;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.someone.familytree.R;
import com.someone.familytree.SingleMemberWI;
import com.someone.familytree.Sketch.UiElements.UiHandler;
import com.someone.familytree.database.DatabaseManager;
import com.someone.familytree.database.FamilyMember;

import java.util.List;

import processing.android.PFragment;

public class SketchActivity extends AppCompatActivity {

    private SingleMemberWI rootWI;
    public Sketch sketch;
    PFragment fragment;
    public ExtendedFloatingActionButton fab;

    public int treeId;

    public UiHandler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sketch);

        treeId = getIntent().getIntExtra("treeId", -1);

        if (treeId == -1) {
            throw new IllegalArgumentException("TreeId is required");
        }
        fab = findViewById(R.id.addNewMember);
        uiHandler = new UiHandler(this);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (sketch != null) {
                    sketch.exit(); // Stop or dispose of the sketch
                }
                if (fragment != null) {
                    fragment.dispose(); // Dispose of any resources related to the fragment
                }
                finish();
            }
        };


        getOnBackPressedDispatcher().addCallback(this, callback);
        // Find the container for the sketch
        FrameLayout canvasContainer = findViewById(R.id.sketchCanvas);

        // Wait for the layout to be ready
        canvasContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener after the layout is ready
                canvasContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                fab.setVisibility(View.GONE);

                // Start the background thread to fetch data and update the sketch
                Thread thread = new Thread(() -> {
                    loadFamilyData();
                    sketch = new Sketch(canvasContainer.getWidth(), canvasContainer.getHeight(), rootWI, SketchActivity.this);

                    fragment = new PFragment(sketch);
                    fragment.setView(canvasContainer, SketchActivity.this);
                });
                thread.start();
            }
        });
    }

    private void loadFamilyData() {
        List<FamilyMember> familyMembers = DatabaseManager.getChildren(0, treeId);
        FamilyMember familyMember;
        if (!familyMembers.isEmpty()) {
            familyMember = familyMembers.get(0);
            rootWI = new SingleMemberWI(familyMember.getName(), familyMember.getId(), treeId);
            convertToSingleMemberWI(rootWI, rootWI.id);
        }
    }

    public void showAddMemberOption() {
        runOnUiThread(() -> {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> uiHandler.addNewMember.addNewChild(0));
        });
    }

    private void convertToSingleMemberWI(SingleMemberWI parent, int id) {
        for (FamilyMember member : DatabaseManager.getChildren(id, treeId)) {
            SingleMemberWI child = new SingleMemberWI(member.getName(), member.getId(), treeId);
            parent.addChildren(child);

            convertToSingleMemberWI(child, member.getId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up the Processing sketch and fragment
        if (sketch != null) {
            sketch.exit(); // Make sure Processing's PApplet is exited properly
        }
        if (fragment != null) {
            fragment.dispose(); // Clean up the fragment
        }
    }
}
