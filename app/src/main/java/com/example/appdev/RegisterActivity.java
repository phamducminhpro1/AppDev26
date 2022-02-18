package com.example.appdev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editPasswordConfirm;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
        editPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
    }

    public void onRegister(View view) {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String passwordConfirm = editPasswordConfirm.getText().toString().trim();

        if (email.isEmpty()) {
            editEmail.setError("E-mail address required!");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password required!");
            editPassword.requestFocus();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            editPassword.setError("Passwords are not the same!");
            editPassword.requestFocus();
            return;
        }

        // Regex from:
        // https://stackoverflow.com/questions/40336374/how-do-i-check-if-a-java-string-contains-at-least-one-capital-letter-lowercase
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            editPassword.setError("Password should be at least 8 characters, contain a number, and both uppercase and lowercase letters!");
            editPassword.requestFocus();
            return;
        }

        Intent intent = new Intent(this, LoginActivity.class);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to register, try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }

                    }
                });

    }
}