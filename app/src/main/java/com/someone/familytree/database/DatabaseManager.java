package com.someone.familytree.database;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DatabaseReference;
import com.someone.familytree.connection.MyDatabase;

import java.util.List;

public class DatabaseManager {
    public static FamilyDatabase familyDatabase;
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


    public static List<FamilyMember> getAllMembers(int treeId) {
        return familyDatabase.familyDao().getAllMembers(treeId);
    }

    public static FamilyMember getMemberByUid(String memberUid, long treeId){
        return familyDatabase.familyDao().getMemberByUid(memberUid, (int) treeId);
    }

    public static void increaseTreeVersion(int treeId) {
        FamilyTreeTable familyTreeTable = familyDatabase.familyDao().getTree(treeId);
        if (familyTreeTable == null) {
            return;
        }
        TreeMetaOffline treeMetaOffline = familyDatabase.familyDao().getTreeMetaOffline(familyTreeTable.getId());
        treeMetaOffline.setTreeVersionOffline(treeMetaOffline.getTreeVersionOffline() + 1);
        familyDatabase.familyDao().updateTreeMetaOffline(treeMetaOffline);
    }

    public static long insertTree(FamilyTreeTable familyTreeTable) {
        long id = familyDatabase.familyDao().insertTree(familyTreeTable);
        TreeMetaOffline treeMetaOffline = new TreeMetaOffline(familyTreeTable.getTreeName());
        treeMetaOffline.setUid(familyTreeTable.getUid());
        treeMetaOffline.setTreeId((int) id);
        familyDatabase.familyDao().insertTreeMetaOffline(treeMetaOffline);
        return id;
    }

    public static void deleteTree(int id) {
        familyDatabase.familyDao().deleteTree(id);
        TreeMetaOffline treeMetaOffline = familyDatabase.familyDao().getTreeMetaOfflineByTreeId(id);
        treeMetaOffline.setDeleted(true);
        familyDatabase.familyDao().updateTreeMetaOffline(treeMetaOffline);
    }

    public static void deleteAllMembers(Integer id) {
        familyDatabase.familyDao().deleteAll(id);
        increaseTreeVersion(id);
    }

    public static void updateTree(FamilyTreeTable familyTreeTable) {
        familyDatabase.familyDao().updateTree(familyTreeTable);
        increaseTreeVersion(familyTreeTable.getId());
    }

    public static String getTreeName(int id) {
        return familyDatabase.familyDao().getTreeName(id);
    }

    public static List<FamilyMember> getChildren(int id, Integer treeId) {
        return familyDatabase.familyDao().getChildren(id, treeId);
    }

    public static long insertMember(FamilyMember familyMember) {
        increaseTreeVersion(familyMember.getTreeId());
        return familyDatabase.familyDao().insertMember(familyMember);
    }

    public static void updateMember(FamilyMember familyMember) {
        increaseTreeVersion(familyMember.getTreeId());
        familyDatabase.familyDao().updateMember(familyMember);
    }

    public static FamilyMember getMember(int id) {
        return familyDatabase.familyDao().getMember(id);
    }

    public static List<MemberDetails> getMemberDetails(int id, int treeId) {
        return familyDatabase.familyDao().getMemberDetails(id, treeId);
    }

    public static void updateMemberDetails(MemberDetails memberDetail) {
        increaseTreeVersion(memberDetail.getTreeId());
        familyDatabase.familyDao().updateMemberDetails(memberDetail);
    }

    public static void insertMemberDetails(MemberDetails memberDetails) {
        increaseTreeVersion(memberDetails.getTreeId());
        familyDatabase.familyDao().insertMemberDetails(memberDetails);
    }

    public static void deleteMember(int id, int treeId) {
        increaseTreeVersion(treeId);
        familyDatabase.familyDao().deleteMember(id, treeId);
    }

    public static void deleteMemberDetails(int id, int treeId) {
        increaseTreeVersion(treeId);
        familyDatabase.familyDao().deleteMemberDetails(id, treeId);
    }

