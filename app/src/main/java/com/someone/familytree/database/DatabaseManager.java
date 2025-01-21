package com.someone.familytree.database;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.someone.familytree.TreeMenu.TreeMenuActivity;
import com.someone.familytree.database.Utils.DetailsFB;
import com.someone.familytree.database.Utils.FamilyMemberFB;
import com.someone.familytree.database.Utils.TreeFB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        familyTreeTable.setVersion(1);
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
        upgradeTree(familyMember.getTreeId());
        return familyDatabase.familyDao().insertMember(familyMember);
    }

    public static FamilyTreeTable getTree(int id) {
        return familyDatabase.familyDao().getTree(id);
    }

    public static void updateMember(FamilyMember familyMember) {
        upgradeTree(familyMember.getTreeId());
        familyDatabase.familyDao().updateMember(familyMember);
    }

    public static FamilyMember getMember(int id) {
        return familyDatabase.familyDao().getMember(id);
    }

    public static List<MemberDetails> getMemberDetails(int id, int treeId) {
        return familyDatabase.familyDao().getMemberDetails(id, treeId);
    }

    public static void updateMemberDetails(MemberDetails memberDetail) {
        upgradeTree(memberDetail.getTreeId());
        familyDatabase.familyDao().updateMemberDetails(memberDetail);
    }

    public static void insertMemberDetails(MemberDetails memberDetails) {
        familyDatabase.familyDao().insertMemberDetails(memberDetails);
    }

    public static void deleteMember(int id, int treeId) {
        upgradeTree(treeId);
        familyDatabase.familyDao().deleteMember(id, treeId);
    }

    public static void deleteMemberDetails(int id, int treeId) {
        upgradeTree(treeId);
        familyDatabase.familyDao().deleteMemberDetails(id, treeId);
    }

    public static void updateParentId(int id, int newParentId, int treeId) {
        upgradeTree(treeId);
        familyDatabase.familyDao().updateParentId(id, newParentId, treeId);
    }

    public static void upgradeTree(int id) {
        FamilyTreeTable tree = getTree(id);
        tree.setVersion(tree.getVersion() + 1);
        updateTree(tree);
    }

    public static void setTreeUid(int id, String uid) {
        FamilyTreeTable tree = getTree(id);
        tree.setUid(uid);
        updateTree(tree);
    }

    public static void setMemberUid(int id, String uid) {
        FamilyMember member = getMember(id);
        member.setMyUid(uid);
        updateMember(member);
    }

    public static void setDetailUid(int id, String uid) {
        MemberDetails memberDetails = getMemberDetails(id, 0).get(0);
        memberDetails.setMyUid(uid);
        updateMemberDetails(memberDetails);
    }

    public static AtomicInteger threadTrack = new AtomicInteger(0);

    public static void init(AppCompatActivity activity, FirebaseUser currentUser) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        familyDatabase = FamilyDatabase.getDatabase(activity);

        assert currentUser != null;
        userRef = firebaseDatabase.getReference("users").child(currentUser.getUid());

        userRef.child("treeMetaData").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if(snapshot.exists()){
                    threadTrack.incrementAndGet();
                    new Thread(() -> {
                        checkValidity(snapshot);
                        threadTrack.decrementAndGet();
                        while(threadTrack.get() > 0){
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        activity.runOnUiThread(() -> {
                            Intent intent = new Intent(activity, TreeMenuActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        });
                    }).start();
                }else{
                    new Thread(()->{
                        uploadAllTrees();
                        activity.runOnUiThread(() -> {
                            Intent intent = new Intent(activity, TreeMenuActivity.class);
                            activity.startActivity(intent);
                            activity.finish();
                        });
                    }).start();
                }
            }
        });
    }

    private static void checkValidity(DataSnapshot snapshot) {
        for (DataSnapshot tree : snapshot.getChildren()) {
            String treeUid = tree.getKey();
            FamilyTreeTable familyTreeTable = getTreeByUid(treeUid);
            if (familyTreeTable == null) {
                createTreeFromOnline(treeUid,tree.getValue(Integer.class)   );
            } else {
                int version = familyTreeTable.getVersion();
                if (version < tree.getValue(Integer.class)) {
                    updateTreeFromOnline(treeUid, tree.getValue(Integer.class));
                } else if (version > tree.getValue(Integer.class)) {
                    updateTreeFromLocal(familyTreeTable);
                }
            }
        }
    }


    static HashMap<String, Object> trees = new HashMap<>();
    static HashMap<String, Object> tree = new HashMap<>();
    static HashMap<String, Object> members = new HashMap<>();
    static HashMap<String, Object> details = new HashMap<>();
    static HashMap<String, Object> treeMeta = new HashMap<>();


    private static void updateTreeFromLocal(FamilyTreeTable familyTreeTable) {
        String uniqueId = familyTreeTable.getUid();
        if (uniqueId == null) {
            uniqueId = userRef.push().getKey();
            familyTreeTable.setUid(uniqueId);
            updateTree(familyTreeTable);
        }
        TreeFB treeFB = new TreeFB();
        treeFB.treeName = familyTreeTable.getTreeName();
        treeFB.uId = uniqueId;
        treeFB.root = getFamilyMemberFB(familyTreeTable.getId());

        members = new HashMap<>();
        tree = new HashMap<>();
        details = new HashMap<>();
        tree.put("treeName", treeFB.treeName);
        putAllMembers(treeFB.root, "root");
        tree.put("members", members);
//        tree.put("details", details);
        HashMap<String, Object> treeMetaData = new HashMap<>();
        treeMetaData.put(treeFB.uId, familyTreeTable.getVersion());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(tree);
        Log.d("DatabaseManager", json);

        threadTrack.incrementAndGet();
        userRef.child("trees"+"/"+treeFB.uId).setValue(tree).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseManager", "Tree uploaded successfully");
            } else {
                Log.d("DatabaseManager", "Tree upload failed: " + task.getException().getMessage());
            }
            threadTrack.decrementAndGet();
        });

        threadTrack.incrementAndGet();
        userRef.child("treeMetaData").updateChildren(treeMetaData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseManager", "Tree metadata uploaded successfully");
            } else {
                Log.d("DatabaseManager", "Tree metadata upload failed: " + task.getException().getMessage());
            }
            threadTrack.decrementAndGet();
        });
    }

    private static void createTreeFromOnline(String treeUid, Integer version) {
        threadTrack.incrementAndGet();
        userRef.child("trees"+"/"+treeUid).get().addOnCompleteListener(task -> {
            threadTrack.decrementAndGet();
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    threadTrack.incrementAndGet();
                    new Thread(() -> {
                        addTree(snapshot, version);
                        threadTrack.decrementAndGet();
                    }).start();
                }
            }
        });
    }

    private static void updateTreeFromOnline(String treeUid, Integer value) {
        threadTrack.incrementAndGet();
        userRef.child("trees"+"/"+treeUid).get().addOnCompleteListener(task -> {
            threadTrack.decrementAndGet();
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    threadTrack.incrementAndGet();
                    new Thread(() -> {
                        updateTree_(snapshot, value);
                        threadTrack.decrementAndGet();
                    }).start();
                }
            }
        });
    }

    private static void updateTree_(DataSnapshot snapshot, Integer value) {
        FamilyTreeTable familyTreeTable = getTreeByUid(snapshot.getKey());
        familyTreeTable.setVersion(value);
        updateTree(familyTreeTable);
        updateLocalChild(snapshot.child("members"), familyTreeTable.getId());
//        updateDetails(snapshot.child("details"), familyTreeTable.getId());
    }

    private static void updateLocalChild(DataSnapshot members, int id) {

    }

    private static void addTree(DataSnapshot snapshot, Integer version) {
        FamilyTreeTable familyTreeTable = new FamilyTreeTable(snapshot.child("treeName").getValue(String.class));
        familyTreeTable.setUid(snapshot.getKey());
        familyTreeTable.setVersion(version);
        int treeId = (int) insertTree(familyTreeTable);
        updateMembers(snapshot.child("members"), treeId);
        updateDetails(snapshot.child("details"), treeId);
    }

    private static FamilyTreeTable getTreeByUid(String treeUid) {
        return familyDatabase.familyDao().getTreeByUid(treeUid);
    }

    private static void printAllTrees() {
        List<FamilyTreeTable> allTrees = getAllTrees();
        for (FamilyTreeTable tree : allTrees) {
            Log.d("DatabaseManager", "Tree: " + tree.getTreeName());
            Log.d("DatabaseManager", "Tree ID: " + tree.getId());
            List<FamilyMember> children = getChildren(0, tree.getId());
            Log.d("DatabaseManager", "Root: " + children.size());
            for (FamilyMember child : children) {
                Log.d("DatabaseManager", "Child: " + child.getName());
                Log.d("DatabaseManager", "Parent ID: " + child.getParentId());
                Log.d("DatabaseManager", "Person ID: " + child.getId());
                Log.d("DatabaseManager", "Person UID: " + child.getPersonUid());
                Log.d("DatabaseManager", "My UID: " + child.getMyUid());
                Log.d("DatabaseManager", "Tree ID: " + child.getTreeId());
                printChildren(child.getId(), tree.getId());
            }
        }
    }

    private static void printChildren(int id, int id1) {
        List<FamilyMember> children = getChildren(id, id1);
        for (FamilyMember child : children) {
            Log.d("DatabaseManager", "Child: " + child.getName());
            Log.d("DatabaseManager", "Parent ID: " + child.getParentId());
            Log.d("DatabaseManager", "Person ID: " + child.getId());
            Log.d("DatabaseManager", "Person UID: " + child.getPersonUid());
            Log.d("DatabaseManager", "My UID: " + child.getMyUid());
            Log.d("DatabaseManager", "Tree ID: " + child.getTreeId());
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
            memberDetails.setPersonId(getPersonId(memberDetails.getPersonUid(), treeId));
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
            FamilyMember parent = getMemberByUid(member.getPersonUid(), treeId);
            if (parent != null) {
                updateParentId(member.getId(), parent.getId(), treeId);
            }
        }
    }


    private static FamilyMember getMemberByUid(String personUid, int treeId) {
        return familyDatabase.familyDao().getMemberByUid(personUid, treeId);
    }

    private static void printMember(FamilyMember familyMember) {
        Log.d("DatabaseManager", "Member Name: " + familyMember.getName());
        Log.d("DatabaseManager", "Parent ID: " + familyMember.getParentId());
        Log.d("DatabaseManager", "Person ID: " + familyMember.getId());
        Log.d("DatabaseManager", "Person UID: " + familyMember.getPersonUid());
        Log.d("DatabaseManager", "My UID: " + familyMember.getMyUid());
        Log.d("DatabaseManager", "Tree ID: " + familyMember.getTreeId());
    }

    private static int getPersonId(String myUid, int treeId) {
        FamilyMember member = familyDatabase.familyDao().getMemberByUid(myUid, treeId);
        return member.getId();
    }

    private static void uploadAllTrees() {
        List<FamilyTreeTable> allTrees = getAllTrees();
        List<TreeFB> treeFBS = new ArrayList<>();
        for (FamilyTreeTable tree : allTrees) {
            String uniqueId = userRef.push().getKey();
            TreeFB treeFB = new TreeFB();
            treeFB.treeName = tree.getTreeName();
            FamilyMemberFB root = getFamilyMemberFB(tree.getId());
            if(root == null){
                continue;
            }
            treeFB.root = root;
            treeFB.uId = uniqueId;
            treeFB.version = tree.getVersion();
            Log.d("DatabaseManager", "Tree Version: " + treeFB.version);
            setTreeUid(tree.getId(), uniqueId);
            treeFBS.add(treeFB);
        }

        treeMeta = new HashMap<>();

        for (TreeFB treeFB : treeFBS) {
            members = new HashMap<>();
            tree = new HashMap<>();
            details = new HashMap<>();
            tree.put("treeName", treeFB.treeName);
            putAllMembers(treeFB.root,"root");
            tree.put("members", members);
            tree.put("details", details);

            treeMeta.put(treeFB.uId, treeFB.version);
            trees.put(treeFB.uId, tree);
        }

        userRef.child("trees").setValue(trees).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseManager", "Trees uploaded successfully");
            } else {
                Log.d("DatabaseManager", "Trees upload failed: " + task.getException().getMessage());
            }
        });

        userRef.child("treeMetaData").setValue(treeMeta).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("DatabaseManager", "Tree metadata uploaded successfully");
            } else {
                Log.d("DatabaseManager", "Tree metadata upload failed: " + task.getException().getMessage());
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
        List<FamilyMember> children = getChildren(0, i);
        if (children == null || children.isEmpty()) {
            return null;
        }
        FamilyMember child = children.get(0);
        if (child == null) {
            return null;
        }
        String uniqueId = child.getMyUid();
        if (uniqueId == null) {
            uniqueId = userRef.push().getKey();
            if (uniqueId == null) {
                Log.e("DatabaseManager", "Failed to generate uniqueId for member: " + child.getName());
                return null;
            }
            child.setMyUid(uniqueId);
            updateMember(child);
        }

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
            String uniqueId = child.getMyUid();
            if (uniqueId == null) {
                uniqueId = userRef.push().getKey();
                child.setMyUid(uniqueId);
                updateMember(child);
            }
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
            String uniqueId = memberDetail.getMyUid();
            if (uniqueId == null) {
                uniqueId = userRef.push().getKey();
                memberDetail.setMyUid(uniqueId);
                updateMemberDetails(memberDetail);
            }
            DetailsFB detailsFB = new DetailsFB();
            detailsFB.setDetail(memberDetail.getDetailValue());
            detailsFB.setDetailName(memberDetail.getDetailName());
            detailsFB.setDetailType(memberDetail.getDetailType());
            detailsFBS.add(detailsFB);
        }
        return detailsFBS;
    }
}
