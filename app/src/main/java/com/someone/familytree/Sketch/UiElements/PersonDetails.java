package com.someone.familytree.Sketch.UiElements;

import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.database.FamilyMember;

import java.util.List;

public class PersonDetails {

    UiHandler uiHandler;
    SketchActivity sketchActivity;
    public ConstraintLayout personDetailsContainer;
    View personDetailsLayout;
    public PersonDetails(UiHandler uiHandler) {
        this.uiHandler = uiHandler;
        this.sketchActivity = uiHandler.sketchActivity;
        personDetailsContainer = sketchActivity.findViewById(R.id.personDetailContainer);
        LayoutInflater inflater = sketchActivity.getLayoutInflater();

        personDetailsLayout = inflater.inflate(R.layout.person_details, personDetailsContainer, false);
        ImageButton closeDetails = personDetailsLayout.findViewById(R.id.backButton);
        closeDetails.setOnClickListener(v -> personDetailsContainer.setVisibility(View.GONE));
        personDetailsContainer.addView(personDetailsLayout);
        personDetailsContainer.setVisibility(View.GONE);

    }

    public void hidePersonDetails() {
        sketchActivity.runOnUiThread(() -> personDetailsContainer.setVisibility(View.GONE));
    }

    public void showPersonDetails(FamilyMember familyMember) {
        LayoutInflater inflater = sketchActivity.getLayoutInflater();
        View personDetailFeild = inflater.inflate(R.layout.person_detail_feilds, (ViewGroup) personDetailsLayout, false);
        TextView heading = personDetailFeild.findViewById(R.id.FieldDetailHeading);
        heading.setText("Name:");
        TextView value = personDetailFeild.findViewById(R.id.FieldDetail);
        value.setText(familyMember.getName());
        LinearLayout personDetailList = personDetailsLayout.findViewById(R.id.PersonDetailsListLayout);
        personDetailList.removeAllViews();
        personDetailList.addView(personDetailFeild);
        Thread thread = new Thread(() -> {
            if(familyMember.getParentId() != 0){
                FamilyMember parent = familyDatabase.familyDao().getMember(familyMember.getParentId());
                sketchActivity.runOnUiThread(()->{
                    View parentDetailFeild = inflater.inflate(R.layout.person_detail_feilds, (ViewGroup) personDetailsLayout, false);
                    TextView parentHeading = parentDetailFeild.findViewById(R.id.FieldDetailHeading);
                    parentHeading.setText("Parent:");
                    TextView parentValue = parentDetailFeild.findViewById(R.id.FieldDetail);
                    parentValue.setText(parent.getName());
                    personDetailList.addView(parentDetailFeild);
                });
            }
        });
        thread.start();
        View personChildrenLayout = personDetailsLayout.findViewById(R.id.PersonChildrenLayout);
        updateChildrensList(familyMember.getId());
        ImageButton addChildButton = personChildrenLayout.findViewById(R.id.addChildren);
        addChildButton.setOnClickListener((v)->{
            uiHandler.addNewMember.addNewChild(familyMember.getId());
        });
        personDetailsContainer.setVisibility(View.VISIBLE);
    }


    public void updateChildrensList(int id) {
        LayoutInflater inflater = sketchActivity.getLayoutInflater();
        View personChildrenLayout = personDetailsLayout.findViewById(R.id.PersonChildrenLayout);
        LinearLayout childrenList = personChildrenLayout.findViewById(R.id.PersonChildrenListLayout);
        Thread thread1 = new Thread(() -> {
            List<FamilyMember> children = familyDatabase.familyDao().getChildren(id, sketchActivity.treeId);
            sketchActivity.runOnUiThread(()->{
                if(!children.isEmpty()){
                    childrenList.removeAllViews();
                    for(FamilyMember child: children){
                        View childDetailFeild = inflater.inflate(R.layout.child_detail_item, (ViewGroup) personDetailsLayout, false);
                        TextView childHeading = childDetailFeild.findViewById(R.id.ChildName);
                        childHeading.setText(child.getName());
                        childrenList.addView(childDetailFeild);
                    }
                }else{
                    childrenList.removeAllViews();
                    TextView noChildren = new TextView(sketchActivity);
                    noChildren.setText("No Children");
                    childrenList.addView(noChildren);
                }
            });
        });
        thread1.start();
    }

}
