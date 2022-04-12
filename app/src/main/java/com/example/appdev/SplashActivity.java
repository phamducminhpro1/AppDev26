package com.example.appdev;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
 This screen is show when the app is booting up.
 It's a placeholder screen while we are checking if there is a user already logged in.
 */
public class SplashActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                redirectUser();
            }
        });
    }

    // Redirect the user to the correct page depending on their account type.
    public void redirectUser() {
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            reference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    // If the user already has existing info, we load that in.
                    if (userProfile != null) {
                        // Send the user to the correct activity based on their account type.
                        if (userProfile.accountType == User.AccountType.RECRUITER) {
                            startActivity(new Intent(SplashActivity.this, RecruiterActivity.class));
                        } else if (userProfile.accountType == User.AccountType.STUDENT) {
                            startActivity(new Intent(SplashActivity.this, StudentActivity.class));
                        }

                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            // If there is no user logged in, send them to the login page.
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }
}