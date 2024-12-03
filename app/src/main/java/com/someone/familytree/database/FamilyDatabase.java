package com.someone.familytree.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FamilyMember.class, FamilyTreeTable.class}, version = 1)
public abstract class FamilyDatabase extends RoomDatabase {

    private static volatile FamilyDatabase INSTANCE;

    public static FamilyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FamilyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            FamilyDatabase.class,
                            "family_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract FamilyDao familyDao();
}