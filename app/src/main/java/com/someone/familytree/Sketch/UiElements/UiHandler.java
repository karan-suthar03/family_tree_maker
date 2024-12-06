package com.someone.familytree.Sketch.UiElements;

import com.someone.familytree.Sketch.SketchActivity;

public class UiHandler {
    SketchActivity sketchActivity;

    public CardViewHandler cardViewHandler;

    public PersonDetails personDetails;
    public AddNewMember addNewMember;
    public UiHandler(SketchActivity sketchActivity) {
        this.sketchActivity = sketchActivity;
        cardViewHandler = new CardViewHandler(this);
        personDetails = new PersonDetails(this);
        addNewMember = new AddNewMember(this);
    }
}
