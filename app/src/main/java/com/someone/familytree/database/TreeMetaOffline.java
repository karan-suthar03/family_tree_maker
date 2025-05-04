package com.someone.familytree.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tree_meta_offline")
public class TreeMetaOffline {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String treeName;
    private String Uid;
    private int treeId;
    private int treeVersionOffline = 0;
    private Boolean isDeleted = false;

    public TreeMetaOffline(String treeName) {
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

    public int getTreeVersionOffline() {
        return treeVersionOffline;
    }

    public void setTreeVersionOffline(int treeVersionOffline) {
        this.treeVersionOffline = treeVersionOffline;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
