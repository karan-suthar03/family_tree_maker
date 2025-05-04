package com.someone.familytree.TreeMenu;

public class Item {
    private String treeName;
    private int treeId;
    private String description;
    private int treeVersionOnline;
    private int treeVersionOffline;
    private int metaId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTreeVersionOnline() {
        return treeVersionOnline;
    }

    public void setTreeVersionOnline(int treeVersionOnline) {
        this.treeVersionOnline = treeVersionOnline;
    }

    public int getTreeVersionOffline() {
        return treeVersionOffline;
    }

    public void setTreeVersionOffline(int treeVersionOffline) {
        this.treeVersionOffline = treeVersionOffline;
    }

    public int getMetaId() {
        return metaId;
    }

    public void setMetaId(int metaId) {
        this.metaId = metaId;
    }
}
