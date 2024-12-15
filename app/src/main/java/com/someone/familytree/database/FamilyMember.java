package com.someone.familytree.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "family_tree_members")
public class FamilyMember {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String myUid;
    private String personUid;
    private int parentId;
    private int treeId;

    public FamilyMember(String name, int parentId, int treeId) {
        this.name = name;
        this.parentId = parentId;
        this.treeId = treeId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getParentId() {
        return parentId;
    }

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

    public int getTreeId() {
        return treeId;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public String getMyUid() {
        return myUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public String getPersonUid() {
        return personUid;
    }

    public void setPersonUid(String personUid) {
        this.personUid = personUid;
    }
}
