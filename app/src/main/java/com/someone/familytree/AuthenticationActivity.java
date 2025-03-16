package com.someone.familytree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.someone.familytree.TreeMenu.TreeMenuActivity;
import com.someone.familytree.connection.Authentication.Authentication;
import com.someone.familytree.database.DatabaseManager;

import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {
    private Authentication mAuth;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonSignup;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mAuth = Authentication.getInstance();

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

            email = email.trim();
            password = password.trim();

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!email.matches(emailPattern)) {
                editTextEmail.setError("Invalid email address");
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            goToMenu();
                            Log.d("AUTH", "onCreate: " + task.getResult());
                            Toast.makeText(AuthenticationActivity.this, "Signup successful"+ task.getResult().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AuthenticationActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Login button action
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            email = email.trim();
            password = password.trim();

            if (email.isEmpty()) {
                editTextEmail.setError("Email is required");
                return;
            }
            if (password.isEmpty()) {
                editTextPassword.setError("Password is required");
                return;
            }
            if (password.length() < 6) {
                editTextPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!email.matches(emailPattern)) {
                editTextEmail.setError("Invalid email address");
                return;
            }

            mAuth.signInUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            goToMenu();
                        } else {
                            Objects.requireNonNull(task.getException()).printStackTrace();
                            Toast.makeText(AuthenticationActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void goToMenu(){
        DatabaseManager.init(this);
        DatabaseManager.updateMetaData().addOnCompleteListener((task) -> {
            if (task.isSuccessful()) {
                Log.d("AUTH", "onCreate: " + task.getResult());
                Toast.makeText(this, "Metadata updated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("AUTH", "onCreate: " + task.getException());
                Toast.makeText(this, "Metadata update failed", Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent = new Intent(this, TreeMenuActivity.class);
        startActivity(intent);
        finish();
//        Intent intent = new Intent(this, TreeMenuActivity.class);
//        startActivity(intent);
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

//    private void signUpSetup(FirebaseUser user) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        assert user != null;
//        DatabaseReference userRef = database.getReference("users").child(user.getUid());
//        NewUserTemplete newUser = new NewUserTemplete(user.getEmail(), false);
//        userRef.setValue(newUser).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                goToMenu();
//            }
//        });
//    }
}