package com.someone.familytree;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.List;

import processing.android.PFragment;

public class SketchActivity extends AppCompatActivity {

    private SingleMemberWI rootWI;
    FamilyDatabase familyDatabase;
    private Sketch sketch;
    PFragment fragment;

    int detailViewHeight;
    int detailViewWidth;

    ExtendedFloatingActionButton fab;

    CardView detailView;
    ConstraintLayout createNewMemberLayout;
    ConstraintLayout personCardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sketch);

        detailView = findViewById(R.id.personCard);

        fab = findViewById(R.id.addNewMember);
        personCardContainer = findViewById(R.id.personCardLayout);

        createNewMemberLayout = findViewById(R.id.createMemberLayout);

        findViewById(R.id.createMemberCancelButton).setOnClickListener(view -> hideNewMemberLayout());

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled */) {
            @Override
            public void handleOnBackPressed() {
                if(createNewMemberLayout.getVisibility() == View.VISIBLE){
                    hideNewMemberLayout();
                }else {
                    finish();
                }
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

                // Initialize the family database and the sketch
                familyDatabase = FamilyDatabase.getDatabase(SketchActivity.this);
                TreeHandler.familyDatabase = familyDatabase;

                createNewMemberLayout.setVisibility(View.GONE);

                fab.setVisibility(View.GONE);

                detailViewWidth = detailView.getWidth();
                detailViewHeight = detailView.getHeight();

                personCardContainer.setVisibility(View.GONE);

                // Start the background thread to fetch data and update the sketch
                Thread thread = new Thread(() -> {
                    loadFamilyData();
                    sketch = new Sketch(canvasContainer.getWidth(), canvasContainer.getHeight(), rootWI,SketchActivity.this);

                    fragment = new PFragment(sketch);
                    fragment.setView(canvasContainer, SketchActivity.this);
                });
                thread.start();
            }
        });
    }
    private void loadFamilyData() {

        List<FamilyMember> familyMembers = familyDatabase.familyMemberDao().getChildren(0);
        FamilyMember familyMember;
        if (!familyMembers.isEmpty()) {
            familyMember = familyMembers.get(0);
            rootWI = new SingleMemberWI(familyMember.getName(), familyMember.getId());
            convertToSingleMemberWI(rootWI, rootWI.id);
        }


    }

    public void showAddMemberOption() {
        runOnUiThread(() -> {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                addNewChild(0);
            });
        });
    }

    private void convertToSingleMemberWI(SingleMemberWI parent, int id) {
        for (FamilyMember member : familyDatabase.familyMemberDao().getChildren(id)) {
            SingleMemberWI child = new SingleMemberWI(member.getName(), member.getId());
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

    public void showPersonCard(float x, float y, int id, int nodeHeight) {
        FamilyMember familyMember = familyDatabase.familyMemberDao().getMember(id);
        if(x + (float) detailViewWidth /2 > personCardContainer.getWidth()){
            x = personCardContainer.getWidth() - (float) detailViewWidth /2;
        }
        if (x - (float) detailViewWidth / 2 < 0) {
            x = (float) detailViewWidth / 2;
        }
        float finalX = x;

        if(y + detailViewHeight + (float) nodeHeight /2 > personCardContainer.getHeight()){
            y = y - detailViewHeight - (float) nodeHeight;
        }
        if (y < 0) {
            y = 0;
        }
        float finalY = y;
        runOnUiThread(() -> {
            personCardContainer.setVisibility(View.VISIBLE);
            detailView.setX(finalX - (float) detailViewWidth / 2);
            detailView.setY(finalY + (float) nodeHeight /2 + 50);
            TextView name = findViewById(R.id.personName);
            name.setText(familyMember.getName());
            TextView DOB = findViewById(R.id.personDob);
            DOB.setText("Unknown");

            Button addParent = detailView.findViewById(R.id.buttonAddParent);
            if(familyMember.getParentId() == 0){
                addParent.setVisibility(View.VISIBLE);
                addParent.setOnClickListener(v -> {
                    addNewParent(familyMember);
                    personCardContainer.setVisibility(View.GONE);
                });
            }else{
                addParent.setVisibility(View.GONE);
            }

            Button addChild = detailView.findViewById(R.id.buttonAddChild);
            addChild.setOnClickListener(v -> {
                addNewChild(id);
                personCardContainer.setVisibility(View.GONE);
            });
        });
    }

    private void addNewParent(FamilyMember familyMember) {
        if(createNewMemberLayout.getVisibility() == View.VISIBLE){
            hideNewMemberLayout();
        }else{
            createNewMemberLayout.setVisibility(View.VISIBLE);
        }
        Button createMember = findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            TextView name = findViewById(R.id.editTextName);
            String memberName = name.getText().toString();
            Log.d("MemberName", familyMember.getId() + "");
            if(memberName.isEmpty()){
                name.setError("Name is required");
            }else{
                Log.d("MemberName", memberName);
                Thread thread = new Thread(() -> {
                    int parentId = 0;
                    FamilyMember newParent = new FamilyMember(memberName, parentId);
                    int newParentId = (int) familyDatabase.familyMemberDao().insert(newParent);
                    familyDatabase.familyMemberDao().updateParentId(familyMember.getId(), newParentId);
                    TreeHandler.refreshTree();
                    if(fab.getVisibility() == View.VISIBLE){
                        runOnUiThread(() -> fab.setVisibility(View.GONE));
                    }
                });
                thread.start();
                name.setText("");
                hideNewMemberLayout();
            }
        });
    }

    private void addNewChild(int id) {
        if(createNewMemberLayout.getVisibility() == View.VISIBLE){
            hideNewMemberLayout();
        }else{
            createNewMemberLayout.setVisibility(View.VISIBLE);
        }
        Button createMember = findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            TextView name = findViewById(R.id.editTextName);
            String memberName = name.getText().toString();

            if(memberName.isEmpty()){
                name.setError("Name is required");
            }else{
                Log.d("MemberName", memberName);
                Thread thread = new Thread(() -> {
                    FamilyMember familyMember = new FamilyMember(memberName, id);
                    familyDatabase.familyMemberDao().insert(familyMember);
                    TreeHandler.refreshTree();
                    if(fab.getVisibility() == View.VISIBLE){
                        runOnUiThread(() -> fab.setVisibility(View.GONE));
                    }
                });
                thread.start();
                name.setText("");
                hideNewMemberLayout();
            }
        });
    }

    private void hideNewMemberLayout() {
        createNewMemberLayout.setVisibility(View.GONE);
        createNewMemberLayout.findViewById(R.id.editTextName).clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(createNewMemberLayout.getWindowToken(), 0);
    }


    public void hidePersonCard() {
        runOnUiThread(() -> personCardContainer.setVisibility(View.GONE));
    }
}
