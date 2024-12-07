package com.someone.familytree.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "member_details")
public class MemberDetails {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String detailName;
    private String detailValue;
    private int personId;
    private int treeId;
    private int detailType;
    public static final int DOB = 1;
    public static final int DOD = 2;
    public static final int DISCRIPTION = 3;
    public static final int LOCATION = 4;
    public static final int OCCUPATION = 5;
    public static final int CURRENT_AGE = 6;
    public static final int MOBILE = 7;
    public static final int CUSTOM_DETAIL = 0;

    public MemberDetails(String detailName, String detailValue, int treeId, int personId, int detailType) {
        this.detailName = detailName;
        this.detailValue = detailValue;
        this.treeId = treeId;
        this.personId = personId;
        this.detailType = detailType;
    }

    public int getId() {
        return id;
    }

    public String getDetailName() {
        return detailName;
    }

    public String getDetailValue() {
        return detailValue;
    }

    public int getTreeId() {
        return treeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName;
    }

    public void setDetailValue(String detailValue) {
        this.detailValue = detailValue;
    }

    public void setTreeId(int treeId) {
        this.treeId = treeId;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public int getDetailType() {
        return detailType;
    }

    public void setDetailType(int detailType) {
        this.detailType = detailType;
    }
}
