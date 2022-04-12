package com.example.appdev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// This activity allows the user to login
public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        editEmail = findViewById(R.id.editTextEmail);
        editPassword = findViewById(R.id.editTextPassword);
    }

    // Attempts to login the user.
    public void onLogin(View view) {
        // Get their credentials from the text fields.
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Check that their email is not empty.
        if (email.isEmpty()) {
            editEmail.setError("E-mail address required!");
            editEmail.requestFocus();
            return;
        }

        // Check that their password is not empty.
        if (password.isEmpty()) {
            editPassword.setError("Password required!");
            editPassword.requestFocus();
            return;
        }

        // Attempt to sign them in.
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Successfully signed in, bring them to the splash activity.
                    // This will then redirect them to the recruiter or student activity.
                    startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                    finish();
                } else {
                    // Failed to login, notify the user the login failed.
                    Toast.makeText(LoginActivity.this, "Failed to login, try again!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Brings the user to the register page.
    public void goToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // Brings the user to the Forgot Password page.
    public void onForgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    // Redirects the user to facebook.
    public void toFacebook(View view) {
        android.widget.Button UrlOpen = findViewById(R.id.button);

        UrlOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GetIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.facebook.com"));
                startActivity(GetIntent);
            }
        });
    }

    // Redirects the user to the TUE page.
    public void toTUE(View view) {
        android.widget.Button UrlOpen = findViewById(R.id.button2);

        UrlOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent GetIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.tue.nl"));
                startActivity(GetIntent);
            }
        });
    }


}