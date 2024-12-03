package com.someone.familytree;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FamilyMemberDao {
    // Insert and return the generated ID
    @Insert
    long insert(FamilyMember member); // Returns long for generated ID

    // Get children by parentId
    @Query("SELECT * FROM family_tree WHERE parentId = :i")
    List<FamilyMember> getChildren(int i);

    // Delete all family members
    @Query("DELETE FROM family_tree")
    void deleteAll();

    // Get all members
    @Query("SELECT * FROM family_tree")
    List<FamilyMember> getAllMembers();

    @Query("SELECT * FROM family_tree WHERE id = :id")
    FamilyMember getMember(int id);

    @Query("UPDATE family_tree SET parentId = :id1 WHERE id = :id")
    void updateParentId(int id, int id1);
}
