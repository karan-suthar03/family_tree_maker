package com.someone.familytree;

import com.someone.familytree.database.FamilyDatabase;
import com.someone.familytree.database.FamilyMember;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class TreeHandler{
    static SingleMemberWI rootWI;
    static Sketch sketch;
    static ArrayList<Node> nodes = new ArrayList<>();

    static ArrayList<Node> TempNodes = new ArrayList<>();

    static int nodeHeight = 50;

    static ArrayList<PVector[]> lines = new ArrayList<>();

    static ArrayList<PVector[]> TempLines = new ArrayList<>();

    static FamilyDatabase familyDatabase;

    static float nodeWidth = 100;

    public static void drawTree(float zoomLevel) {
        float strokeWeight = PApplet.constrain(2 / zoomLevel, 1, 5);
        Iterator<PVector[]> lineIterator = lines.iterator();
        while (lineIterator.hasNext()) {
            PVector[] line = lineIterator.next();
            sketch.strokeWeight(strokeWeight);
            sketch.stroke(0);
            sketch.line(line[0].x, line[0].y + (float) TreeHandler.nodeHeight /2, line[1].x, line[1].y -  (float) TreeHandler.nodeHeight /2);
        }

        Iterator<Node> nodeIterator = nodes.iterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            if (node.isActive) {
                sketch.fill(0, 255, 0);
            } else {
                sketch.fill(255);
            }
            sketch.strokeWeight(strokeWeight);
            sketch.stroke(0);
            sketch.rect(node.position.x - nodeWidth / 2, node.position.y - (float) nodeHeight / 2, nodeWidth, nodeHeight);
            sketch.fill(0);
            sketch.noStroke();
            String name = node.name;
            if(sketch.textWidth(name) > TreeHandler.nodeWidth  - 10){
                int i = 0;
                while(sketch.textWidth(name) > TreeHandler.nodeWidth - 15){
                    i++;
                    name = name.substring(0, name.length() - 1);
                }
                if (i > 0) {
                    name = name + "...";
                }else {
                    name = name.substring(0, name.length() - 1) + "...";
                }
            }
            sketch.textAlign(sketch.CENTER, sketch.CENTER);
            sketch.text(name, node.position.x, node.position.y);
        }
    }

    public static void checkTouch(float touchPosX, float touchPosY) {
        for (Node node : nodes) {
            float myWidth = nodeWidth;
            if (touchPosX > node.position.x - myWidth / 2 && touchPosX < node.position.x + myWidth / 2 && touchPosY > node.position.y - (float) nodeHeight / 2 && touchPosY < node.position.y + (float) nodeHeight / 2) {
                showDetails(node);
            } else {
                node.isActive = false;
            }
        }
    }

    private static void showDetails(Node node) {
        float x = (node.position.x + sketch.offsetX) * sketch.zoomLevel + (float) sketch.width / 2;
        float y = (node.position.y + sketch.offsetY) * sketch.zoomLevel + (float) sketch.height / 2;
        sketch.sketchActivity.showPersonCard(x, y, node.id, (int) (nodeHeight * sketch.zoomLevel));
        node.isActive = true;
    }

    static void addChild(int id) {
        String name = "New Child";
        FamilyMember familyMember = new FamilyMember(name, id, sketch.sketchActivity.treeId);
        familyDatabase.familyDao().insertMember(familyMember);
        refreshTree();
    }

    public static void refreshTree() {
        List<FamilyMember> familyMembers = familyDatabase.familyDao().getChildren(0,sketch.sketchActivity.treeId);
        if (familyMembers.isEmpty()) {
            sketch.sketchActivity.showAddMemberOption();
        }else{
            FamilyMember familyMember = familyDatabase.familyDao().getChildren(0,sketch.sketchActivity.treeId).get(0);
            rootWI = new SingleMemberWI(familyMember.getName(), familyMember.getId(),sketch.sketchActivity.treeId);
            convertToSingleMemberWI(rootWI, rootWI.id);
            TempNodes = new ArrayList<>();
            TempLines = new ArrayList<>();
            calculatePositions(rootWI, 0, -sketch.height*2/5);
            nodes = TempNodes;
            lines = TempLines;
        }
    }

    private static void convertToSingleMemberWI(SingleMemberWI parent, int id) {
        for (FamilyMember member : familyDatabase.familyDao().getChildren(id,sketch.sketchActivity.treeId)) {
            SingleMemberWI child = new SingleMemberWI(member.getName(), member.getId(),sketch.sketchActivity.treeId);
            parent.addChildren(child);
            convertToSingleMemberWI(child, member.getId());
        }
    }

    public static void clear() {
        nodes = new ArrayList<>();
        lines = new ArrayList<>();
    }

    static class Node {
        public String name;
        public PVector position;
        public boolean isActive = false;
        public int id;
        Node(String name, PVector position,int id) {
            this.name = name;
            this.position = position;
            this.id = id;
        }
    }

    public static void setRootWI(SingleMemberWI rootWI, Sketch sketch) {
        TreeHandler.rootWI = rootWI;
        TreeHandler.sketch = sketch;
    }

    private static int calculateRootWidth(SingleMemberWI dataPath) {
        if (dataPath.children.isEmpty()) {
            return (int) nodeWidth + 10;
        } else {
            int width = 0;
            for (SingleMemberWI child : dataPath.children) {
                width += calculateRootWidth(child);
            }
            return width;
        }
    }

    public static void calculatePositions(SingleMemberWI dataPath, int x, int y) {
        if(dataPath.children.isEmpty()) {
            TempNodes.add(new Node(dataPath.name, new PVector(x, y), dataPath.id));
            return;
        }

        int totalChildWidth = 0;
        for (SingleMemberWI child : dataPath.children) {
            totalChildWidth += calculateRootWidth(child);
        }

        int verticalSpacingForChildren = (int) (totalChildWidth * 0.1 + nodeHeight * 1.5);
        int childX = x - totalChildWidth / 2;

        for(SingleMemberWI child : dataPath.children) {
            int childWidth = calculateRootWidth(child);
            int ChildCenterX = childX + childWidth / 2;
            int ChildY = y + verticalSpacingForChildren;
            TempLines.add(new PVector[]{new PVector(x, y), new PVector(ChildCenterX, ChildY)});
            calculatePositions(child, ChildCenterX, ChildY);
            childX += childWidth;
        }
        TempNodes.add(new Node(dataPath.name, new PVector(x, y), dataPath.id));
    }
}
