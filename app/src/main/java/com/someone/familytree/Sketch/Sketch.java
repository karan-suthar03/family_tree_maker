package com.someone.familytree.Sketch;

import static com.someone.familytree.Sketch.TreeHandler.nodeHeight;
import static com.someone.familytree.Sketch.TreeHandler.nodeWidth;

import com.someone.familytree.SingleMemberWI;

import processing.core.PApplet;
import processing.core.PVector;

public class Sketch extends PApplet {
    int cHeight;
    int cWidth;
    float offsetX = 0;
    float offsetY = 0;
    boolean isDragging = false;
    float lastX = 0;
    float lastY = 0;
    float zoomLevel = 1;
    boolean isFocusing = false;
    boolean focusDone = true;
    float lastTouchDist = 0;
    float focusFromX = 0;
    float focusFromY = 0;
    float focusToX = 0;
    float focusToY = 0;
    float deltaTime = 0L;
    float lastTime = 0L;
    SketchActivity sketchActivity;
    boolean isZooming = false;

    PVector lastTouch1 = new PVector(0,0);
    PVector lastTouch2 = new PVector(0,0);


    public Sketch(int width, int height, SingleMemberWI rootWI, SketchActivity sketchActivity) {
        cWidth = width;
        cHeight = height;
        TreeHandler.setRootWI(rootWI, this);
        this.sketchActivity = sketchActivity;
        TreeHandler.shadow = createGraphics((int) (((int)nodeWidth+10)*0.5), (int) ((nodeHeight+10)*0.5));
        TreeHandler.shadow.beginDraw();
        TreeHandler.shadow.noStroke();
        for (int i = 20; i > 0; i--) {  // Layered shadow for softness
            float alpha = map(i, 0, 20, 0, 50);  // Adjust opacity
            TreeHandler.shadow.fill(50, alpha);  // Dark grey with transparency
            TreeHandler.shadow.rect(0, 0, (int)nodeWidth, nodeHeight, 10);  // Rounded rectangle
        }
        TreeHandler.shadow.endDraw();
    }

    private void calculateDeltaTime() {
        deltaTime = (millis() - lastTime) / 1000f;
        lastTime = millis();
    }

    public void setup() {
        background(255);
        TreeHandler.refreshTree();
        frameRate(120);
    }

    public void settings() {
        size(cWidth, cHeight);
    }

    public void draw() {


        calculateDeltaTime();
        if(isFocusing){
            focusTo();
        }

        background(255);
        //draw grid
        stroke(0, 50);
        strokeWeight(2);
        for (int i = (int) ((float) width /2+(offsetX)*zoomLevel); i < width; i += (int) (100 * zoomLevel)) {
            line(i, 0, i, height);
        }
        for (int i = (int) ((float) width /2+(offsetX)*zoomLevel); i > 0; i -= (int) (100 * zoomLevel)) {
            line(i, 0, i, height);
        }
        for (int i = (int) ((float) height /2+(offsetY)*zoomLevel); i < height; i += (int) (100 * zoomLevel)) {
            line(0, i, width, i);
        }
        for (int i = (int) ((float) height /2+(offsetY)*zoomLevel); i > 0; i -= (int) (100 * zoomLevel)) {
            line(0, i, width, i);
        }

        //font colour black
        fill(0);
        text(frameRate, 100, 100);
        DrawTree();
    }

    private void focusTo() {
        if (focusDone) {
            isFocusing = false;
            return;
        }
        float focusSpeed = deltaTime;
        float diffX = focusToX - offsetX;
        float diffY = focusToY - offsetY;
        
        if (abs(diffX) < 1 && abs(diffY) < 1) {
            focusDone = true;
            return;
        }
        offsetX += diffX * focusSpeed *5;
        offsetY += diffY * focusSpeed *5;
        TreeHandler.checkTouch(-focusToX,-focusToY);
    }

    private void DrawTree() {
        push();
        translate((float) width / 2, (float) height / 2);
        scale(zoomLevel);
        translate(offsetX, offsetY);
        TreeHandler.drawTree(zoomLevel);
        pop();
    }

    public void touchStarted() {
        if (touches.length == 1) {
            lastX = touches[0].x;
            lastY = touches[0].y;
            isDragging = true;
            isZooming = false;
            lastTouch1 = new PVector(touches[0].x, touches[0].y);
        }
        if (touches.length >= 2) {
            lastTouchDist = dist(touches[0].x, touches[0].y, touches[1].x, touches[1].y);
            isZooming = true;
        }
        isFocusing = false;
        sketchActivity.uiHandler.cardViewHandler.hidePersonCard();
    }



    private void TouchInPut(float x, float y) {
        float touchPosX = (x - (float) width / 2) / zoomLevel - offsetX;
        float touchPosY = (y - (float) height / 2) / zoomLevel - offsetY;
        TreeHandler.checkTouch(touchPosX, touchPosY);
    }

    public void touchMoved() {
        if (touches.length == 1 && isDragging) {
            offsetX += (touches[0].x - lastX)/zoomLevel;
            offsetY += (touches[0].y - lastY)/zoomLevel;
            lastX = touches[0].x;
            lastY = touches[0].y;
            lastTouch2 = new PVector(touches[0].x, touches[0].y);
            isZooming = false;
        }

        if (touches.length >= 2) {
            float newDist = dist(touches[0].x, touches[0].y, touches[1].x, touches[1].y);

            float zoomAmount = (newDist - lastTouchDist) / 1000;

            zoomLevel += zoomAmount*zoomLevel;

            float pivotX = (touches[0].x + touches[1].x) / 2;
            float pivotY = (touches[0].y + touches[1].y) / 2;

            float pivotXInCanvas = (pivotX - offsetX) / zoomLevel;

            float pivotYInCanvas = (pivotY - offsetY) / zoomLevel;

            offsetX = pivotX - pivotXInCanvas * zoomLevel;

            offsetY = pivotY - pivotYInCanvas * zoomLevel;

            lastTouchDist = newDist;
            isZooming = true;
        }
    }

    // Called when a touch ends (one or more fingers)
    public void touchEnded() {

        float dist = lastTouch1.dist(lastTouch2);
        if (dist < 10 && !isZooming) {
            TouchInPut(lastTouch2.x, lastTouch2.y);
        }

        // Reset dragging state
        isDragging = false;
        // Reset zooming state
        lastTouchDist = 0;
    }

    @Override
    public void onDestroy() {
        sketchActivity = null;
        TreeHandler.clear();
        super.onDestroy();
    }

    public void goToPerson(int id) {
        focusFromX = offsetX;
        focusFromY = offsetY;
        PVector pos = TreeHandler.getPersonPosition(id);
        assert pos != null;
        focusToX = -pos.x;
        focusToY = -pos.y;
        isFocusing = true;
        focusDone = false;
    }
}
