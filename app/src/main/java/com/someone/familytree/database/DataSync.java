package com.someone.familytree.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.someone.familytree.connection.Authentication.Authentication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataSync {

    public static class SendingMemberData {
        String name;
        String uid;
        List<SendingMemberData> children;

    }

    public static class SendingData {
        String treeName;
        String treeUid;
        String treeVersion;
        String userUid;
        SendingMemberData root;
    }

    public static String uploadTree(FamilyTreeTable familyTreeTable) {
        List<FamilyMember> member = DatabaseManager.getChildren(0, familyTreeTable.getId());
        SendingData sendingData = new SendingData();
        sendingData.treeName = familyTreeTable.getTreeName();
        if (familyTreeTable.getUid() == null || familyTreeTable.getUid().isEmpty()) {
            String uid = generateUid();
            familyTreeTable.setUid(uid);
            sendingData.treeUid = uid;
            DatabaseManager.updateTree(familyTreeTable);
        } else {
            sendingData.treeUid = familyTreeTable.getUid();
        }
        SendingMemberData root = createSendingMember(member.get(0), familyTreeTable.getId());
        sendingData.root = root;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<TreeMetaOffline> treeMetaOfflines = DatabaseManager.getTreeMeta(familyTreeTable.getId());
        if (treeMetaOfflines != null && !treeMetaOfflines.isEmpty()) {
            sendingData.treeVersion = String.valueOf(treeMetaOfflines.get(0).getTreeVersionOffline());
        }
        sendingData.userUid = Authentication.getInstance().getCurrentUser().getUid();
        return gson.toJson(sendingData);
    }

    private static SendingMemberData createSendingMember(FamilyMember familyMember, int id) {
        SendingMemberData sendingMember = new SendingMemberData();
        sendingMember.name = familyMember.getName();
        if (familyMember.getMyUid() == null || familyMember.getMyUid().isEmpty()) {
            String uid = generateUid();
            familyMember.setMyUid(uid);
            sendingMember.uid = uid;
            DatabaseManager.updateMember(familyMember);
        } else {
            sendingMember.uid = familyMember.getMyUid();
        }
        List<FamilyMember> children = DatabaseManager.getChildren(familyMember.getId(), id);
        if (children != null && !children.isEmpty()) {
            sendingMember.children = new ArrayList<>();
            for (FamilyMember child : children) {
                SendingMemberData childMember = createSendingMember(child, id);
                sendingMember.children.add(childMember);
            }
        }
        return sendingMember;
    }

    public static class onlineMeta {
        int id;
        String uid;
        String treeUid;
        int version;
        String treeName;
        String userUid;
    }

    public static void updateMetadataFromServer(String meta) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(meta);
        List<onlineMeta> onlineMetas = new ArrayList<>();
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            onlineMeta record = gson.fromJson(element, onlineMeta.class);
            onlineMetas.add(record);
        }
        saveAllMetadata(onlineMetas);
    }

    private static void saveAllMetadata(List<onlineMeta> onlineMetas) {
        for (onlineMeta onlineMeta : onlineMetas) {
            TreeMetaOnline treeMetaOnline = DatabaseManager.getTreeMetaOnline(onlineMeta.treeUid);
            if (treeMetaOnline == null) {
                treeMetaOnline = new TreeMetaOnline(onlineMeta.treeName);
                treeMetaOnline.setUid(onlineMeta.uid);
                treeMetaOnline.setTreeId(onlineMeta.id);
                treeMetaOnline.setTreeVersionOnline(String.valueOf(onlineMeta.version));
                DatabaseManager.insertTreeMetaOnline(treeMetaOnline);
            } else {
                treeMetaOnline.setTreeName(onlineMeta.treeName);
                treeMetaOnline.setTreeVersionOnline(String.valueOf(onlineMeta.version));
                DatabaseManager.updateTreeMetaOnline(treeMetaOnline);
            }
        }
    }

    public static class ReceivedData {
        String memberId;
        String memberName;
        String memberUid;
        String parentId;
        String treeUid;
        String treeName;
        String personUid;
        String treeId;

        public ReceivedData(String memberId, String memberName, String memberUid,
                            String parentId, String treeUid, String treeName,
                            String personUid, String treeId) {
            this.memberId = memberId;
            this.memberName = memberName;
            this.memberUid = memberUid;
            this.parentId = parentId;
            this.treeUid = treeUid;
            this.treeName = treeName;
            this.personUid = personUid;
            this.treeId = treeId;
        }
    }

    static class Tree {
        String treeName;
        String treeUid;
        HashMap<Integer, List<ReceivedData>> members = new HashMap<>();
    }

    static class SaveTree {
        String treeName;
        String treeUid;
        RecivedMember root;
    }

    static class RecivedMember {
        String memberName;
        String memberUid;
        List<RecivedMember> children;

        @Override
        public String toString() {
            return "RecivedMember{" +
                    "memberName='" + memberName + '\'' +
                    ", memberUid='" + memberUid + '\'' +
                    ", children=" + children +
                    '}';
        }
    }
    public static void updateDatabaseFromServer(String jsonString) {
        List<ReceivedData> data = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = JsonParser.parseString(jsonString);
        for (JsonElement element : jsonElement.getAsJsonArray()) {
            ReceivedData record = gson.fromJson(element, ReceivedData.class);
            data.add(record);
        }
        HashMap<String, Tree> trees = new HashMap<>();
        for (ReceivedData singleData : data) {
            Tree tree = trees.get(singleData.treeUid);
            if (tree == null) {
                tree = new Tree();
                tree.treeName = singleData.treeName;
                tree.treeUid = singleData.treeUid;
                trees.put(singleData.treeUid, tree);
            }
            // Use parentId as key (converted to int) to group children.
            int parentKey = Integer.parseInt(singleData.parentId);
            List<ReceivedData> membersList = tree.members.get(parentKey);
            if (membersList == null) {
                membersList = new ArrayList<>();
                tree.members.put(parentKey, membersList);
            }
            membersList.add(singleData);
        }
        saveAllTrees(trees);
    }
    private static void saveAllTrees(HashMap<String, Tree> trees) {
        for (Tree tree : trees.values()) {
            SaveTree saveTree = new SaveTree();
            saveTree.treeName = tree.treeName;
            saveTree.treeUid = tree.treeUid;

            List<ReceivedData> roots = tree.members.get(0);
            if (roots == null || roots.isEmpty()) {
                System.out.println("No root member found for tree: " + tree.treeUid);
                continue;
            }
            saveTree.root = buildMember(roots.get(0), tree.members);
            saveReceivedData(saveTree);
        }
    }
    private static RecivedMember buildMember(ReceivedData data, HashMap<Integer, List<ReceivedData>> members) {
        RecivedMember member = new RecivedMember();
        member.memberName = data.memberName;
        member.memberUid = data.memberUid;
        member.children = new ArrayList<>();
        int currentMemberId = Integer.parseInt(data.memberId);
        List<ReceivedData> childrenData = members.get(currentMemberId);
        if (childrenData != null) {
            for (ReceivedData childData : childrenData) {
                RecivedMember childMember = buildMember(childData, members);
                member.children.add(childMember);
            }
        }
        return member;
    }

    private static void saveReceivedData(SaveTree saveTree) {
        long treeId = setTree(saveTree.treeUid, saveTree.treeName);
        FamilyMember root = DatabaseManager.getMemberByUid(saveTree.root.memberUid, treeId);
        if (root == null) {
            root = new FamilyMember(saveTree.root.memberName, 0, (int) treeId);
            root.setMyUid(saveTree.root.memberUid);
            int rootId = (int) DatabaseManager.insertMember(root);
            root.setId(rootId);
        }
        saveMembers(saveTree.root.children, root.getId(), treeId);
    }

    private static void saveMembers(List<RecivedMember> members, int parentId, long treeId) {
        for (RecivedMember member : members) {
            FamilyMember familyMember = DatabaseManager.getMemberByUid(member.memberUid, treeId);
            if (familyMember == null) {
                familyMember = new FamilyMember(member.memberName, parentId, (int) treeId);
                familyMember.setMyUid(member.memberUid);
                int memberId = (int) DatabaseManager.insertMember(familyMember);
                familyMember.setId(memberId);
            }
            saveMembers(member.children, familyMember.getId(), treeId);
        }
    }

    private static long setTree(String treeUid, String treeName) {
        long treeId = 0;
        FamilyTreeTable tree = DatabaseManager.getTreeByUid(treeUid);
        if (tree == null) {
            tree = new FamilyTreeTable(treeName);
            tree.setUid(treeUid);
            treeId = DatabaseManager.insertTree(tree);
        }
        return treeId;
    }

    static class sendingMember{
        String memberName;
        String memberId;
        String memberUid;
        List<sendingMember> children;
    }

    static class sendingTree {
        String treeName;
        String treeUid;
        int version;
        List<sendingMember> members;
    }
    public static String updateServerFromDevice() {
        List<sendingTree> sendingTrees = new ArrayList<>();
        List<FamilyTreeTable> trees = DatabaseManager.getAllTrees();
        for (FamilyTreeTable tree : trees) {
            sendingTree treeData = new sendingTree();
            treeData.treeName = tree.getTreeName();
            if (tree.getUid() == null || tree.getUid().isEmpty()) {
                treeData.treeUid = generateUid();
            } else {
                treeData.treeUid = tree.getUid();
            }
            treeData.version = tree.getVersion();
            List<FamilyMember> root = DatabaseManager.getChildren(0, tree.getId());
            if (root != null && !root.isEmpty()) {
                sendingMember rootMember = new sendingMember();
                rootMember.memberName = root.get(0).getName();
                if (root.get(0).getMyUid() == null || root.get(0).getMyUid().isEmpty()) {
                    rootMember.memberUid = generateUid();
                    root.get(0).setMyUid(rootMember.memberUid);
                    DatabaseManager.updateMember(root.get(0));
                } else {
                    rootMember.memberUid = root.get(0).getMyUid();
                }
                rootMember.memberId = String.valueOf(root.get(0).getId());
                treeData.members = new ArrayList<>();
                treeData.members.add(rootMember);
                buildTree(rootMember, tree.getId());
            }
            sendingTrees.add(treeData);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(sendingTrees);
    }

    private static void buildTree(sendingMember parent, int id) {
        List<FamilyMember> children = DatabaseManager.getChildren(Integer.parseInt(parent.memberId), id);
        if (children != null && !children.isEmpty()) {
            parent.children = new ArrayList<>();
            for (FamilyMember child : children) {
                sendingMember childMember = new sendingMember();
                childMember.memberName = child.getName();
                if (child.getMyUid() == null || child.getMyUid().isEmpty()) {
                    childMember.memberUid = generateUid();
                    child.setMyUid(childMember.memberUid);
                    DatabaseManager.updateMember(child);
                } else {
                    childMember.memberUid = child.getMyUid();
                }
                childMember.memberId = String.valueOf(child.getId());
                parent.children.add(childMember);
                buildTree(childMember, id);
            }
        }
    }

    private static String generateUid() {
        return UUID.randomUUID().toString();
    }
}
