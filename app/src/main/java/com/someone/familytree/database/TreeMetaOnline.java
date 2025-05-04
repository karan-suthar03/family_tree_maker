package com.someone.familytree.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_meta_online")
public class TreeMetaOnline {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String treeName;
    private String Uid;
    private int treeId;
    private String treeVersionOnline;

    public TreeMetaOnline(String treeName) {
        this.treeName = treeName;
    }

    public int getId() {
        return id;
    }

    public String getTreeName() {
        return treeName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTreeName(String TreeName) {
        this.treeName = TreeName;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String Uid) {
        this.Uid = Uid;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public String getTreeVersionOnline() {
        return treeVersionOnline;
    }

    public void setTreeVersionOnline(String treeVersionOnline) {
        this.treeVersionOnline = treeVersionOnline;
    }
}
