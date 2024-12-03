package com.someone.familytree;

import java.util.ArrayList;
import java.util.List;

public class SingleMemberWI {
    int id;
    String name;
    List<SingleMemberWI> children;

    public SingleMemberWI(String name,int id) {
        this.name = name;
        this.id = id;
        children = new ArrayList<>();
    }

    public void addChildren(SingleMemberWI child) {
        children.add(child);
    }
}
