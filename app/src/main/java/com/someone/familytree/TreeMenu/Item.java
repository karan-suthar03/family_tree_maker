package com.someone.familytree.TreeMenu;

public class Item {
    private String treeName;
    private int treeId;

    public Item(String text, int treeId) {
        this.treeName = text;
        this.treeId = treeId;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }
}
