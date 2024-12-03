package com.someone.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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

        familyDatabase = FamilyDatabase.getDatabase(this);
        Thread thread = getThread();
        thread.start();


    }

    private @NonNull Thread getThread() {
        Gson gson = new Gson();
        singleMember root = gson.fromJson(familyJson, singleMember.class);


        return new Thread(() -> {

//            familyDatabase.familyMemberDao().deleteAll();
//
//            addAllMembers(root, 0);

            printAllMembers();
            Button button = findViewById(R.id.button);
            button.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SketchActivity.class);
                startActivity(intent);
            });

        });
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

    private void convertToSingleMemberWI(SingleMemberWI rootWI, int parentId) {
        List<FamilyMember> members = familyDatabase.familyMemberDao().getChildren(parentId);

        for (FamilyMember member : members) {
            SingleMemberWI singleMemberWI = new SingleMemberWI(member.getName(), member.getId());
            rootWI.addChildren(singleMemberWI);
            convertToSingleMemberWI(singleMemberWI, member.getId());
        }
    }

    private void addAllMembers(singleMember member, int parentId) {
        if (member == null) {
            return;
        }
        FamilyMember familyMember = new FamilyMember(member.name, parentId);

        long memberId = familyDatabase.familyMemberDao().insert(familyMember);

        familyMember.setId((int) memberId);

        for (singleMember child : member.children) {
            addAllMembers(child, familyMember.getId());
        }
    }

    private void printAllMembers() {
        List<FamilyMember> members = familyDatabase.familyMemberDao().getAllMembers();
        for (FamilyMember member : members) {
            Log.d("FamilyMember", member.getName() + " " + member.getId() + " " + member.getParentId());
        }
    }
}