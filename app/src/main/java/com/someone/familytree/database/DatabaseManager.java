package com.someone.familytree.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.someone.familytree.Authentication;
import com.someone.familytree.database.Utils.DetailsFB;
import com.someone.familytree.database.Utils.FamilyMemberFB;
import com.someone.familytree.database.Utils.TreeFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseManager {
    public static FamilyDatabase familyDatabase;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference userRef;

    public DatabaseManager(FamilyDatabase familyDatabase) {
        DatabaseManager.familyDatabase = familyDatabase;
    }


    public static List<FamilyTreeTable> getAllTrees() {
        if (familyDatabase == null) {
            return null;
        }
        return familyDatabase.familyDao().getAllTrees();
    }

    public static long insertTree(FamilyTreeTable familyTreeTable) {
        return familyDatabase.familyDao().insertTree(familyTreeTable);
    }

    public static void deleteTree(Integer id) {
        familyDatabase.familyDao().deleteTree(id);
    }

    public static void deleteAllMembers(Integer id) {
        familyDatabase.familyDao().deleteAll(id);
    }

    public static void updateTree(FamilyTreeTable familyTreeTable) {
        familyDatabase.familyDao().updateTree(familyTreeTable);
    }

    public static String getTreeName(int id) {
        return familyDatabase.familyDao().getTreeName(id);
    }

    public static List<FamilyMember> getChildren(int id, Integer treeId) {
        return familyDatabase.familyDao().getChildren(id, treeId);
    }

    public static long insertMember(FamilyMember familyMember) {
        return familyDatabase.familyDao().insertMember(familyMember);
    }

    public static void updateMember(FamilyMember familyMember) {
        familyDatabase.familyDao().updateMember(familyMember);
    }

    public static FamilyMember getMember(int id) {
        return familyDatabase.familyDao().getMember(id);
    }

    public static List<MemberDetails> getMemberDetails(int id, int treeId) {
        return familyDatabase.familyDao().getMemberDetails(id, treeId);
    }

    public static void updateMemberDetails(MemberDetails memberDetail) {
        familyDatabase.familyDao().updateMemberDetails(memberDetail);
    }

    public static void insertMemberDetails(MemberDetails memberDetails) {
        familyDatabase.familyDao().insertMemberDetails(memberDetails);
    }

    public static void deleteMember(int id, int treeId) {
        familyDatabase.familyDao().deleteMember(id, treeId);
    }

    public static void deleteMemberDetails(int id, int treeId) {
        familyDatabase.familyDao().deleteMemberDetails(id, treeId);
    }

    public static void updateParentId(int id, int newParentId, int treeId) {
        familyDatabase.familyDao().updateParentId(id, newParentId, treeId);
    }

    public static void init(Authentication authentication, FirebaseUser currentUser) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        familyDatabase = FamilyDatabase.getDatabase(authentication);

        assert currentUser != null;
        userRef = firebaseDatabase.getReference("users").child(currentUser.getUid());

        userRef.child("trees").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
//                    new Thread(() -> updateDatabase(snapshot)).start();
                    printAllTrees();
                }else{
                    new Thread(DatabaseManager::uploadAllTrees).start();
                }
            }
        });
    }

    private static void printAllTrees() {
        List<FamilyTreeTable> allTrees = getAllTrees();
        for (FamilyTreeTable tree : allTrees) {
            Log.d("DatabaseManager", "Tree: " + tree.getTreeName());
            List<FamilyMember> children = getChildren(0, tree.getId());
            for (FamilyMember child : children) {
                Log.d("DatabaseManager", "Child: " + child.getName());
                printChildren(child.getId(), tree.getId());
            }
        }
    }

    private static void printChildren(int id, int id1) {
        List<FamilyMember> children = getChildren(id, id1);
        for (FamilyMember child : children) {
            Log.d("DatabaseManager", "Child: " + child.getName());
            printChildren(child.getId(), id1);
        }
    }

    private static void updateDatabase(DataSnapshot snapshot) {
        for (DataSnapshot tree : snapshot.getChildren()) {
            FamilyTreeTable familyTreeTable = new FamilyTreeTable(tree.child("treeName").getValue(String.class));
            familyTreeTable.setUid(tree.getKey());
            int treeId = (int) insertTree(familyTreeTable);
            updateMembers(tree.child("members"), treeId);
            updateDetails(tree.child("details"), treeId);
        }
    }

    private static void updateDetails(DataSnapshot details, int treeId) {
        for (DataSnapshot detail : details.getChildren()) {
            MemberDetails memberDetails = new MemberDetails(detail.child("detailName").getValue(String.class), detail.child("detail").getValue(String.class), treeId, 0, (Integer) detail.child("detailType").getValue());
            memberDetails.setMyUid(detail.getKey());
            memberDetails.setPersonUid(detail.child("member").getValue(String.class));
            memberDetails.setPersonId(getParentId(memberDetails.getPersonUid(), treeId));
            insertMemberDetails(memberDetails);
        }
    }

    private static void updateMembers(DataSnapshot members, int treeId) {
        for (DataSnapshot member : members.getChildren()) {
            FamilyMember familyMember = new FamilyMember(member.child("memberName").getValue(String.class), 0, treeId);
            familyMember.setMyUid(member.getKey());
            familyMember.setPersonUid(member.child("parent").getValue(String.class));
            insertMember(familyMember);
        }

        List<FamilyMember> allMembers = familyDatabase.familyDao().getAllMembers(treeId);
        for (FamilyMember member : allMembers) {
            member.setParentId(getParentId(member.getPersonUid(), treeId));
        }
    }

    private static int getParentId(String personUid, int treeId) {
        FamilyMember member = familyDatabase.familyDao().getMemberByUid(personUid, treeId);
        return member.getId();
    }


    static HashMap<String, Object> trees = new HashMap<>();
    static HashMap<String, Object> tree = new HashMap<>();
    static HashMap<String, Object> members = new HashMap<>();
    static HashMap<String, Object> details = new HashMap<>();

    private static void uploadAllTrees() {
        List<FamilyTreeTable> allTrees = getAllTrees();
        List<TreeFB> treeFBS = new ArrayList<>();
        for (FamilyTreeTable tree : allTrees) {
            String uniqueId = userRef.push().getKey();
            TreeFB treeFB = new TreeFB();
            treeFB.treeName = tree.getTreeName();
            treeFB.root = getFamilyMemberFB(tree.getId());
            treeFB.uId = uniqueId;
            treeFBS.add(treeFB);
        }

        for (TreeFB treeFB : treeFBS) {
            members = new HashMap<>();
            tree = new HashMap<>();
            details = new HashMap<>();
            tree.put("treeName", treeFB.treeName);
            putAllMembers(treeFB.root,"root");
            tree.put("members", members);
            tree.put("details", details);
            trees.put(treeFB.uId, tree);
        }

        userRef.child("trees").setValue(trees).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseManager", "Trees uploaded successfully");
            } else {
                Log.d("DatabaseManager", "Trees upload failed: " + task.getException().getMessage());
            }
        });
    }

    private static void putAllMembers(FamilyMemberFB root, String parent) {
        HashMap<String, Object> member = new HashMap<>();
        member.put("memberName", root.getMemberName());
        member.put("parent", parent);
        for (DetailsFB detail : root.getDetails()) {
            HashMap<String, Object> detailMap = new HashMap<>();
            detailMap.put("detail", detail.getDetail());
            detailMap.put("detailName", detail.getDetailName());
            detailMap.put("detailType", detail.getDetailType());
            detailMap.put("member", root.uId);
            details.put(detail.uId, detailMap);
        }
        for (FamilyMemberFB child : root.getChildren()) {
            putAllMembers(child, root.uId);
        }
        members.put(root.uId, member);
    }

    private static void printTree(TreeFB treeFB) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(treeFB);
        Log.d("DatabaseManager", json);
    }


    private static FamilyMemberFB getFamilyMemberFB(int i) {
        FamilyMember child = getChildren(0, i).get(0);
        if (child == null) {
            return null;
        }
        String uniqueId = userRef.push().getKey();
        FamilyMemberFB familyMemberFB = new FamilyMemberFB();
        familyMemberFB.setMemberName(child.getName());
        familyMemberFB.setDetails(getDetailsFB(child.getId(), i));
        familyMemberFB.setChildren(getChildrenFB(child.getId(), i));
        familyMemberFB.uId = uniqueId;
        return familyMemberFB;
    }

    private static List<FamilyMemberFB> getChildrenFB(int id, int i) {
        List<FamilyMember> children = getChildren(id, i);
        if (children == null) {
            return null;
        }
        List<FamilyMemberFB> familyMemberFBS = new ArrayList<>();
        for (FamilyMember child : children) {
            String uniqueId = userRef.push().getKey();
            FamilyMemberFB familyMemberFB = new FamilyMemberFB();
            familyMemberFB.setMemberName(child.getName());
            familyMemberFB.setDetails(getDetailsFB(child.getId(), i));
            familyMemberFB.setChildren(getChildrenFB(child.getId(), i));
            familyMemberFB.uId = uniqueId;
            familyMemberFBS.add(familyMemberFB);
        }
        return familyMemberFBS;
    }

    private static List<DetailsFB> getDetailsFB(int id, int id1) {
        List<MemberDetails> memberDetails = getMemberDetails(id, id1);
        if (memberDetails == null) {
            return null;
        }
        List<DetailsFB> detailsFBS = new ArrayList<>();
        for (MemberDetails memberDetail : memberDetails) {
            String uniqueId = userRef.push().getKey();
            DetailsFB detailsFB = new DetailsFB();
            detailsFB.setDetail(memberDetail.getDetailValue());
            detailsFB.setDetailName(memberDetail.getDetailName());
            detailsFB.setDetailType(memberDetail.getDetailType());
            detailsFB.uId = uniqueId;
            detailsFBS.add(detailsFB);
        }
        return detailsFBS;
    }

}
