package com.someone.familytree.database.Utils;

import java.util.ArrayList;
import java.util.List;


public class TreeFB {
    // Ensure fields are either public or have getters and setters
    public String treeName;
    public int treeId;
    public FamilyMemberFB root;
    public String uId;
    public TreeFB() {}

    // Getter and setter methods
    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public void setRoot(FamilyMemberFB root) {
        this.root = root;
    }

    public String getTreeName() {
        return treeName;
    }

    public int getTreeId() {
        return treeId;
    }

    public FamilyMemberFB getRoot() {
        return root;
    }
}