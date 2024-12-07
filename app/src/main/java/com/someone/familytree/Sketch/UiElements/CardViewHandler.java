package com.someone.familytree.Sketch.UiElements;

import static com.someone.familytree.Sketch.TreeHandler.familyDatabase;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.someone.familytree.R;
import com.someone.familytree.Sketch.SketchActivity;
import com.someone.familytree.database.FamilyMember;

public class CardViewHandler {
    CardView cardView;
    ConstraintLayout constraintLayout;
    int cardWidth = 0;
    int cardHeight = 0;
    int constraintWidth = 0;
    int constraintHeight = 0;
    SketchActivity sketchActivity;
    UiHandler uiHandler;
    public CardViewHandler(UiHandler uiHandler) {
        this.sketchActivity = uiHandler.sketchActivity;
        this.uiHandler = uiHandler;
        constraintLayout = sketchActivity.findViewById(R.id.personCardLayout);
        cardView = sketchActivity.findViewById(R.id.personCard);

        constraintLayout.post(() -> {
            cardHeight = cardView.getHeight();
            cardWidth = cardView.getWidth();
            constraintWidth = constraintLayout.getWidth();
            constraintHeight = constraintLayout.getHeight();

            sketchActivity.runOnUiThread(() -> cardView.setVisibility(View.GONE));
        });
    }

    public void showCard(float x, float y, int id, int nodeHeight) {
        FamilyMember familyMember = familyDatabase.familyDao().getMember(id);

        if (x + (float) cardWidth / 2 > constraintWidth) {
            x = constraintWidth - (float) cardWidth / 2;
        }
        if (x - (float) cardWidth / 2 < 0) {
            x = (float) cardWidth / 2;
        }

        if (y + cardHeight + (float) nodeHeight / 2 > constraintHeight) {
            y = y - cardHeight - (float) nodeHeight;
        }
        if (y < 0) {
            y = 0;
        }

        float finalX = x;
        float finalY = y;

        sketchActivity.runOnUiThread(() -> {

            cardView.setVisibility(View.VISIBLE);
            constraintLayout.bringChildToFront(cardView);
            constraintLayout.setVisibility(View.VISIBLE);
            cardView.setX(finalX - (float) cardWidth / 2);
            cardView.setY(finalY + (float) nodeHeight / 2 + 50);

            TextView name = cardView.findViewById(R.id.personName);
            name.setText(familyMember.getName());
            TextView DOB = cardView.findViewById(R.id.personDob);
            DOB.setText("Unknown");
            Button edit = cardView.findViewById(R.id.editPerson);
            edit.setOnClickListener(v -> {
                uiHandler.personDetails.showPersonDetails(familyMember);
                hidePersonCard();
            });
        });
    }

    public void hidePersonCard() {
        sketchActivity.runOnUiThread(() -> cardView.setVisibility(View.GONE));
    }
}
