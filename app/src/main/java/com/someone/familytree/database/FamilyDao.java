package com.someone.familytree.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FamilyDao {
    // Insert and return the generated ID
    @Insert
    long insertMember(FamilyMember member);

    @Insert
    long insertTree(FamilyTreeTable tree);

    // Get all family trees
    @Query("SELECT * FROM family_tree")
    List<FamilyTreeTable> getAllTrees();

    // Get treeName
    @Query("SELECT treeName FROM family_tree WHERE id = :id")
    String getTreeName(int id);

    // Get children by parentId
    @Query("SELECT * FROM family_tree_members WHERE parentId = :i AND treeId = :treeId")
    List<FamilyMember> getChildren(int i, int treeId);

    // Delete all family members
    @Query("DELETE FROM family_tree_members WHERE treeId = :treeId")
    void deleteAll(int treeId);

    @Query("DELETE FROM family_tree_members WHERE id = :id AND treeId = :treeId")
    void deleteMember(int id, int treeId);

    // Get all members
    @Query("SELECT * FROM family_tree_members where treeId = :treeId")
    List<FamilyMember> getAllMembers(int treeId);

    @Query("SELECT * FROM family_tree_members WHERE id = :id")
    FamilyMember getMember(int id);

    @Query("UPDATE family_tree_members SET parentId = :id1 WHERE id = :id AND treeId = :treeId")
    void updateParentId(int id, int id1, int treeId);

    @Query("DELETE FROM family_tree WHERE id = :id")
    void deleteTree(int id);

    @Update
    void updateTree(FamilyTreeTable familyTreeTable);

    @Query("SELECT * FROM member_details WHERE personId = :memberId AND treeId = :treeId")
    List<MemberDetails> getMemberDetails(int memberId, int treeId);

    @Insert
    void insertMemberDetails(MemberDetails memberDetails);

    @Update
    void updateMemberDetails(MemberDetails memberDetail);

    @Update
    void updateMember(FamilyMember familyMember);

    @Delete
    void deleteMemberDetails(MemberDetails memberDetails);

    @Query("DELETE FROM member_details WHERE personId = :id AND treeId = :treeId")
    void deleteMemberDetails(int id, int treeId);

    @Delete
    void deleteMember(FamilyMember familyMember);

    @Query("SELECT * FROM family_tree_members WHERE personUid = :personUid AND treeId = :treeId")
    FamilyMember getMemberByUid(String personUid, int treeId);
}
