package com.someone.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.someone.familytree.database.DatabaseManager;

import java.util.Objects;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            Toast.makeText(this, "Welcome back, " + mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
            goToMenu();
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
//        finish();
    }

    static class NewUserTemplete {
        String email;
        boolean isPremium;

        public NewUserTemplete() {
            this.email = "";
            this.isPremium = false;
        }

        public NewUserTemplete(String email, boolean isPremium) {
            this.email = email;
            this.isPremium = isPremium;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isPremium() {
            return isPremium;
        }
    }

    private void signUpSetup(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;
        DatabaseReference userRef = database.getReference("users").child(user.getUid());
        NewUserTemplete newUser = new NewUserTemplete(user.getEmail(), false);
        userRef.setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                goToMenu();
            }
        });
    }

    private void goToMenu(){

        DatabaseManager.init(this, Objects.requireNonNull(mAuth.getCurrentUser()));
//
//        Intent intent = new Intent(this, TreeMenuActivity.class);
//        startActivity(intent);
    }

}