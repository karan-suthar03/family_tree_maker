package com.someone.familytree.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FamilyDao {
    // Insert and return the generated ID
    @Insert
    long insertMember(FamilyMember member); // Returns long for generated ID

    @Insert
    long insertTree(FamilyTreeTable tree); // Returns long for generated ID

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
}
