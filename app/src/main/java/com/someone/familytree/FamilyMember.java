package com.someone.familytree;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "family_tree")
public class FamilyMember {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private int parentId;

    // Constructor
    public FamilyMember(String name, int parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int setParentId(int parentId) {
        this.parentId = parentId;
        return parentId;
    }
}
