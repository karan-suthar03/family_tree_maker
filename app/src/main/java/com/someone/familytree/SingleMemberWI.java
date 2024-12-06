package com.someone.familytree;

import java.util.ArrayList;
import java.util.List;

public class SingleMemberWI {
    public int id;
    public String name;
    public List<SingleMemberWI> children;
    int treeId;

    public SingleMemberWI(String name,int id, int treeId) {
        this.name = name;
        this.id = id;
        children = new ArrayList<>();
    }

    public void addChildren(SingleMemberWI child) {
        children.add(child);
    }
}
