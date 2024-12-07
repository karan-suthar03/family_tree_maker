package com.someone.familytree.Sketch.UiElements;

import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.Sketch.TreeHandler;
import com.someone.familytree.database.FamilyMember;

public class AddNewMember {
    SketchActivity sketchActivity;
    UiHandler uiHandler;
    public AddNewMember(UiHandler uiHandler) {
        this.uiHandler = uiHandler;
        this.sketchActivity = uiHandler.sketchActivity;
    }
    public void addNewParent(FamilyMember familyMember) {
        Log.d("MemberName", familyMember.getName());
        Dialog dialog = new Dialog(sketchActivity, R.style.CustomTransparentDialog);
        dialog.setContentView(R.layout.add_new_member);
        Button createMember = dialog.findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            EditText name = dialog.findViewById(R.id.editTextName);
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
                    FamilyMember updatedFamilyMember = familyDatabase.familyDao().getMember(familyMember.getId());
                    TreeHandler.refreshTree();
                    sketchActivity.runOnUiThread(() -> {
                        uiHandler.personDetails.updatePersonalDetails(updatedFamilyMember);
                    });
                    dialog.dismiss();
                });
                thread.start();
            }
        });

        dialog.show();
    }

    public void addNewChild(int id) {
        Dialog dialog = new Dialog(sketchActivity, R.style.CustomTransparentDialog);
        dialog.setContentView(R.layout.add_new_member);
        Button createMember = dialog.findViewById(R.id.buttonSubmit);
        createMember.setOnClickListener(v -> {
            TextView name = dialog.findViewById(R.id.editTextName);
            String memberName = name.getText().toString();
            if(memberName.isEmpty()){
                name.setError("Name is required");
            }else{
                Log.d("MemberName", memberName);
                Thread thread = new Thread(() -> {
                    FamilyMember familyMember = new FamilyMember(memberName, id, sketchActivity.treeId);
                    familyDatabase.familyDao().insertMember(familyMember);
                    FamilyMember originalFamilyMember = familyDatabase.familyDao().getMember(id);
                    TreeHandler.refreshTree();
                    sketchActivity.runOnUiThread(() -> {
                        if(sketchActivity.fab.getVisibility() == View.VISIBLE){
                            sketchActivity.fab.setVisibility(View.GONE);
                        }
                        uiHandler.personDetails.updatePersonalDetails(originalFamilyMember);
                    });
                });
                dialog.dismiss();
                thread.start();
            }
        });
        dialog.show();
    }
}
