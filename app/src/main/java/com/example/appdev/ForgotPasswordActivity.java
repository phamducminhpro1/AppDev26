package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// This activity allows users to pick a new password after giving their e-mail address.
// They will receive an e-mail with a link where they can set their new password.
public class ForgotPasswordActivity extends AppCompatActivity {
    // Field to give their e-mail address.
    private EditText emailEditText;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        emailEditText = (EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void resetPassword(View v){
        String email = emailEditText.getText().toString().trim();

        // Make sure the user fills out an email address
        if(email.isEmpty()){
            emailEditText.setError("Email required");
            emailEditText.requestFocus();
            return;
        }

        // Make sure their e-mail address is in the right format.
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Email not in database, please try again");
            emailEditText.requestFocus();
            return;
        }

        // Show the progress bar.
        progressBar.setVisibility(View.VISIBLE);

        // Ask FireBaseAuth to send out the email
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // We successfully send the email
                    Toast.makeText(ForgotPasswordActivity.this, "Reset request is in your mailbox!", Toast.LENGTH_SHORT).show();
                } else {
                    // We failed to send the email
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong, try again", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}