package com.someone.familytree.Sketch.UiElements;

import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.database.FamilyMember;
import com.someone.familytree.database.MemberDetails;

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
        View personDetailField = inflater.inflate(R.layout.person_detail_feilds, (ViewGroup) personDetailsLayout, false);
        TextView heading = personDetailField.findViewById(R.id.FieldDetailHeading);
        heading.setText("Name:");
        TextView value = personDetailField.findViewById(R.id.FieldDetail);
        value.setText(familyMember.getName());
        LinearLayout personDetailList = personDetailsLayout.findViewById(R.id.PersonDetailsListLayout);
        personDetailList.removeAllViews();
        personDetailList.addView(personDetailField);
        Log.d("ParentId", familyMember.getParentId() + "");
        Thread thread = new Thread(() -> {
            if(familyMember.getParentId() != 0){
                FamilyMember parent = familyDatabase.familyDao().getMember(familyMember.getParentId());
                sketchActivity.runOnUiThread(()->{
                    View parentDetailField = inflater.inflate(R.layout.person_detail_feilds, (ViewGroup) personDetailsLayout, false);
                    TextView parentHeading = parentDetailField.findViewById(R.id.FieldDetailHeading);
                    parentHeading.setText("Parent:");
                    TextView parentValue = parentDetailField.findViewById(R.id.FieldDetail);
                    parentValue.setText(parent.getName());
                    personDetailList.addView(parentDetailField);
                });
            }else {
                Log.d("ParentId", "No Parent");
                sketchActivity.runOnUiThread(()->{
                    View addParentField = inflater.inflate(R.layout.add_parent_detail_item, (ViewGroup) personDetailsLayout, false);
                    ImageButton addParentButton = addParentField.findViewById(R.id.addParent);
                    personDetailList.addView(addParentField);
                    addParentButton.setOnClickListener((v)-> uiHandler.addNewMember.addNewParent(familyMember));
                });
            }
            List<MemberDetails> memberDetails = familyDatabase.familyDao().getMemberDetails(familyMember.getId(), sketchActivity.treeId);
            for(MemberDetails memberDetail: memberDetails){
                sketchActivity.runOnUiThread(()->{
                    Log.d("Detail", memberDetail.getDetailName() + " " + memberDetail.getDetailValue());
                    View detailField = inflater.inflate(R.layout.person_detail_feilds, (ViewGroup) personDetailsLayout, false);
                    TextView detailHeading = detailField.findViewById(R.id.FieldDetailHeading);
                    detailHeading.setText(memberDetail.getDetailName() + ":");
                    TextView detailValue = detailField.findViewById(R.id.FieldDetail);
                    detailValue.setText(memberDetail.getDetailValue());
                    personDetailList.addView(detailField);
                });
            }
        });
        thread.start();
        View personChildrenLayout = personDetailsLayout.findViewById(R.id.PersonChildrenLayout);
        updateChildrenList(familyMember.getId());
        ImageButton addChildButton = personChildrenLayout.findViewById(R.id.addChildren);
        addChildButton.setOnClickListener((v)->{
            uiHandler.addNewMember.addNewChild(familyMember.getId());
        });
        personDetailsContainer.setVisibility(View.VISIBLE);

        ImageButton addDetailButton = personDetailsLayout.findViewById(R.id.addDetails);
        addDetailButton.setOnClickListener((v)-> addNewDetail(familyMember.getId()));
    }

    private void addNewDetail(int id) {
        Dialog dialog = new Dialog(sketchActivity, R.style.CustomTransparentDialog);
        dialog.setContentView(R.layout.add_detail_type);
        Button addDetail = dialog.findViewById(R.id.next_button);
        RadioGroup radioGroup = dialog.findViewById(R.id.options_group);
        Thread thread = new Thread(() -> {
            List<MemberDetails> memberDetails = familyDatabase.familyDao().getMemberDetails(id, sketchActivity.treeId);
            for (MemberDetails memberDetail : memberDetails) {
                if (memberDetail.getDetailType() == MemberDetails.DOB) {
                    radioGroup.findViewById(R.id.option_dob).setVisibility(View.GONE);
                }
                if (memberDetail.getDetailType() == MemberDetails.DOD) {
                    radioGroup.findViewById(R.id.option_dod).setVisibility(View.GONE);
                }
                if (memberDetail.getDetailType() == MemberDetails.MOBILE) {
                    radioGroup.findViewById(R.id.option_mobile).setVisibility(View.GONE);
                }
                if (memberDetail.getDetailType() == MemberDetails.CURRENT_AGE) {
                    radioGroup.findViewById(R.id.option_age).setVisibility(View.GONE);
                }
                if (memberDetail.getDetailType() == MemberDetails.LOCATION){
                    radioGroup.findViewById(R.id.option_location).setVisibility(View.GONE);
                }
            }
            int visibleCount = 0;
            View lastVisibleView = null;

            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child.getVisibility() == View.VISIBLE) {
                    visibleCount++;
                    lastVisibleView = child;
                }
            }

            if (visibleCount == 1 && lastVisibleView.getId() == R.id.option_custom) {
                sketchActivity.runOnUiThread(() -> {
                    dialog.dismiss();
                    addNewDetailWI(id, MemberDetails.CUSTOM_DETAIL);
                });
                return;
            }

            sketchActivity.runOnUiThread(() -> {
                addDetail.setOnClickListener(v -> {
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    if (selectedId == R.id.option_mobile) {
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.MOBILE);
                    } else if (selectedId == R.id.option_age) {
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.CURRENT_AGE);
                    } else if (selectedId == R.id.option_dob) {
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.DOB);
                    } else if (selectedId == R.id.option_dod) {
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.DOD);
                    }else if (selectedId == R.id.option_location){
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.LOCATION);
                    }
                });
                dialog.show();
            });
        });
        thread.start();
    }

    private void addNewDetailWI(int id, int detailType) {
        if (detailType != MemberDetails.DOB && detailType != MemberDetails.DOD) {
            Dialog dialog = new Dialog(sketchActivity, R.style.CustomTransparentDialog);
            dialog.setContentView(R.layout.add_detail);
            TextView detailTitle = dialog.findViewById(R.id.title);
            LinearLayout detailLayout = dialog.findViewById(R.id.detail_name_layout);
            EditText detailValue = dialog.findViewById(R.id.detail_value);
            EditText detailName = dialog.findViewById(R.id.detail_name);
            switch (detailType){
                case MemberDetails.MOBILE:
                    detailTitle.setText("Add Mobile Number");
                    detailLayout.setVisibility(View.GONE);
                    detailValue.setVisibility(View.VISIBLE);
                    detailValue.setHint("Mobile Number");
                    detailValue.setInputType(InputType.TYPE_CLASS_PHONE);
                    break;
                case MemberDetails.CURRENT_AGE:
                    detailTitle.setText("Add Current Age");
                    detailLayout.setVisibility(View.GONE);
                    detailValue.setVisibility(View.VISIBLE);
                    detailValue.setHint("Current Age");
                    detailValue.setInputType(InputType.TYPE_CLASS_NUMBER);
                    break;
                case MemberDetails.LOCATION:
                    detailTitle.setText("Add Location");
                    detailLayout.setVisibility(View.GONE);
                    detailValue.setVisibility(View.VISIBLE);
                    detailValue.setHint("Location");
                    detailValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MemberDetails.OCCUPATION:
                    detailTitle.setText("Add Occupation");
                    detailLayout.setVisibility(View.GONE);
                    detailValue.setVisibility(View.VISIBLE);
                    detailValue.setHint("Occupation");
                    detailValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MemberDetails.DISCRIPTION:
                    detailTitle.setText("Add Description");
                    detailLayout.setVisibility(View.GONE);
                    detailValue.setVisibility(View.VISIBLE);
                    detailValue.setHint("Description");
                    detailValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MemberDetails.CUSTOM_DETAIL:
                    detailTitle.setText("Add Detail");
                    detailLayout.setVisibility(View.VISIBLE);
                    detailName.setHint("Detail Name");
                    detailName.setInputType(InputType.TYPE_CLASS_TEXT);
                    detailValue.setHint("Detail Value");
                    detailValue.setInputType(InputType.TYPE_CLASS_TEXT);
                    detailValue.setVisibility(View.VISIBLE);
                    break;
            }
            Button addDetail = dialog.findViewById(R.id.add_detail_button);
            addDetail.setOnClickListener(v -> {
                String detailNameValue = "";
                if(detailType == MemberDetails.CUSTOM_DETAIL){
                    detailNameValue = detailName.getText().toString();
                    if(detailNameValue.isEmpty()){
                        detailName.setError("Detail Name is required");
                        return;
                    }
                }else{
                    switch (detailType){
                        case MemberDetails.MOBILE:
                            detailNameValue = "Mobile Number";
                            break;
                        case MemberDetails.CURRENT_AGE:
                            detailNameValue = "Current Age";
                            break;
                        case MemberDetails.LOCATION:
                            detailNameValue = "Location";
                            break;
                        case MemberDetails.OCCUPATION:
                            detailNameValue = "Occupation";
                            break;
                        case MemberDetails.DISCRIPTION:
                            detailNameValue = "Description";
                            break;
                    }
                }
                String detailValueValue = detailValue.getText().toString();
                if(detailValueValue.isEmpty()){
                    detailValue.setError("Detail Value is required");
                    return;
                }else{
                    if(validateDetail(detailNameValue, detailValueValue, detailType, dialog)){
                        storeDetail(detailNameValue, detailValue.getText().toString(), id, detailType);
                    }
                }
                dialog.dismiss();
            });
            dialog.show();
        } else {
            DatePickerDialog datePickerDialog = getDatePickerDialog(id, detailType);
            datePickerDialog.show();
        }
    }

    private @NonNull DatePickerDialog getDatePickerDialog(int id, int detailType) {
        String detailName;
        if(detailType == MemberDetails.DOB){
            detailName = "Date of Birth";
        }else if(detailType == MemberDetails.DOD){
            detailName = "Date of Death";
        } else {
            detailName = "";
        }
        return new DatePickerDialog(sketchActivity, (view, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            storeDetail(detailName, date, id, detailType);
        }, 2000, 0, 1);
    }

    private void storeDetail(String detailNameValue, String string, int id, int detailType) {
        Log.d("Detail", detailNameValue + " " + string + " " + id + " " + detailType);
        Thread thread = new Thread(() -> {
            MemberDetails memberDetails = new MemberDetails(detailNameValue, string, sketchActivity.treeId, id, detailType);
            familyDatabase.familyDao().insertMemberDetails(memberDetails);
            FamilyMember updatedFamilyMember = familyDatabase.familyDao().getMember(id);
            sketchActivity.runOnUiThread(() -> {
                if (uiHandler.personDetails.personDetailsContainer.getVisibility() == View.VISIBLE) {
                    uiHandler.personDetails.showPersonDetails(updatedFamilyMember);
                }
            });
        });
        thread.start();
    }

    private boolean validateDetail(String detailNameValue, String detailValueValue, int detailType, Dialog dialog) {
        detailNameValue = detailNameValue.trim();
        detailValueValue = detailValueValue.trim();
        boolean isValid = true;
        if(detailNameValue.isEmpty()){
            EditText detailName = dialog.findViewById(R.id.detail_name);
            detailName.setError("Detail Name is required");
            isValid = false;
        }
        if(detailValueValue.isEmpty()){
            EditText detailValue = dialog.findViewById(R.id.detail_value);
            detailValue.setError("Detail Value is required");
            isValid = false;
        }
        if(detailType == MemberDetails.MOBILE){
            if(detailValueValue.length() != 10){
                EditText detailValue = dialog.findViewById(R.id.detail_value);
                detailValue.setError("Mobile Number should be 10 digits");
                isValid = false;
            }
        }
        if(detailType == MemberDetails.CURRENT_AGE){
            if(Integer.parseInt(detailValueValue) < 0){
                EditText detailValue = dialog.findViewById(R.id.detail_value);
                detailValue.setError("Age should be greater than 0");
                isValid = false;
            }
        }
        return isValid;
    }


    public void updateChildrenList(int id) {
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