    public static FamilyTreeTable getTreeByUid(String treeUid) {
        return familyDatabase.familyDao().getTreeByUid(treeUid);
    }

    public static void updateParentId(int id, int newParentId, int treeId) {
        increaseTreeVersion(treeId);
        familyDatabase.familyDao().updateParentId(id, newParentId, treeId);
    }

    public static TreeMetaOnline getTreeMetaOnline(String treeUid) {
        return familyDatabase.familyDao().getTreeMetaOnline(treeUid);
    }

    public static void insertTreeMetaOnline(TreeMetaOnline treeMetaOnline) {
        familyDatabase.familyDao().insertTreeMetaOnline(treeMetaOnline);
    }

    public static void updateTreeMetaOnline(TreeMetaOnline treeMetaOnline) {
        familyDatabase.familyDao().updateTreeMetaOnline(treeMetaOnline);
    }

    public static List<TreeMetaOffline> getTreeMeta(int id) {
        return familyDatabase.familyDao().getTreeMeta(id);
    }
    public static void init(Activity authentication) {
        familyDatabase = FamilyDatabase.getDatabase(authentication);
    }


    public static Task<Object> updateAllTreesFromServer() {
        MyDatabase myDatabase = MyDatabase.getInstance();
        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();

        myDatabase.getAllTrees().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                new Thread(() -> {
                    Log.d("DatabaseManager", "updateAllTreesFromServer: task.getResult().toString(): " + task.getResult().toString());
                    DataSync.updateDatabaseFromServer(task.getResult().toString());
                    taskCompletionSource.setResult("Trees updated successfully");
                }).start();
            } else {
                taskCompletionSource.setException(task.getException());
            }
        });
        return taskCompletionSource.getTask();
    }
    public static Task<Object> updateAllTreesFromDevice() {
        MyDatabase myDatabase = MyDatabase.getInstance();
        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();

        new Thread(() -> {
            String jsonToSend = DataSync.updateServerFromDevice();
            Log.d("DatabaseManager", "updateAllTreesFromDevice: jsonToSend: " + jsonToSend);
            myDatabase.updateAllTreesOnServer(jsonToSend).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    taskCompletionSource.setResult("Trees updated successfully");
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            });
        }).start();

        return taskCompletionSource.getTask();
    }

    public static Task<Object> updateMetaData() {
        TaskCompletionSource<Object> completionSource = new TaskCompletionSource<>();
        MyDatabase myDatabase = MyDatabase.getInstance();
        myDatabase.updateMetaData().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                new Thread(() -> {
                    completionSource.setResult(task.getResult());
                    DataSync.updateMetadataFromServer(task.getResult().toString());
                }).start();
            } else {
                completionSource.setException(task.getException());
            }
        });
        return completionSource.getTask();
    }


    public static List<TreeMetaOffline> getAllOfflineTrees() {
        return familyDatabase.familyDao().getAllOfflineTrees();
    }

    public static FamilyTreeTable getFamilyTreeTable(int treeId) {
        return familyDatabase.familyDao().getTree(treeId);
    }

    public static List<TreeMetaOnline> getAllOnlineTrees() {
        return familyDatabase.familyDao().getAllOnlineTrees();
    }

    public static Task<Object> uploadTree(FamilyTreeTable familyTreeTable) {
        TaskCompletionSource<Object> taskCompletionSource = new TaskCompletionSource<>();
        MyDatabase myDatabase = MyDatabase.getInstance();
        new Thread(() -> {
            String jsonToSend = DataSync.uploadTree(familyTreeTable);
            Log.d("haha", "uploadTree: jsonToSend: " + jsonToSend);
            myDatabase.uploadTreeToServer(jsonToSend).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    taskCompletionSource.setResult("Tree uploaded successfully");
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            });
        }).start();
        return taskCompletionSource.getTask();
    }
}
