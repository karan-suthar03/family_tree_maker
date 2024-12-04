package com.someone.familytree;

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

    float lastTouchDist = 0;


    SketchActivity sketchActivity;

    boolean isZooming = false;

    PVector lastTouch1 = new PVector(0,0);
    PVector lastTouch2 = new PVector(0,0);


    public Sketch(int width, int height, SingleMemberWI rootWI, SketchActivity sketchActivity) {
        cWidth = width;
        cHeight = height;
        TreeHandler.setRootWI(rootWI, this);
        this.sketchActivity = sketchActivity;
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
        background(255);
        //font colour black
        fill(0);
        text(frameRate, 100, 100);
        DrawTree();
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
        sketchActivity.hidePersonCard();
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
}
