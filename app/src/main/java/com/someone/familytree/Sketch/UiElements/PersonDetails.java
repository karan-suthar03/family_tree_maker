package com.someone.familytree.Sketch.UiElements;

import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Build;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.database.FamilyMember;
import com.someone.familytree.database.MemberDetails;

import java.util.List;

public class PersonDetails {

    UiHandler uiHandler;
    SketchActivity sketchActivity;
    BottomSheetDialog personDetailsLayout;
    public PersonDetails(UiHandler uiHandler) {
        this.uiHandler = uiHandler;
        this.sketchActivity = uiHandler.sketchActivity;
    }

    public void showPersonDetails(FamilyMember familyMember) {
        if(personDetailsLayout == null){
            personDetailsLayout = getBottomSheetDialog();
        }
        LayoutInflater inflater = sketchActivity.getLayoutInflater();
        View personDetailField = inflater.inflate(R.layout.person_detail_feilds, null, false);
        TextView heading = personDetailField.findViewById(R.id.FieldDetailHeading);
        heading.setText("Name:");
        TextView value = personDetailField.findViewById(R.id.FieldDetail);
        value.setText(familyMember.getName());
        LinearLayout personDetailList = personDetailsLayout.findViewById(R.id.PersonDetailsListLayout);
        assert personDetailList != null;
        personDetailList.removeAllViews();
        personDetailList.addView(personDetailField);
        Log.d("ParentId", familyMember.getParentId() + "");
        Thread thread = new Thread(() -> {
            if(familyMember.getParentId() != 0){
                FamilyMember parent = familyDatabase.familyDao().getMember(familyMember.getParentId());
                sketchActivity.runOnUiThread(()->{
                    View parentDetailField = inflater.inflate(R.layout.person_detail_feilds, null, false);
                    TextView parentHeading = parentDetailField.findViewById(R.id.FieldDetailHeading);
                    parentHeading.setText("Parent:");
                    TextView parentValue = parentDetailField.findViewById(R.id.FieldDetail);
                    parentValue.setText(parent.getName());
                    ImageButton goToParent = parentDetailField.findViewById(R.id.editDetail);
                    goToParent.setImageDrawable(AppCompatResources.getDrawable(sketchActivity, R.drawable.rounded_arrow_outward_24));
                    goToParent.setOnClickListener(v -> {
                        sketchActivity.sketch.goToPerson(parent.getId());
                        personDetailsLayout.dismiss();
                    });
                    personDetailList.addView(parentDetailField);
                });
            }else {
                Log.d("ParentId", "No Parent");
                sketchActivity.runOnUiThread(()->{
                    View addParentField = inflater.inflate(R.layout.add_parent_detail_item, null, false);
                    ImageButton addParentButton = addParentField.findViewById(R.id.addParent);
                    personDetailList.addView(addParentField);
                    addParentButton.setOnClickListener((v)-> uiHandler.addNewMember.addNewParent(familyMember));
                });
            }
            List<MemberDetails> memberDetails = familyDatabase.familyDao().getMemberDetails(familyMember.getId(), sketchActivity.treeId);
            for(MemberDetails memberDetail: memberDetails){
                sketchActivity.runOnUiThread(()->{
                    View detailField = inflater.inflate(R.layout.person_detail_feilds, null, false);
                    TextView detailHeading = detailField.findViewById(R.id.FieldDetailHeading);
                    detailHeading.setText(memberDetail.getDetailName() + ":");
                    TextView detailValue = detailField.findViewById(R.id.FieldDetail);
                    detailValue.setText(memberDetail.getDetailValue());
                    ImageButton editDetail = detailField.findViewById(R.id.editDetail);
                    editDetail.setOnClickListener((v)->{
                        editMemberDetail(memberDetail);
                    });
                    personDetailList.addView(detailField);
                });
            }
        });
        thread.start();
        updateChildrenList(familyMember.getId());
        MaterialButton addChildButton = personDetailsLayout.findViewById(R.id.addChildren);
        addChildButton.setOnClickListener((v)-> uiHandler.addNewMember.addNewChild(familyMember.getId()));
        MaterialButton addDetailButton = personDetailsLayout.findViewById(R.id.addDetails);
        assert addDetailButton != null;
        addDetailButton.setOnClickListener((v)-> addNewDetail(familyMember.getId()));

        personDetailsLayout.show();
    }

