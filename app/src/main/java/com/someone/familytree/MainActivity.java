package com.someone.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.someone.familytree.TreeMenu.TreeMenuActivity;
import com.someone.familytree.database.DatabaseManager;
import com.someone.familytree.database.FamilyDatabase;
import com.someone.familytree.database.FamilyMember;
import com.someone.familytree.database.FamilyTreeTable;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String newTestTreeName = "testTree";

    String familyJson = "{\n" +
            "    \"name\": \"root\",\n" +
            "    \"children\": [\n" +
            "        {\n" +
            "            \"name\": \"child1\",\n" +
            "            \"children\": [\n" +
            "                {\n" +
            "                    \"name\": \"child1_1\",\n" +
            "                    \"children\": [\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_1\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"child1_2\",\n" +
            "                    \"children\": [\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        },\n" +
            "        {\n" +
            "            \"name\": \"child2\",\n" +
            "            \"children\": [\n" +
            "                {\n" +
            "                    \"name\": \"child2_1\",\n" +
            "                    \"children\": [\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": [\n" +
            "                                {\n" +
            "                                    \"name\": \"child1_1_2\",\n" +
            "                                    \"children\": []\n" +
            "                                },\n" +
            "                                {\n" +
            "                                    \"name\": \"child1_1_2\",\n" +
            "                                    \"children\": []\n" +
            "                                },\n" +
            "                                {\n" +
            "                                    \"name\": \"child1_1_2\",\n" +
            "                                    \"children\": []\n" +
            "                                }\n" +
            "                            ]\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"name\": \"child1_1_2\",\n" +
            "                            \"children\": []\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"child2_2\",\n" +
            "                    \"children\": []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"child1_1_2\",\n" +
            "                    \"children\": []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"child1_1_2\",\n" +
            "                    \"children\": []\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"child1_1_2\",\n" +
            "                    \"children\": []\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "}\n";

    FamilyDatabase familyDatabase;

    static class singleMember {
        String name;
        List<singleMember> children;
    }

    SingleMemberWI rootWI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Intent intent = new Intent(MainActivity.this, Authentication.class);
        startActivity(intent);
        finish();


//        familyDatabase = FamilyDatabase.getDatabase(this);
//        Thread thread = getThread();
//        thread.start();
    }

    private @NonNull Thread getThread() {
        singleMember root = new singleMember();
        root.name = "root";
        root.children = new ArrayList<>();
//
//        int depth = 5;
//
//        makeTree(root,depth);

        return new Thread(() -> {

            FamilyTreeTable familyTreeTable = new FamilyTreeTable(newTestTreeName);
            int treeId = (int) familyDatabase.familyDao().insertTree(familyTreeTable);

            int rootId = (int) familyDatabase.familyDao().insertMember(new FamilyMember(root.name, 0, treeId));

            rootWI = new SingleMemberWI(root.name, rootId, treeId);
            addAllMembers(root, rootId, treeId);

            convertToSingleMemberWI(rootWI, rootId, treeId);

            printAllMembers(treeId);
//            Button button = findViewById(R.id.button);
//            button.setOnClickListener(v -> {
//                Intent intent = new Intent(MainActivity.this, SketchActivity.class);
//                // put extra tree id
//                intent.putExtra("treeId", treeId);
//                startActivity(intent);
//            });
        });
    }

    private void makeTree(singleMember root, int depth) {
        if (depth == 0) {
            return;
        }
        singleMember child1 = new singleMember();
        child1.name = "child1";
        child1.children = new ArrayList<>();
        root.children.add(child1);

        singleMember child2 = new singleMember();
        child2.name = "child2";
        child2.children = new ArrayList<>();
        root.children.add(child2);

        makeTree(child1, depth - 1);
        makeTree(child2, depth - 1);
    }

    private void printAllMembersWI(SingleMemberWI rootWI) {
        if (rootWI == null) {
            return;
        }
        Log.d("FamilyMemberWI", rootWI.name + " " + rootWI.id);
        for (SingleMemberWI child : rootWI.children) {
            printAllMembersWI(child);
        }
    }

    private void convertToSingleMemberWI(SingleMemberWI rootWI, int parentId, int treeId) {
        List<FamilyMember> members = familyDatabase.familyDao().getChildren(parentId, treeId);

        for (FamilyMember member : members) {
            SingleMemberWI singleMemberWI = new SingleMemberWI(member.getName(), member.getId(), treeId);
            rootWI.addChildren(singleMemberWI);
            convertToSingleMemberWI(singleMemberWI, member.getId(), treeId);
        }
    }

    private void addAllMembers(singleMember member, int parentId, int treeId) {
        if (member == null) {
            return;
        }
        FamilyMember familyMember = new FamilyMember(member.name, parentId, treeId);

        long memberId = familyDatabase.familyDao().insertMember(familyMember);

        familyMember.setId((int) memberId);

        for (singleMember child : member.children) {
            addAllMembers(child, familyMember.getId(), treeId);
        }
    }

    private void printAllMembers(int treeId) {
        List<FamilyMember> members = familyDatabase.familyDao().getAllMembers(treeId);
        for (FamilyMember member : members) {
            Log.d("FamilyMember", member.getName() + " " + member.getId() + " " + member.getParentId());
        }
    }
}