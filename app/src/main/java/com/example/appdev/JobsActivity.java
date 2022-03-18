package com.example.appdev;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class JobsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);

        mAuth = FirebaseAuth.getInstance();

    }

    public void onLogout(View view) {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void onEditPersonalInfo(View view) {
        startActivity(new Intent(this, PersonalInfoActivity.class));
        finish();
    }

    public void onStudentButton(View view){
        startActivity(new Intent(this, StudentActivity.class));
        finish();
    }

}

