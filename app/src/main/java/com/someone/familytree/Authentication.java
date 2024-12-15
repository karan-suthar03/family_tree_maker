package com.someone.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.someone.familytree.TreeMenu.TreeMenuActivity;
import com.someone.familytree.database.DatabaseManager;

public class Authentication extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);


        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            goToMenu();
        }

        // Find views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Signup button action
        buttonSignup.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Authentication.this, "Signup successful! Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                goToMenu();
                            } else {
                                Toast.makeText(Authentication.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(Authentication.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });

        // Login button action
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(Authentication.this, "Login successful! Welcome, " + user.getEmail(), Toast.LENGTH_SHORT).show();
                                signUpSetup(user);
                            } else {
                                Toast.makeText(Authentication.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(Authentication.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            }
        });
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

        DatabaseManager.init(this, mAuth.getCurrentUser());

        Intent intent = new Intent(this, TreeMenuActivity.class);
        startActivity(intent);
    }

}