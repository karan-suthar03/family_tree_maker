package com.someone.familytree.database.Utils;

import java.util.ArrayList;
import java.util.List;

public class FamilyMemberFB{
    String memberName;
    List<DetailsFB> details;
    List<FamilyMemberFB> children;
    public String uId;

    public FamilyMemberFB() {
        this.memberName = "";
        this.details = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public List<DetailsFB> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsFB> details) {
        this.details = details;
    }

    public List<FamilyMemberFB> getChildren() {
        return children;
    }

    public void setChildren(List<FamilyMemberFB> children) {
        this.children = children;
    }
}