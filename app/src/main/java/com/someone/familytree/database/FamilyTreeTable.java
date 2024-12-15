package com.someone.familytree.database;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "family_tree")
public class FamilyTreeTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String treeName;
    private String Uid;

    public FamilyTreeTable(String treeName) {
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

}