    private void editMemberDetail(MemberDetails memberDetail) {
        Dialog dialog = new Dialog(sketchActivity, R.style.CustomTransparentDialog);
        dialog.setContentView(R.layout.add_detail);
        TextView detailTitle = dialog.findViewById(R.id.title);
        LinearLayout detailLayout = dialog.findViewById(R.id.detail_name_layout);
        EditText detailValue = dialog.findViewById(R.id.detail_value);
        EditText detailName = dialog.findViewById(R.id.detail_name);
        detailTitle.setText("Edit Detail");
        detailLayout.setVisibility(View.GONE);
        detailValue.setVisibility(View.VISIBLE);
        detailValue.setText(memberDetail.getDetailValue());
        Button addDetail = dialog.findViewById(R.id.add_detail_button);
        addDetail.setText("Update");
        addDetail.setOnClickListener(v -> {
            String detailValueValue = detailValue.getText().toString();
            if(detailValueValue.isEmpty()){
                detailValue.setError("Detail Value is required");
                return;
            }else{
                if(validateDetail(memberDetail.getDetailName(), detailValueValue, memberDetail.getDetailType(), dialog)){
                    memberDetail.setDetailValue(detailValueValue);
                    Thread thread = new Thread(() -> {
                        familyDatabase.familyDao().updateMemberDetails(memberDetail);
                        FamilyMember updatedFamilyMember = familyDatabase.familyDao().getMember(memberDetail.getPersonId());
                        sketchActivity.runOnUiThread(() -> showPersonDetails(updatedFamilyMember));
                    });
                    thread.start();
                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private @NonNull BottomSheetDialog getBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(sketchActivity);
        bottomSheetDialog.setContentView(R.layout.person_details);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog bottomSheet = (BottomSheetDialog) dialog;
            View bottomSheetInternal = bottomSheet.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheetInternal != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);

                int screenHeight;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowMetrics windowMetrics = sketchActivity.getWindowManager().getCurrentWindowMetrics();
                    screenHeight = windowMetrics.getBounds().height();
                } else {
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    sketchActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    screenHeight = displayMetrics.heightPixels;
                }

                int minHeight = (int) (screenHeight * 0.75);
                bottomSheetInternal.setMinimumHeight(minHeight);

                bottomSheetInternal.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                behavior.setPeekHeight(minHeight);

                ScrollView scrollView = bottomSheetInternal.findViewById(R.id.scrollView);
                if (scrollView != null) {
                    scrollView.setOnTouchListener((v, event) -> {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            v.getParent().requestDisallowInterceptTouchEvent(v.getScrollY() != 0);
                        }
                        return false;
                    });
                }
            }
        });
        return bottomSheetDialog;
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
                    }else if (selectedId == R.id.option_custom){
                        dialog.dismiss();
                        addNewDetailWI(id, MemberDetails.CUSTOM_DETAIL);
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
            sketchActivity.runOnUiThread(() -> showPersonDetails(updatedFamilyMember));
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
        LinearLayout childrenList = personDetailsLayout.findViewById(R.id.PersonChildrenLayout);
        assert childrenList != null;
        Thread thread1 = new Thread(() -> {
            List<FamilyMember> children = familyDatabase.familyDao().getChildren(id, sketchActivity.treeId);
            sketchActivity.runOnUiThread(()->{
                if(!children.isEmpty()){
                    childrenList.removeAllViews();
                    for(FamilyMember child: children){
                        View childDetailField = inflater.inflate(R.layout.child_detail_item, null, false);
                        TextView childHeading = childDetailField.findViewById(R.id.ChildName);
                        childHeading.setText(child.getName());
                        ImageButton goToChild = childDetailField.findViewById(R.id.goToPerson);
                        goToChild.setOnClickListener(v -> {
                            sketchActivity.sketch.goToPerson(child.getId());
                            personDetailsLayout.dismiss();
                        });
                        childrenList.addView(childDetailField);
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
