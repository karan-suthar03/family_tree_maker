package com.someone.familytree.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "family_tree")
public class FamilyTreeTable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String treeName;

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
}
