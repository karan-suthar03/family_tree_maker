package com.someone.familytree.Sketch.UiElements;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.Sketch.TreeHandler;
import com.someone.familytree.database.FamilyMember;

public class AddNewMember {
    public ConstraintLayout createNewMemberLayout;
    SketchActivity sketchActivity;
    UiHandler uiHandler;
    public AddNewMember(UiHandler uiHandler) {
        this.uiHandler = uiHandler;
        this.sketchActivity = uiHandler.sketchActivity;
        createNewMemberLayout = sketchActivity.findViewById(R.id.createMemberLayout);
        createNewMemberLayout.setVisibility(View.GONE);
    }

    public void hide() {
        sketchActivity.runOnUiThread(() -> createNewMemberLayout.setVisibility(View.GONE));
    }

    private void addNewParent(FamilyMember familyMember) {
        if(createNewMemberLayout.getVisibility() == View.VISIBLE){
            hide();
        }else{
            createNewMemberLayout.setVisibility(View.VISIBLE);
        }
        Button createMember = createNewMemberLayout.findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            TextView name = createNewMemberLayout.findViewById(R.id.editTextName);
            String memberName = name.getText().toString();
            Log.d("MemberName", familyMember.getId() + "");
            if(memberName.isEmpty()){
                name.setError("Name is required");
            }else{
                Log.d("MemberName", memberName);
                Thread thread = new Thread(() -> {
                    int parentId = 0;
                    FamilyMember newParent = new FamilyMember(memberName, parentId, sketchActivity.treeId);
                    int newParentId = (int) familyDatabase.familyDao().insertMember(newParent);
                    familyDatabase.familyDao().updateParentId(familyMember.getId(), newParentId, sketchActivity.treeId);
                    TreeHandler.refreshTree();
                    sketchActivity.runOnUiThread(() -> {
                        name.setText("");
                        hide();
                    });
                });
                thread.start();
            }
        });
    }

    public void addNewChild(int id) {
        if(createNewMemberLayout.getVisibility() == View.VISIBLE){
            hideNewMemberLayout();
        }else{
            createNewMemberLayout.setVisibility(View.VISIBLE);
        }
        Button createMember = sketchActivity.findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            TextView name = sketchActivity.findViewById(R.id.editTextName);
            String memberName = name.getText().toString();
            if(memberName.isEmpty()){
                name.setError("Name is required");
            }else{
                Log.d("MemberName", memberName);
                Thread thread = new Thread(() -> {
                    FamilyMember familyMember = new FamilyMember(memberName, id, sketchActivity.treeId);
                    familyDatabase.familyDao().insertMember(familyMember);
                    TreeHandler.refreshTree();
                    sketchActivity.runOnUiThread(() -> {
                        if(sketchActivity.fab.getVisibility() == View.VISIBLE){
                            sketchActivity.fab.setVisibility(View.GONE);
                        }
                        name.setText("");
                        hideNewMemberLayout();
                        if(uiHandler.personDetails.personDetailsContainer.getVisibility() == View.VISIBLE){
                            uiHandler.personDetails.updateChildrensList(id);
                        }
                    });
                });
                thread.start();
            }
        });
    }
    public void hideNewMemberLayout() {
        createNewMemberLayout.setVisibility(View.GONE);
        createNewMemberLayout.findViewById(R.id.editTextName).clearFocus();
        InputMethodManager imm = (InputMethodManager) sketchActivity.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(createNewMemberLayout.getWindowToken(), 0);
    }

}
