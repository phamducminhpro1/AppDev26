package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private  FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, JobsActivity.class));
            finish();
        }
    }

    public void onLogin(View view) {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

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

        Intent intent = new Intent(this, JobsActivity.class);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Failed to login, try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void goToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}